package no.ecm.creditcard

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@EntityScan(basePackages = ["no.ecm.creditcard"])
class CreditCardApplicationConfig {


}