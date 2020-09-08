//package ro.optimizit.hrolistic.web
//
//import io.micronaut.http.HttpRequest
//import io.micronaut.http.HttpStatus
//import io.micronaut.http.client.RxHttpClient
//import io.micronaut.http.client.annotation.Client
//import io.micronaut.http.client.exceptions.HttpClientResponseException
//import io.micronaut.security.authentication.UsernamePasswordCredentials
//import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
//import io.micronaut.test.annotation.MicronautTest
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertThrows
//import org.junit.jupiter.api.Test
//import javax.inject.Inject
//
//
//@MicronautTest
//class PeopleControllerTest {
//
//    @Inject
//    @Client("/")
//    lateinit var client: RxHttpClient
//
//    @Test
//    fun testUserEndpointIsSecured() {
//        val thrown: HttpClientResponseException = assertThrows(HttpClientResponseException::class.java) { client.toBlocking().exchange<Any, Any>(HttpRequest.GET("/user")) }
//        assertEquals(HttpStatus.UNAUTHORIZED, thrown.response.status)
//    }
//
//    @Test
//    fun testAuthenticatedCanFetchUsername() {
//        val credentials = UsernamePasswordCredentials("sherlock", "password")
//        val request = HttpRequest.POST("/login", credentials)
//        val bearerAccessRefreshToken = client.toBlocking().retrieve(request, BearerAccessRefreshToken::class.java)
//        val username = client.toBlocking().retrieve(HttpRequest.GET<Any>("/user")
//                .header("Authorization", "Bearer " + bearerAccessRefreshToken.accessToken), String::class.java)
//        assertEquals("sherlock", username);
//
//    }
//
//}