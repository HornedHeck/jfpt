package processor

import models.Command
import models.Response
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.Socket

class FtpProcessor(private val host: String, private val port: Int = 21) {

    private val socket: Socket = Socket(host, port)
    val sockerPort
        get() = socket.localPort

    val socketAddress
        get() = socket.inetAddress

    private val reader: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer: PrintStream = PrintStream(BufferedOutputStream(socket.getOutputStream()), true)

    val connectionResponse = receive()

    fun receive(): Response {
        val line = reader.readLine() ?: return Response(400, "Unknown")
        val code = line.substring(0, 3).toInt()
        val body = line.substring(3)
        val response = Response(code, body)
        println(response)
        return response
    }

    fun receiveAll(): Response {
        var res = receive()
        while (reader.ready()) {
            res = receive()
        }
        return res
    }

    fun send(command: Command, args: Array<out String>) {
        writer.println("${command.name} ${
            args.joinToString(" ") { it }
        }")
    }

    fun execute(command: Command, vararg args: String): Response {
        send(command, args)
        var res = receive()
        while (reader.ready()) {
            res = receive()
        }
        return res
    }

    fun close() {
        socket.close()
    }

}