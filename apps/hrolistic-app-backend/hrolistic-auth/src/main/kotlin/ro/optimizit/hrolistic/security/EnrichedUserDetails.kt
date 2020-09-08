package ro.optimizit.hrolistic.security

import io.micronaut.security.authentication.UserDetails


class EnrichedUserDetails(username: String?, roles: List<String>?,val permissions: List<String>?) : UserDetails(username, roles)