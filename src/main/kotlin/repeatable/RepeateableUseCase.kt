package repeatable

import base.BaseUseCase
import base.UseCaseResult.*
import models.Command
import models.Response
import processor.FtpProcessor

class RepeateableUseCase(private val processor: FtpProcessor) : BaseUseCase() {

    override val commands = listOf(
        Command.APPE, Command.NLST, Command.REIN, Command.STOU
    )

    override fun start(command: Command, args: Array<String>) = when (processor.execute(command, *args).type) {
        Response.Type.POSITIVE_1 -> tryAgain(command, args)
        Response.Type.POSITIVE_2 -> SUCCESS
        Response.Type.POSITIVE_3 -> ERROR
        Response.Type.NEGATIVE_4 -> FAILURE
        Response.Type.NEGATIVE_5 -> FAILURE
    }

    private fun tryAgain(command: Command, args: Array<String>) = when (processor.execute(command, *args).type) {
        Response.Type.POSITIVE_1 -> ERROR
        Response.Type.POSITIVE_2 -> SUCCESS
        Response.Type.POSITIVE_3 -> ERROR
        Response.Type.NEGATIVE_4 -> FAILURE
        Response.Type.NEGATIVE_5 -> FAILURE
    }

}