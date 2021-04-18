package simple

import base.BaseUseCase
import base.UseCaseResult.*
import models.Command
import models.Response
import processor.FtpProcessor

class SimpleUseCase(private val processor: FtpProcessor) : BaseUseCase() {

    override val commands = listOf(
        Command.ABOR,
        Command.ALLO,
        Command.DELE,
        Command.CWD,
        Command.CDUP,
        Command.SMNT,
        Command.HELP,
        Command.MODE,
        Command.NOOP,
        Command.PASV,
        Command.QUIT,
        Command.SITE,
        Command.SYST,
        Command.STAT,
        Command.RMD,
        Command.PWD,
        Command.STRU,
        Command.TYPE
    )

    override fun start(command: Command, args: Array<String>) = when (processor.execute(command, *args).type) {
        Response.Type.POSITIVE_1 -> ERROR
        Response.Type.POSITIVE_2 -> SUCCESS
        Response.Type.POSITIVE_3 -> ERROR
        Response.Type.NEGATIVE_4 -> FAILURE
        Response.Type.NEGATIVE_5 -> FAILURE
    }

}