package com.aitorr.admintelegrambot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class AdminTelegramBotApplication

fun main(args: Array<String>) {
    runApplication<AdminTelegramBotApplication>(*args)
}
