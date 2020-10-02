package com.example.sleuth.netty

import brave.http.HttpTracing
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.zalando.logbook.Logbook
import org.zalando.logbook.netty.LogbookClientHandler
import org.zalando.logbook.netty.LogbookServerHandler
import reactor.netty.ConnectionObserver
import reactor.netty.http.client.HttpClient
import reactor.netty.http.server.BraveHttpServerTracing

@Configuration
class LoggingConfiguration {

    @Bean
    fun braveHttpServerTracing(httpTracing: HttpTracing) = BraveHttpServerTracing
        .create(httpTracing) as BraveHttpServerTracing

    @Bean
    fun logbook() = Logbook.builder()
        .build()

    @Bean
    @ConditionalOnClass(WebClientCustomizer::class)
    fun webClientCustomizer(logbook: Logbook, braveHttpServerTracing: BraveHttpServerTracing): WebClientCustomizer =
        WebClientCustomizer { webClientBuilder ->
            val httpClient = HttpClient.create()
                .observe(braveHttpServerTracing)
                .doOnConnected {
                    it.addHandlerLast(LogbookClientHandler(logbook))
                }
            webClientBuilder.clientConnector(ReactorClientHttpConnector(httpClient))
        }

    @Bean
    @ConditionalOnClass(NettyServerCustomizer::class)
    fun nettyServerCustomizer(logbook: Logbook, braveHttpServerTracing: BraveHttpServerTracing): NettyServerCustomizer =
        NettyServerCustomizer { httpServer ->
            httpServer.childObserve(braveHttpServerTracing)
                .doOnConnection {
                    it.addHandlerLast(LogbookServerHandler(logbook))
                }
        }
}
