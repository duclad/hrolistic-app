package ro.optimizit.hrolistic

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
        info = Info(
                title = "Employees Management",
                version = "1.0",
                description = "Employee API",
                contact = Contact(
                        name = "Claudiu Dumitrescu",
                        email = "claudiu.dumitrescu@gmail.com")
        )
)
object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("ro.optimizit.hrolistic")
                .mainClass(Application.javaClass)
                .start()
    }
}