package api
import java.net.ServerSocket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import org.example.AssistanceSystem
import org.example.User
fun startServer(port: Int) {
    val system = AssistanceSystem()

    ServerSocket(port).use { serverSocket ->
        println("服务端已启动，等待连接... (端口 $port)")

        while (true) {
            serverSocket.accept().use { clientSocket ->
                println("\n=== 新客户端连接 ===")
                println("客户端IP: ${clientSocket.inetAddress.hostAddress}")

                clientSocket.getInputStream().bufferedReader().use { reader ->
                    clientSocket.getOutputStream().bufferedWriter().use { writer ->
                        try {
                            val request = reader.readLine()
                            println("收到原始请求: $request")

                            val response = processRequest(system, request)
                            println("返回响应: $response")

                            writer.write("$response\n")
                            writer.flush()
                        } catch (e: Exception) {
                            println("处理错误: ${e.javaClass.simpleName} - ${e.message}")
                        }
                    }
                }
                println("=== 连接处理完成 ===\n")
            }
        }
    }
}


private fun processRequest(system: AssistanceSystem, request: String): String {
    return try {
        val parts = request.split("|")
        when (parts[0]) {
            "ADD_USER" -> {
                val name = parts[1]
                val user = User(name, mutableMapOf())
                system.addUser(user)
                "SUCCESS|用户添加成功|ID=${system.id - 1}"
            }

            "CLOCK_IN" -> {
                val params = parts[1].split(",")
                val userId = params[0].toInt()
                val day = params[1].toInt()
                val timePeriods = params[2]

                if (params.size > 3 && params[3] == "true") {
                    system.supRecord(userId, day, timePeriods, system.nowWeek)
                } else {
                    system.clockinCard(userId, day, timePeriods)
                }
                "SUCCESS|打卡记录成功"
            }

            "GET_DAILY" -> {
                val params = parts[1].split(",")
                val hours = system.getDailyTime(params[0].toInt(), params[1].toInt())
                "SUCCESS|日工时查询|HOURS=$hours"
            }

            "GET_WEEKLY" -> {
                val params = parts[1].split(",")
                val hours = system.getWeeklyTime(params[0].toInt(), params[1].toInt())
                "SUCCESS|周工时查询|HOURS=$hours"
            }

            else -> "ERROR|无效指令"
        }
    } catch (e: Exception) {
        "ERROR|请求处理失败: ${e.message}"
    }
}


