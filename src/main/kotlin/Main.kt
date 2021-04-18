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


private const val NAME_KEY = "--name"
private const val PASS_KEY = "--pass"

private val commandsMap = mapOf(
    "ls" to LIST,
    "cd" to CWD,
    "login" to USER,
    "user" to USER,
    "pass" to PASS,
    "account" to ACCT,
    "quit" to QUIT,
    "port" to PORT,
//    "passive" to PASV,
    "retrieve" to RETR
)

class FtpClient(host: String, port: Int, args: Map<String, String>) {


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

fun main(args: Array<String>) {

    val client = FtpClient("0.0.0.0", 21, emptyMap())

    client.start()

    client.close()
}