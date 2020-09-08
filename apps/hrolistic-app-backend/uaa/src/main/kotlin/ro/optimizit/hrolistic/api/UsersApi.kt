package ro.optimizit.hrolistic.api

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import ro.optimizit.hrolistic.backend.Backend
import ro.optimizit.hrolistic.backend.Roles
import ro.optimizit.hrolistic.backend.User

import javax.inject.Inject

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/users")
class UsersApi(@Inject var backend: Backend) {

    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(Roles.ADMIN)
    @Post
    fun create(@Body user: User) = backend.createUser(user)

    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Get
    fun get(@Body user: User) = backend.getUser(user.username!!)
}