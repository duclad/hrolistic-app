package ro.optimizit.hrolistic.clients

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

data class User(val username: String? = null, val password: CharArray? = null, val roles: List<String>?= emptyList())

@Client(id = "users")
interface UserApiClient {
    @Get
    fun get(@Body user: User): User
}