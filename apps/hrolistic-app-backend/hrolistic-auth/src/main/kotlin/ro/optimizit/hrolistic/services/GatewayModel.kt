package ro.optimizit.hrolistic.services

import com.mongodb.MongoClientURI
import io.micronaut.context.annotation.Value
import org.litote.kmongo.*
import java.time.LocalDateTime
import javax.inject.Singleton
import kotlin.collections.toList

object Roles {
    const val ADMIN = "ADMIN"
    const val HR_MANAGER = "HR_MANAGER"
    const val PROJECT_MANAGER = "PROJECT_MANAGER"
}

object Permissions {
    const val MANAGE_TASKS="MANAGE_TASKS"
}

data class Role(val name: String, var active: Boolean = false, var permissions: List<String>?) {
    constructor(name: String) : this(name, false, null)
}



@Singleton
class Backend(@Value("\${mongodb.uri}") var mongoURI: String) {

    private val client = KMongo.createClient(MongoClientURI(mongoURI))
    private val database = client.getDatabase("hrolistic")
    private val rolesCollection = database.getCollection<Role>()

    fun getRoles() = rolesCollection.find().toList()

    fun createRole(role: Role) = rolesCollection.insertOne(role)

    fun getRole(roleName: List<String>) = rolesCollection.find(Role::name `in` roleName).toList()

    fun deleteRole(roleName: String) = rolesCollection.deleteOne(Role::name eq roleName)




}