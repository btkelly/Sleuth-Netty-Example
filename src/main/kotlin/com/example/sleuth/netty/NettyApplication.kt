package com.example.sleuth.netty

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.sleuth.netty"])
class NettyApplication

fun main(args: Array<String>) {
	runApplication<NettyApplication>(*args)
}
