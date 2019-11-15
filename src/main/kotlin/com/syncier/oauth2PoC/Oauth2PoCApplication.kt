package com.syncier.oauth2PoC

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import org.springframework.security.oauth2.common.AuthenticationScheme
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableOAuth2Client
class Oauth2PoCApplication {
    @Bean
    fun oauth2restTemplate(): OAuth2RestTemplate {

        var details = object: ResourceOwnerPasswordResourceDetails() {
            override fun getAuthenticationScheme() = AuthenticationScheme.form
            override fun isAuthenticationRequired() = true
            override fun getId() = "id"
            override fun getTokenName() = "tokenName"
            override fun isClientOnly() = false
            override fun getClientId() = "zv-inventory-tu"
            override fun getGrantType() = "password"
            override fun getClientAuthenticationScheme() = AuthenticationScheme.header
            override fun getAccessTokenUri() = "https://auth.marketplace.syncier.rocks/auth/realms/syncier-motor-registration/protocol/openid-connect/token"
            override fun getClientSecret() = "b06d28ab-989e-4cd7-8d0c-b781d0e6d106"
            override fun isScoped() = false
            override fun getScope() = mutableListOf<String>()
            override fun getPassword() = "bwPCqSxqZJtqkCm9q3S2DHjNWzZbAse6"
            override fun getUsername() = "zv-inventory-test"
        }

        return OAuth2RestTemplate(details)
    }

    @Bean
    fun cmd(myController: MyController) = CommandLineRunner {
        println(myController.get())
    }

}

@RestController
class MyController(private val oAuth2RestTemplate: OAuth2RestTemplate) {
    @GetMapping("/")
    fun get(): String {
        val token = oAuth2RestTemplate.accessToken
        return """
            token: ${token.value},
            expires: ${token.expiration}
        """.trimIndent()
    }
}

fun main(args: Array<String>) {
	runApplication<Oauth2PoCApplication>(*args)
}
