//package ro.optimizit.hrolistic.web
//
//import io.micronaut.context.annotation.Requires
//import io.micronaut.context.env.Environment
//import io.reactivex.Single
//import javax.inject.Singleton
//
//@Requires(env = [Environment.TEST])
//@Singleton
//class UsernameFetcherImplementation:UsernameFetcher{
//
//    override fun findUsername(): Single<String?>? = Single.just("sherlock")
//
//}