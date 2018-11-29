package no.ecm.utils.messages

class InfoMessages{

    companion object {
        fun entityCreatedSuccessfully(type: String, id: String): String {
            return "Successfully created $type with id: $id"
        }

        fun entityFieldUpdatedSuccessfully(type: String, id: String, field: String): String {
            return "Updated $field on $type with id: $id"
        }

        fun entitySuccessfullyUpdated(type: String, id: String): String {
            return "$type with id: $id successfully updated"
        }

        fun entitySuccessfullyDeleted(type: String, id: String): String {
            return "Successfully deleted $type with id: $id"
        }
    }
}