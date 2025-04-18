package org.example
import api.startServer
import java.net.Socket
import kotlin.concurrent.thread

fun testCases() {
    val testPort = 8081 // 用不同端口避免冲突
    thread { startServer(testPort) }.also { Thread.sleep(500) } // 启动测试服务端

    val testCommands = listOf(
        "ADD_USER|测试用户" to "SUCCESS",
        "CLOCK_IN|0,5,8-12" to "SUCCESS",
        "GET_DAILY|1,3" to "HOURS=4",
        "GET_WEEKLY|0,1" to "WEEKLY=0",
        "INVALID_CMD" to "ERROR"
    )

    testCommands.forEach { (cmd, expected) ->
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