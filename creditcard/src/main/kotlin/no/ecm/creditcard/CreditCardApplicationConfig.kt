package no.ecm.creditcard

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.CrossOrigin

@Configuration
@EntityScan(basePackages = ["no.ecm.creditcard"])
@CrossOrigin(origins = ["http://localhost:8080"])
class CreditCardApplicationConfig {


}