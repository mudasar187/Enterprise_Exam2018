package no.ecm.user

import org.springframework.boot.SpringApplication

class LocalApplicationRunner{}

fun main(args: Array<String>) {
    SpringApplication.run(UserApplication::class.java, "--spring.profiles.active=test")
}