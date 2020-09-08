package ro.optimizit.hrolistic

import io.micronaut.context.BeanContext
import io.micronaut.context.ExecutionHandleLocator
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Executable
import io.micronaut.context.annotation.Parameter
import io.micronaut.core.io.buffer.ByteBuffer
import io.micronaut.core.io.buffer.ReferenceCounted
import io.micronaut.core.util.StringUtils
import io.micronaut.http.*
import io.micronaut.http.client.RxStreamingHttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.simple.SimpleHttpRequestFactory
import io.micronaut.http.simple.SimpleHttpResponseFactory
import io.micronaut.web.router.DefaultRouteBuilder
import io.micronaut.web.router.RouteBuilder.UriNamingStrategy
import io.reactivex.Flowable
import io.reactivex.processors.UnicastProcessor
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.Collectors.*
import javax.inject.Inject
import javax.inject.Singleton


@EachProperty("proxynaut")
class ProxyConfiguration(@param:Parameter val name: String) {
    var timeoutMs = 30000
    var context: String? = null
    var uri: URI? = null
        private set
    private var allowedMethods = ALL_METHODS
    private var includeRequestHeaders: Collection<String> = emptySet()
    private var excludeRequestHeaders: Collection<String> = emptySet()
    private var includeResponseHeaders: Collection<String> = emptySet()
    private var excludeResponseHeaders: Collection<String> = emptySet()
    var url: URL? = null
        private set

    @Throws(MalformedURLException::class)
    fun setUri(uri: URI) {
        this.uri = uri
        url = uri.toURL()
    }

    fun getAllowedMethods(): Collection<String> {
        return allowedMethods.stream().map { obj: HttpMethod -> obj.name }.collect(toSet())
    }

    fun setAllowedMethods(allowedMethods: Collection<String>) {
        if (allowedMethods.contains(ASTERISK)) {
            this.allowedMethods = ALL_METHODS
        } else {
            this.allowedMethods = allowedMethods.stream()
                    .map { s: String -> safeUpper(s) }
                    .map { name: String -> HttpMethod.parse(name) }
                    .collect(toSet())
        }
    }

    fun shouldAllowMethod(method: HttpMethod): Boolean {
        return allowedMethods.contains(method)
    }

    fun getIncludeRequestHeaders(): Collection<String> {
        return Collections.unmodifiableCollection(includeRequestHeaders)
    }

    fun setIncludeRequestHeaders(values: Collection<String>) {
        includeRequestHeaders = upperCaseSet(values)
    }

    fun getExcludeRequestHeaders(): Collection<String> {
        return Collections.unmodifiableCollection(excludeRequestHeaders)
    }

    fun setExcludeRequestHeaders(values: Collection<String>) {
        excludeRequestHeaders = upperCaseSet(values)
    }

    fun shouldIncludeRequestHeader(headerName: String): Boolean {
        return if (!includeRequestHeaders.isEmpty()) {
            includeRequestHeaders.contains(safeUpper(headerName))
        } else if (!excludeRequestHeaders.isEmpty()) {
            !excludeRequestHeaders.contains(safeUpper(headerName))
        } else {
            true
        }
    }

    fun getIncludeResponseHeaders(): Collection<String> {
        return Collections.unmodifiableCollection(includeResponseHeaders)
    }

    fun setIncludeResponseHeaders(values: Collection<String>) {
        includeResponseHeaders = upperCaseSet(values)
    }

    fun getExcludeResponseHeaders(): Collection<String> {
        return Collections.unmodifiableCollection(excludeResponseHeaders)
    }

    fun setExcludeResponseHeaders(values: Collection<String>) {
        excludeResponseHeaders = upperCaseSet(values)
    }

    fun shouldIncludeResponseHeader(headerName: String): Boolean {
        return if (!includeResponseHeaders.isEmpty()) {
            includeResponseHeaders.contains(safeUpper(headerName))
        } else if (!excludeResponseHeaders.isEmpty()) {
            !excludeResponseHeaders.contains(safeUpper(headerName))
        } else {
            true
        }
    }

