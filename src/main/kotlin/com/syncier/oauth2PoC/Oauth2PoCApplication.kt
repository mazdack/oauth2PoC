package com.syncier.oauth2PoC

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Oauth2PoCApplication {
    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.keycloak-client")
    fun details(): ResourceOwnerPasswordResourceDetails {
        return ResourceOwnerPasswordResourceDetails()
    }

    @Bean
    fun oauth2restTemplate(details: ResourceOwnerPasswordResourceDetails): OAuth2RestTemplate {
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
