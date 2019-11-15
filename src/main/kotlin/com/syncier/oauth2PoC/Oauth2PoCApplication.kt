package com.syncier.oauth2PoC

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
class Oauth2PoCApplication {
    @Bean
    fun cmd(myController: MyController) = CommandLineRunner {
        println(myController.get())
    }

}

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
    fun keycloakClientRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId("keycloak")
            .clientId("zv-inventory-tu")
            .clientSecret("b06d28ab-989e-4cd7-8d0c-b781d0e6d106")
            .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
            .authorizationGrantType(AuthorizationGrantType.PASSWORD)
            .tokenUri("https://auth.marketplace.syncier.rocks/auth/realms/syncier-motor-registration/protocol/openid-connect/token")
            .clientName("KeyCloak")
            .build();
    }

    @Bean
    fun client(): OAuth2AuthorizedClient? {
        val providerBuilder = OAuth2AuthorizedClientProviderBuilder.builder().password().build()
        val oAuth2AuthorizationContext = OAuth2AuthorizationContext
                .withClientRegistration(keycloakClientRegistration())
                .attribute(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, "bwPCqSxqZJtqkCm9q3S2DHjNWzZbAse6")
                .attribute(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, "zv-inventory-test")
                .principal(object: Authentication {
                    override fun getAuthorities() = mutableListOf< SimpleGrantedAuthority>()

                    override fun setAuthenticated(isAuthenticated: Boolean) {
                    }

                    override fun getName() = "name"

                    override fun getCredentials() = "credentials"

                    override fun getPrincipal() = "principal"

                    override fun isAuthenticated() = true

                    override fun getDetails() = "details"
                })
                .build()
        return providerBuilder.authorize(oAuth2AuthorizationContext)
    }


}

@RestController
class MyController(private val authorizedClient:OAuth2AuthorizedClient) {
    @GetMapping("/")
    fun get(): String {
        val token = authorizedClient.accessToken
        return """
            token: ${token.tokenValue},
            expires: ${token.expiresAt}
        """.trimIndent()
    }
}

fun main(args: Array<String>) {
	runApplication<Oauth2PoCApplication>(*args)
}
