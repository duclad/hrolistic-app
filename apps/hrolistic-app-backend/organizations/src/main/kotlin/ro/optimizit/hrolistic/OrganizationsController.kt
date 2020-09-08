package ro.optimizit.hrolistic

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.HttpStatus

@Controller("/organizations")
class OrganizationsController {

    @Get("/")
    fun index(): HttpStatus {
        return HttpStatus.OK
    }
}