    private fun upperCaseSet(strings: Collection<String>): Set<String> {
        return strings.stream().map { s: String -> s.toUpperCase(Locale.ENGLISH) }.collect(toSet())
    }

    companion object {
        private const val ASTERISK = "*"
        private val ALL_METHODS = setOf(HttpMethod.OPTIONS, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
        private fun safeUpper(s: String): String {
            return s.toUpperCase(Locale.ENGLISH)
        }
    }

}


@Singleton
class Proxy(private val configs: Collection<ProxyConfiguration>, private val beanContext: BeanContext) : Closeable {
    private val proxyMap = Collections.synchronizedMap(HashMap<String, RxStreamingHttpClient>())

    @Executable
    @Throws(InterruptedException::class)
    fun serve(request: HttpRequest<ByteBuffer<*>?>, path: String?): HttpResponse<Flowable<ByteArray>> {
        var path = path
        if (path == null) {
            path = ""
        }
        val requestPath = request.path
        val proxyContextPath = requestPath.substring(0, requestPath.length - path.length)
        val config = findConfigForRequest(proxyContextPath)
        if (!config.isPresent) {
            // This should never happen, only if Micronaut's router somehow was confused
            val prefixes = configs.stream().map { c: ProxyConfiguration -> c.context }.collect(toList())
            LOG.warn("Matched " + request.method + " " + request.path + " to the proxy, but no configuration is found. Prefixes found in config: " + prefixes)
            return HttpResponse.status(HttpStatus.BAD_REQUEST, "Unknown proxy path: $proxyContextPath")
        }
        val upstreamRequest = buildRequest(request, path, config)
        val client = findOrCreateClient(config.get())
        LOG.info("About to pivot proxy call to " + config.get().uri + path)
        val upstreamResponseFlowable = client.exchangeStream(upstreamRequest).serialize()
        val futureResponse = buildResponse(config, upstreamResponseFlowable)
        return try {
            futureResponse[config.get().timeoutMs.toLong(), TimeUnit.MILLISECONDS]
        } catch (e: ExecutionException) {
            HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        } catch (e: TimeoutException) {
            LOG.info("Timeout occurred before getting upstream headers (configured to {} millisecond(s)", config.get().timeoutMs)
            HttpResponse.status(HttpStatus.BAD_GATEWAY)
        }
    }

    private fun buildResponse(config: Optional<ProxyConfiguration>,
                              upstreamResponseFlowable: Flowable<HttpResponse<ByteBuffer<*>>>): CompletableFuture<HttpResponse<Flowable<ByteArray>>> {
        val futureResponse = CompletableFuture<HttpResponse<Flowable<ByteArray>>>()
        val responseBodyFlowable = UnicastProcessor.create<ByteArray>()
        upstreamResponseFlowable.subscribe(object : Subscriber<HttpResponse<ByteBuffer<*>>> {
            private var subscription: Subscription? = null
            override fun onSubscribe(s: Subscription) {
                subscription = s
                s.request(1)
            }

            override fun onNext(upstreamResponse: HttpResponse<ByteBuffer<*>>) {
                if (LOG.isTraceEnabled) {
                    LOG.trace("************ Read Response from {}", upstreamResponse.body()!!.toString(StandardCharsets.UTF_8))
                }
                // When the upstream first first packet comes in, complete the response
                if (!futureResponse.isDone) {
                    LOG.info("Completed pivot: " + upstreamResponse.status)
                    val response = makeResponse(upstreamResponse, responseBodyFlowable, config)
                    futureResponse.complete(response as HttpResponse<Flowable<ByteArray>>?)
                }
                val byteBuffer = upstreamResponse.body()!!
                responseBodyFlowable.onNext(byteBuffer.toByteArray())
                if (byteBuffer is ReferenceCounted) (byteBuffer as ReferenceCounted).release()
                subscription!!.request(1)
            }

            override fun onError(t: Throwable) {
                if (t is HttpClientResponseException && !futureResponse.isDone) {
                    val upstreamException = t
                    LOG.info("HTTP error from upstream: " + upstreamException.status.reason)
                    val upstreamErrorResponse = upstreamException.response
                    val upstreamResponse = upstreamException.response as HttpResponse<ByteBuffer<*>>
                    LOG.info("Completed pivot: " + upstreamResponse.status)
                    val response = makeErrorResponse(upstreamResponse, config)
                    futureResponse.complete(response as HttpResponse<Flowable<ByteArray>>)
                } else {
                    LOG.info("Proxy got unknown error from upstream: " + t.message, t)
                    responseBodyFlowable.onError(t)
                }
            }

            override fun onComplete() {
                LOG.trace("Upstream response body done")
                responseBodyFlowable.onComplete()
            }
        })
        return futureResponse
    }

