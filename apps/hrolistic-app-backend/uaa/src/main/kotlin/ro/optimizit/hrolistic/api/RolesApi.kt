package ro.optimizit.hrolistic.api

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import ro.optimizit.hrolistic.backend.Backend
import ro.optimizit.hrolistic.backend.Role
import javax.inject.Inject

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/roles")
class RolesApi(@Inject var backend: Backend) {

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Post
    fun createRole(@Body role: Role) = backend.createRole(role)
}