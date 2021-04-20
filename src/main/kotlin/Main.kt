import data.DataUseCase
import login.LoginInteractor
import login.LoginUseCase
import models.Command
import models.Command.*
import processor.FtpProcessor
import repeatable.RepeateableUseCase
import simple.SimpleUseCase
import java.util.*
import kotlin.system.exitProcess


const val SERVER_CONTROL_PORT = 8954

private val commandsMap = mapOf(
    "ls" to LIST,
    "cd" to CWD,
    "login" to USER,
    "user" to USER,
    "pass" to PASS,
    "account" to ACCT,
    "quit" to QUIT,
    "port" to PORT,
    "pasv" to PASV,
    "get" to RETR,
    "mkdir" to MKD,
    "dir" to PWD,
    "syst" to SYST,
    "status" to STAT,
    "rmdir" to RMD,
    "send" to STOR
)

class FtpClient(host: String, port: Int) {

    private val processor = FtpProcessor(host, port)

    init {
        if (!processor.connectionResponse.isSuccessful) {
            println("Connection error")
            processor.close()
            exitProcess(1)
        }
    }

    private val useCases = listOf(
        LoginUseCase(processor, LoginInteractor()),
        SimpleUseCase(processor),
        RepeateableUseCase(processor),
        DataUseCase(processor)
    )

    private fun getExecutor(command: Command) = useCases.firstOrNull { command in it.commands }

    private fun availableCommands() {
        println("Available commands:")
        println(commandsMap.keys.joinToString("\n") { it })
    }

    fun start() {
        val scanner = Scanner(System.`in`)
        while (true) {
            val commandArgsString = scanner.nextLine()
            if (commandArgsString.isEmpty()) {
                continue
            }
            val commandArgs = commandArgsString.split(" ")
            val command = commandsMap[commandArgs[0]]
            command?.let {
                getExecutor(it)?.start(it, *commandArgs.subList(1, commandArgs.size).toTypedArray())
            } ?: run { availableCommands() }

            if (command == QUIT) {
                break
            }
        }
    }

    fun close() = processor.close()

}

fun main() {

    setupServer()

    val client = FtpClient("0.0.0.0", SERVER_CONTROL_PORT)

    client.start()

    client.close()
}