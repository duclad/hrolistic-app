package ro.optimizit.hrolistic.backend

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


data class User(val username: String? = null, val email: String? = null, val firstName: String? = null, val lastName: String? = null,
                val image: ByteArray? = null, val password: CharArray? = null, val activated: Boolean = false, val langKey: String? = null,
                val activationKey: String? = null, val resetKey: String? = null, val createdBy: String? = null, val creationDate: LocalDateTime = LocalDateTime.now(),
                val resetDate: LocalDateTime? = null, val lastModifiedBy: String? = null, val lastModifiedDate: LocalDateTime? = null,
                val roles: List<String>?)


@Singleton
class Backend(@Value("\${mongodb.uri}") var mongoURI: String) {

    private val client = KMongo.createClient(MongoClientURI(mongoURI))
    private val database = client.getDatabase("hrolistic")
    private val usersCollection = database.getCollection<User>()
    private val rolesCollection = database.getCollection<Role>()

    fun getRoles() = rolesCollection.find().toList()

    fun createRole(role: Role) = rolesCollection.insertOne(role)

    fun getRole(roleName: List<String>) = rolesCollection.find(Role::name `in` roleName).toList()

    fun deleteRole(roleName: String) = rolesCollection.deleteOne(Role::name eq roleName)


    fun getUsers() = usersCollection.find().toList()

    fun createUser(user: User) = usersCollection.insertOne(user)

    fun getUser(username: String) = usersCollection.findOne(User::username eq username)


}