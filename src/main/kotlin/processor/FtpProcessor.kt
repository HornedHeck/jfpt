package processor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import models.Command
import models.DataResponse
import models.Response
import models.addData
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket

class FtpProcessor(private val host: String, private val port: Int = 21) {

    private val socket: Socket = Socket(host, port)

    val address = socket.localAddress

    private var mode: FtpMode = FtpMode.PORT
    var dataPort: Int = socket.localPort - 1
        private set

    fun setMode(isActive: Boolean, port: Int) {
        mode = if (isActive) {
            FtpMode.PORT
        } else {
            FtpMode.PASV
        }
        dataPort = port
    }

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
        return receive()
    }

    fun close() {
        socket.close()
    }

    private val context = Dispatchers.IO

    fun executeWithData(command: Command, args: Array<out String>): DataResponse {
        return when (mode) {
            FtpMode.PORT -> {
                val serverSocket = ServerSocket(dataPort)
                send(command, args)
                val firstResponse = receive()
                if (firstResponse.type != Response.Type.POSITIVE_1)
                    return firstResponse.addData("")
                val connection = serverSocket.accept()
                val data = InputStreamReader(connection.getInputStream()).readText()
                serverSocket.close()
                receive().addData(data)
            }
            FtpMode.PASV -> {
                send(command, args)
                val firstResponse = receive()
                if (firstResponse.type != Response.Type.POSITIVE_1)
                    return firstResponse.addData("")

                val connection = Socket(host, dataPort)
                val data = InputStreamReader(connection.getInputStream()).readText()
                receive().addData(data)
            }
        }
    }

    fun sendData(command: Command , data: String, path: String): Response {
        return when (mode) {
            FtpMode.PORT -> {
                val serverSocket = ServerSocket(dataPort)
                send(command, arrayOf(path))
                val firstResponse = receive()
                if (firstResponse.type != Response.Type.POSITIVE_1)
                    return firstResponse
                val connection = serverSocket.accept()
                PrintStream(connection.getOutputStream(), true).println(data)
                connection.close()
                serverSocket.close()
                receive()
            }
            FtpMode.PASV -> {
                send(command, arrayOf(path))
                val firstResponse = receive()
                if (firstResponse.type != Response.Type.POSITIVE_1)
                    return firstResponse

                val connection = Socket(host, dataPort)
                PrintStream(connection.getOutputStream(), true).println(data)
                receive()
            }
        }
    }

    fun stopReceiving() {
        context.cancel()
    }

    enum class FtpMode {
        PASV,
        PORT
    }

}