package xgen.chat.sever.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class SetusersTask implements Runnable {

	int count = 1;
	Map<String, Integer> users;
	Socket socket;
	OutputStream out;

	public SetusersTask( Map<String, Integer> users, Socket socket) {
//		this.count = count;
		this.users = users;
		this.socket = socket;
	}

	/**
	 * 在有人上线后，服务端想每位用户发送更新的用户列表
	 */
	@Override
	public void run() {
		try {
			out = socket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			while (true) {
				Thread.sleep(5000);
				if (count != users.size()) {
					String json = new Gson().toJson(users);
					out.write(json.getBytes());
					System.out.println("发送：" + json);
					out.flush();
					count = users.size();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
