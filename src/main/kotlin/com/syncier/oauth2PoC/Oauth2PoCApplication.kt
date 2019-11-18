package com.syncier.oauth2PoC

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.*
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
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
    @Bean
    fun client(clientRegistrationRepository: ClientRegistrationRepository): OAuth2AuthorizedClient? {
        val clientRegistration = clientRegistrationRepository.findByRegistrationId("keycloak-client")
        val provider = OAuth2AuthorizedClientProviderBuilder.builder().password().build()
        val auth = object : Authentication {
            override fun getAuthorities() = mutableListOf<SimpleGrantedAuthority>()
            override fun setAuthenticated(isAuthenticated: Boolean) {
            }
            override fun getName() = "name"
            override fun getCredentials() = "credentials"
            override fun getPrincipal() = "principal"
            override fun isAuthenticated() = true
            override fun getDetails() = "details"
        }
        val oAuth2AuthorizationContext = OAuth2AuthorizationContext
                .withClientRegistration(clientRegistration)
                .attribute(OAuth2AuthorizationContext.PASSWORD_ATTRIBUTE_NAME, "bwPCqSxqZJtqkCm9q3S2DHjNWzZbAse6")
                .attribute(OAuth2AuthorizationContext.USERNAME_ATTRIBUTE_NAME, "zv-inventory-test")
                .principal(auth)
                .build()
        return provider.authorize(oAuth2AuthorizationContext)
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
