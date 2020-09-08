package ro.optimizit.hrolistic.security

import com.nimbusds.jwt.JWTClaimsSet
import io.micronaut.context.annotation.Replaces
import io.micronaut.security.authentication.UserDetails
import io.micronaut.security.token.config.TokenConfiguration
import io.micronaut.security.token.jwt.generator.claims.ClaimsAudienceProvider
import io.micronaut.security.token.jwt.generator.claims.JWTClaimsSetGenerator
import io.micronaut.security.token.jwt.generator.claims.JwtIdGenerator
import javax.inject.Singleton

@Singleton
@Replaces(JWTClaimsSetGenerator::class)
class CustomJWTClaimsSetGenerator(tokenConfiguration: TokenConfiguration?, jwtIdGenerator: JwtIdGenerator?, claimsAudienceProvider: ClaimsAudienceProvider?) : JWTClaimsSetGenerator(tokenConfiguration, jwtIdGenerator, claimsAudienceProvider) {

    override fun populateWithUserDetails(builder: JWTClaimsSet.Builder, userDetails: UserDetails) {
        super.populateWithUserDetails(builder, userDetails)
        if (userDetails is EnrichedUserDetails) {
            builder.claim("permissions", userDetails.permissions)
        }
    }
}