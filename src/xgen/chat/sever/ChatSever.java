
package xgen.chat.sever;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatSever {

	// TCP 服务端套接字
	ServerSocket serversocket;

	// 并发编程
	ExecutorService pool;

	// 记录客户端信息
	Map<String, Integer> users = new HashMap<>();
	
	// 计数器
	int count = 1;

	public ChatSever() {
		pool = Executors.newCachedThreadPool();
	}

	public void start() {
		try {
			serversocket = new ServerSocket(9000);
			System.out.println("服务器启动...");
			while (true) {
				// 建立连接
				Socket socket = serversocket.accept();

				// 让线程池中一个线程处理用户上线
				OnlineService onlineservice = new OnlineService(socket,users);
				count++;
				pool.execute(onlineservice);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ChatSever sever = new ChatSever();
		sever.start();
	}
	
}
