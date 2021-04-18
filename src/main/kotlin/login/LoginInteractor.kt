package login

import base.BaseInteractor

class LoginInteractor : BaseInteractor() {


    fun requireUsername() = requireString("username")


    fun requirePassword() = requireString("password")


    fun requireAcct() = requireString("account info")

}