package com.example.sleuth.netty

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.zalando.logbook.Logbook
import org.zalando.logbook.netty.LogbookClientHandler
import org.zalando.logbook.netty.LogbookServerHandler
import reactor.netty.http.client.HttpClient

@Configuration
class LoggingConfiguration {

    @Bean
    fun logbook() = Logbook.builder()
        .build()

    @Bean
    @ConditionalOnClass(WebClientCustomizer::class)
    fun webClientCustomizer(logbook: Logbook): WebClientCustomizer =
        WebClientCustomizer { webClientBuilder ->
            val httpClient = HttpClient.create()
                .tcpConfiguration { tcpClient ->
                    tcpClient.doOnConnected { connection ->
                        connection.addHandlerLast(LogbookClientHandler(logbook))
                    }
                }
            webClientBuilder.clientConnector(ReactorClientHttpConnector(httpClient))
        }

    @Bean
    @ConditionalOnClass(NettyServerCustomizer::class)
    fun nettyServerCustomizer(logbook: Logbook): NettyServerCustomizer =
        NettyServerCustomizer { httpServer ->
            httpServer.tcpConfiguration { tcpServer ->
                tcpServer.doOnConnection { connection ->
                    connection.addHandlerLast(LogbookServerHandler(logbook))
                }
            }
        }
}
