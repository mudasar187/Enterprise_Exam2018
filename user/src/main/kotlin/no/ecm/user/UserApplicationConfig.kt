package no.ecm.user

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration


@Configuration
@EntityScan(basePackages = ["no.ecm.user"])
class UserApplicationConfig {
}