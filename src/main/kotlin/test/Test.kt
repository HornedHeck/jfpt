package test

import SERVER_CONTROL_PORT
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket

fun main() {


    val socket = Socket("0.0.0.0", SERVER_CONTROL_PORT)
    val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    println(reader.readLine())
    val writer = PrintStream(BufferedOutputStream(socket.getOutputStream()), true)

    writer.println("USER hornedheck")
    println(reader.readLine())

    writer.println("PASS 110101")
    println(reader.readLine())
// 37 , 28
    val serverSocket = ServerSocket(9500)

    writer.println("PASV")
    val rawAddres = reader.readLine()
    println(rawAddres)
    val portRaw = rawAddres.substringAfter('(').substringBefore(')').split(',').takeLast(2)
    val port = portRaw[0].toInt() * 256 + portRaw[1].toInt()
    val host = "127.0.1.1"

    writer.println("LIST")
    var dataSocket = Socket(host, port)
    println("Connected")

    var dataReader = BufferedReader(InputStreamReader(dataSocket.getInputStream()))

    println(reader.readLine())
    println(dataReader.readText())
    println(reader.readLine())

//    writer.println("RETR test2.txt")
//    println(reader.readLine())
//    dataSocket = Socket(host, port)
//    dataReader = BufferedReader(InputStreamReader(dataSocket.getInputStream()))
//    println(dataReader.readText())

}