package ro.optimizit.hrolistic.web

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured
import io.reactivex.Single

@Controller("/people")
class PeopleController {
    @Secured("isAuthenticated()")
    @Produces(MediaType.TEXT_PLAIN)
    @Get("/{uuid}")
    fun getPerson(@PathVariable uuid: String): Single<String> {
        return Single.just("Gogu")
    }
}
