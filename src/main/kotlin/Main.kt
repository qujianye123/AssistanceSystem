package org.example
import api.startServer
import java.net.Socket
import kotlin.concurrent.thread

fun testCases() {
    val testPort = 8081 // 用不同端口避免冲突
    thread { startServer(testPort) }.also { Thread.sleep(500) } // 启动测试服务端

    val testCommands = listOf(
        "CLOCK_IN|0,5,8-12 13-17" ,
        "GET_DAILY|1,5",
        "GET_WEEKLY|0,1",
        "SUP_CARD|0,3,4,16-19 20-21" ,
        "INVALID_CMD"
    )

    testCommands.forEach { cmd ->
        Socket("localhost", testPort).use { socket ->
            socket.getOutputStream().bufferedWriter().use { writer ->
                socket.getInputStream().bufferedReader().use { reader ->
                    writer.write("$cmd\n")
                    writer.flush()
                    val response = reader.readLine()
                }
            }
        }
    }
}

fun main() {
    testCases() // 运行自动化测试用例
}