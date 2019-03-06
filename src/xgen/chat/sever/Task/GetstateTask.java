package xgen.chat.sever.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class GetstateTask implements Runnable {

	Map<String, Integer> users;
	Socket socket;
	int result;
	String nick;
	InputStream in;

	public GetstateTask(Map<String, Integer> users, Socket socket, int result, String nick) {
		this.users = users;
		this.socket = socket;
		this.result = result;
		this.nick = nick;
	}

	/**
	 * 用户下线后，服务端在在线列表中删除用户信息
	 */
	@Override
	public void run() {
		try {
			in = socket.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				result = in.read();

				if (result == 0) {
					users.remove(nick);
					System.out.println(nick + "已下线");
					System.out.println(users);
					Thread.sleep(10000);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
