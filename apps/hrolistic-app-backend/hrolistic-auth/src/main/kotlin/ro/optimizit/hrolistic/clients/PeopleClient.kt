package ro.optimizit.hrolistic.clients

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client
import io.reactivex.Single

@Client(id = "people")
interface PeopleClient {
    @Get("/people/{uuid}")
    fun getPerson(@PathVariable uuid: String): Single<String>
}