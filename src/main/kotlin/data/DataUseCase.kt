package data

import base.BaseUseCase
import base.UseCaseResult
import base.UseCaseResult.*
import models.Command
import models.Response
import processor.FtpProcessor

private fun Response.Type.toUseCaseResult() = when (this) {
    Response.Type.POSITIVE_1 -> ERROR
    Response.Type.POSITIVE_2 -> SUCCESS
    Response.Type.POSITIVE_3 -> ERROR
    Response.Type.NEGATIVE_4 -> FAILURE
    Response.Type.NEGATIVE_5 -> FAILURE
}

class DataUseCase(private val processor: FtpProcessor) : BaseUseCase() {

    //    private var isServerActive = true
    private var dataPort = processor.sockerPort

    override val commands = listOf(
        Command.PORT,
        Command.RETR,
        Command.LIST
    )

    override fun start(command: Command, args: Array<String>) = when (command) {
        Command.PORT -> proceedPortCommand(args)
        else -> ERROR
    }

    private fun proceedPortCommand(args: Array<String>): UseCaseResult {
        val rawPort = args.getOrNull(0)?.toInt() ?: processor.sockerPort
        dataPort = rawPort - 1
        val address = processor.socketAddress.address
        val portH = (dataPort / 256)
        val portL = (dataPort % 256)
        val formattedArgs = "$192,168,0,108,$portH,$portL"
        return processor.execute(Command.PORT, formattedArgs).type.toUseCaseResult()
    }

//    private fun executeRetr(args: Array<String>): UseCaseResult {
//        processor.send(Command.RETR, args)
//        val res = processor.receive()
//        if (res.type != Response.Type.POSITIVE_1) return res.type.toUseCaseResult()
//    }

}