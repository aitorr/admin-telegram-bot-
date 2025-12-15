package com.aitorr.moderator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ModeratorApplication

fun main(args: Array<String>) {
    runApplication<ModeratorApplication>(*args)
}
