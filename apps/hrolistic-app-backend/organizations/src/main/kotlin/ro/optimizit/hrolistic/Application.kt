package ro.optimizit.hrolistic

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("ro.optimizit.hrolistic")
                .mainClass(Application.javaClass)
                .start()
    }
}