//package no.ecm.creditcard.security
//
//import no.ecm.creditcard.repository.CreditCardRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.userdetails.UserDetails
//
//
///**
// * Custom check. Not only we need a user authenticated, but we also
// * need to make sure that a user can only access his/her data, and not the
// * one of the other users
// */
//class UserSecurity{
//
//    @Autowired
//    private lateinit var creditCardRepository: CreditCardRepository
//
//    fun checkId(authentication: Authentication, id: String) : Boolean{
//        val current = (authentication.principal as UserDetails).username
//
//        return try {
//            creditCardRepository.findByUsername(current).username == current
//        } catch (e : Exception){
//            false
//        }
//    }
//}