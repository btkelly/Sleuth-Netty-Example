package com.example.sleuth.netty

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class RouterFunctions(webClientBuilder: WebClient.Builder) {

    private val webClient = webClientBuilder.baseUrl("https://httpbin.org/")
        .build()

    private val logger = LoggerFactory.getLogger("LocalLogger")

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return router {
            POST("/local", ::basicGetRequest)
            POST("/fetch", ::httpBinRequest)
        }
    }

    fun basicGetRequest(request: ServerRequest): Mono<ServerResponse> = request
        .bodyToMono<String>()
        .switchIfEmpty(Mono.just("No request body"))
        .map { logger.info("/local log message with request body: $it") }
        .flatMap { ServerResponse.ok().build() }

    fun httpBinRequest(request: ServerRequest): Mono<ServerResponse> = request
        .bodyToMono<String>()
        .switchIfEmpty(Mono.just("No request body"))
        .flatMap {
            logger.info("/fetched log message with request body: $it")
            webClient.post()
                .uri("/post")
                .bodyValue(it)
                .retrieve()
                .bodyToMono<String>()
                .switchIfEmpty(Mono.just("No response body"))
        }
        .flatMap {
            logger.info("/fetched log message with httpbin response body: $it")
            ServerResponse.ok()
                .bodyValue(it)
        }
}