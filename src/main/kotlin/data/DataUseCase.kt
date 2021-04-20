package data

import base.BaseUseCase
import base.UseCaseResult
import base.UseCaseResult.*
import models.Command
import models.Response
import processor.FtpProcessor
import java.io.File

private fun Response.Type.toUseCaseResult() = when (this) {
    Response.Type.POSITIVE_1 -> ERROR
    Response.Type.POSITIVE_2 -> SUCCESS
    Response.Type.POSITIVE_3 -> ERROR
    Response.Type.NEGATIVE_4 -> FAILURE
    Response.Type.NEGATIVE_5 -> FAILURE
}

class DataUseCase(private val processor: FtpProcessor) : BaseUseCase() {

    override val commands = listOf(
        Command.PORT,
        Command.RETR,
        Command.LIST,
        Command.PASV,
        Command.STOR
    )

    override fun start(command: Command, args: Array<String>) = when (command) {
        Command.PORT -> proceedPortCommand(args)
        Command.LIST -> proceedList(args)
        Command.PASV -> proceedPasv()
        Command.RETR -> proceedRetr(args)
        Command.STOR -> proceedStore(args)
        else -> ERROR
    }

    private fun proceedList(args: Array<String>): UseCaseResult {
        val res = processor.executeWithData(Command.LIST, args)
        if (res.data.isNotEmpty()) {
            println(res.data)
        }
        return res.type.toUseCaseResult()
    }

    private fun proceedPortCommand(args: Array<String>): UseCaseResult {
        val port = args.getOrNull(0)?.toInt() ?: processor.dataPort
        val address = processor.address.hostAddress.replace(".", ",") + ",${port / 256},${port % 256}"
        val res = processor.execute(Command.PORT, address)
        if (res.isSuccessful) {
            processor.setMode(true, port)
        }
        return res.type.toUseCaseResult()
    }

    private fun proceedPasv(): UseCaseResult {
        val res = processor.execute(Command.PASV)
        if (res.isSuccessful) {
            processor.setMode(false, extractPort(res))
        }
        return res.type.toUseCaseResult()
    }

    private fun extractPort(res: Response): Int {
        val rawPort = res.body
            .substringAfter('(')
            .substringBefore(')')
            .split(",")
            .takeLast(2)

        return rawPort[0].toInt() * 256 + rawPort[1].toInt()
    }

    private fun proceedStore(args: Array<String>) = try {
        val file = File(args.first())
        val path = args.getOrElse(1) { file.name }
        processor.sendData(Command.STOR, file.readText(), path).type.toUseCaseResult()
    } catch (e: Exception) {
        println("File not found")
        ERROR
    }

    private fun proceedRetr(args: Array<String>): UseCaseResult {
        val res = processor.executeWithData(Command.RETR, args)
        if (res.isSuccessful && res.data.isNotEmpty()) {
            println(res.data)
            File(args.first().substringAfterLast("/"))
        }
        return res.type.toUseCaseResult()
    }

}