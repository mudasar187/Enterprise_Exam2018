package no.ecm.order.service

import no.ecm.order.model.converter.InvoiceConverter
import no.ecm.order.model.entity.Invoice
import no.ecm.order.repository.InvoiceRepository
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.ExceptionMessages.Companion.invalidFieldCombination
import no.ecm.utils.validation.ValidationHandler
import no.ecm.utils.validation.ValidationHandler.Companion.validateId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class InvoiceService(
        private var invoiceRepository: InvoiceRepository
) {

    @Value("\${movieService}")
    private lateinit var moviePath : String

    val logger = logger<InvoiceService>()

    fun findInvoice(username: String?, nowPlayingId: String?, isPaid: Boolean?): MutableList<InvoiceDto> {

        val invoices = when {
            !username.isNullOrBlank() && !nowPlayingId.isNullOrBlank() && isPaid == null -> {
                val errorMsg = invalidFieldCombination("username, nowPlayingId and isPaid")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)}

            !username.isNullOrBlank() && !nowPlayingId.isNullOrBlank() -> invoiceRepository
                    .findAllByUsernameIgnoreCaseAndNowPlayingId(
                            username!!,
                            validateId(nowPlayingId, "nowPLayingId"))

            !username.isNullOrBlank() && isPaid != null -> {
                invoiceRepository.findAllByUsernameIgnoreCaseAndIsPaid(username!!, isPaid)}

            !username.isNullOrBlank() -> invoiceRepository.findAllByUsernameIgnoreCase(username!!).toMutableList()

            isPaid != null && !nowPlayingId.isNullOrBlank() -> invoiceRepository
                    .findAllByIsPaidAndNowPlayingId(
                            isPaid,
                            validateId(nowPlayingId, "nowPLayingId"))

            isPaid != null -> invoiceRepository.findAllByIsPaid(isPaid)

            !nowPlayingId.isNullOrBlank() -> {
                invoiceRepository.findAllByNowPlayingId(validateId(nowPlayingId, "nowPLayingId"))}

            else -> invoiceRepository.findAll().toMutableList()
        }
        return InvoiceConverter.entityListToDtoList(invoices)
    }

    fun findById(stringId: String?): Invoice{
        val id = ValidationHandler.validateId(stringId, "id")

        checkFOrInvoiceInDatabase(id)

        return invoiceRepository.findById(id).get()
    }

    private fun checkFOrInvoiceInDatabase(id: Long){
        if (!invoiceRepository.existsById(id)){
            val errorMsg = ExceptionMessages.notFoundMessage("Invoice", "id", id.toString())
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }
    }

    private fun handleUnableToParse(fieldName: String){
        val errorMsg = ExceptionMessages.unableToParse(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    private fun handleIllegalField(fieldName: String) {
        val errorMsg = ExceptionMessages.illegalParameter(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    private fun handleMissingField(fieldName: String){
        val errorMsg = ExceptionMessages.missingRequiredField(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

}