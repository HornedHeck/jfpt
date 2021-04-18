package models

data class Response(val code: Int, val body: String) {

    override fun toString(): String {
        return "$code $body"
    }

    val type = Type.values()[code / 100 - 1]

    val isSuccessful = type == Type.POSITIVE_2


    enum class Type {
        POSITIVE_1,
        POSITIVE_2,
        POSITIVE_3,
        NEGATIVE_4,
        NEGATIVE_5,
    }

}
