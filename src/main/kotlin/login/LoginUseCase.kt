package login

import base.BaseUseCase
import base.UseCaseResult
import base.UseCaseResult.*
import models.Command
import models.Command.*
import models.Response.Type.*
import processor.FtpProcessor

class LoginUseCase(private val processor: FtpProcessor, private val interactor: LoginInteractor) : BaseUseCase() {

    override val commands = listOf(USER, PASS, ACCT)

    private var name: String? = null
        get() = field.apply { name = null }

    private var pass: String? = null
        get() = field.apply { pass = null }

    private var info: String? = null
        get() = field.apply { info = null }

    override fun start(command: Command, args: Array<String>): UseCaseResult {

        return when (command) {
            USER -> {
                name = args.getOrNull(0)
                pass = args.getOrNull(1)
                info = args.getOrNull(2)
                proceedUser()
            }
            PASS -> {
                pass = args.getOrNull(0)
                info = args.getOrNull(1)
                proceedPassword()
            }
            ACCT -> {
                info = args.getOrNull(0)
                proceedAcct()
            }

            else -> ERROR
        }
    }

    private fun proceedUser() = when (processor.execute(USER, name ?: interactor.requireUsername()).type) {
        POSITIVE_1 -> ERROR
        POSITIVE_2 -> SUCCESS
        POSITIVE_3 -> proceedPassword()
        NEGATIVE_4 -> FAILURE
        NEGATIVE_5 -> FAILURE
    }

    private fun proceedPassword() = when (processor.execute(PASS, pass ?: interactor.requirePassword()).type) {
        POSITIVE_1 -> ERROR
        POSITIVE_2 -> SUCCESS
        POSITIVE_3 -> proceedAcct()
        NEGATIVE_4 -> FAILURE
        NEGATIVE_5 -> FAILURE
    }


    private fun proceedAcct() = when (processor.execute(ACCT, info ?: interactor.requireAcct()).type) {
        POSITIVE_1 -> ERROR
        POSITIVE_2 -> SUCCESS
        POSITIVE_3 -> ERROR
        NEGATIVE_4 -> FAILURE
        NEGATIVE_5 -> FAILURE
    }

}