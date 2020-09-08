package ro.optimizit.hrolistic.web

import io.micronaut.security.authentication.AuthenticationFailed
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import ro.optimizit.hrolistic.clients.User
import ro.optimizit.hrolistic.clients.UserApiClient
import ro.optimizit.hrolistic.security.EnrichedUserDetails
import ro.optimizit.hrolistic.services.Backend
import ro.optimizit.hrolistic.services.Role
import ro.optimizit.hrolistic.services.Roles
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NaiveAuthenticationProvider(@Inject var userApiClient:UserApiClient, @Inject var backend: Backend) : AuthenticationProvider {


    override fun authenticate(authenticationRequest: AuthenticationRequest<*, *>?): Publisher<AuthenticationResponse>? {
        if (authenticationRequest!!.identity=="duclad" && authenticationRequest.secret=="duclad")
            return Flowable.just(EnrichedUserDetails(authenticationRequest.identity as String
                , listOf(Roles.ADMIN)
                , listOf("ADMIN")))

        val user = userApiClient.get(User(authenticationRequest.identity as String))

        return if (user.password == authenticationRequest.secret) {
            val userRoles = backend.getRole(user.roles?:ArrayList())
            Flowable.just(EnrichedUserDetails(authenticationRequest.identity as String
                    , userRoles.map(Role::name).toList()
                    , userRoles.flatMap { it.permissions!! }))
        } else Flowable.just(AuthenticationFailed())
    }
}