    private fun buildRequest(request: HttpRequest<ByteBuffer<*>?>, path: String,
                             config: Optional<ProxyConfiguration>): MutableHttpRequest<Any> {
        val originPath = config.get().uri!!.path + path
        val queryPart = request.uri.query
        val originUri = if (StringUtils.isEmpty(queryPart)) originPath else "$originPath?$queryPart"
        LOG.debug("Proxy'ing incoming " + request.method + " " + request.path + " -> " + originPath)
        val upstreamRequest = SimpleHttpRequestFactory.INSTANCE.create<Any>(request.method,
                originUri)
        val body: Optional<*> = request.body
        if (HttpMethod.permitsRequestBody(request.method)) {
            body.ifPresent { b: Any -> upstreamRequest.body(b) }
        }
        return upstreamRequest
    }

    protected fun makeResponse(upstreamResponse: HttpResponse<*>,
                               responseFlowable: Flowable<ByteArray>,
                               config: Optional<ProxyConfiguration>?): HttpResponse<*> {
        val httpResponse = SimpleHttpResponseFactory.INSTANCE.status(upstreamResponse.status, responseFlowable)
        upstreamResponse.contentType.ifPresent { mediaType: MediaType? -> httpResponse.contentType(mediaType) }
        return httpResponse
    }

    protected fun makeErrorResponse(upstreamResponse: HttpResponse<*>,
                                    config: Optional<ProxyConfiguration>?): HttpResponse<*> {
        val httpResponse = SimpleHttpResponseFactory.INSTANCE.status(upstreamResponse.status, Flowable.empty<ByteArray>())
        upstreamResponse.contentType.ifPresent { mediaType: MediaType? -> httpResponse.contentType(mediaType) }
        return httpResponse
    }

    private fun findOrCreateClient(config: ProxyConfiguration): RxStreamingHttpClient {
        return proxyMap.computeIfAbsent(config.name) { n: String? ->
            LOG.debug("Creating proxy for " + config.url)
            beanContext.createBean(RxStreamingHttpClient::class.java, config.url)
        }
    }

    private fun findConfigForRequest(prefix: String): Optional<ProxyConfiguration> {
        return configs.stream().filter { config: ProxyConfiguration -> config.context == prefix }.findFirst()
    }

    @Throws(IOException::class)
    override fun close() {
        proxyMap.values.forEach(Consumer { client: RxStreamingHttpClient -> client.stop() })
        proxyMap.clear()
    }

    companion object {
        protected val LOG = LoggerFactory.getLogger(Proxy::class.java)
    }

}

@Singleton
class ProxyRouteBuilder(executionHandleLocator: ExecutionHandleLocator?, uriNamingStrategy: UriNamingStrategy?) : DefaultRouteBuilder(executionHandleLocator, uriNamingStrategy) {
    @Inject
    fun buildProxyRoutes(configs: Collection<ProxyConfiguration>) {
        for (config in configs) {
            val contextPath = config.context + "{+path:?}"
            for (method in arrayOf(HttpMethod.DELETE, HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.OPTIONS)) {
                if (!config.shouldAllowMethod(method)) continue
                buildRoute(method, contextPath, Proxy::class.java, "serve", HttpRequest::class.java, String::class.java)
            }
        }
    }
}
