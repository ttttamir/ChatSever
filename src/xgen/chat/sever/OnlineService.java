package xgen.chat.sever;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import xgen.chat.sever.Task.GetstateTask;
import xgen.chat.sever.Task.SetusersTask;

public class OnlineService implements Runnable {

	Socket socket;

	Map<String, Integer> users;
	String nick;
	int count;
	List<Object> list = new ArrayList<>();

	public OnlineService(Socket socket, Map<String, Integer> users) {
		this.socket = socket;
		this.users = users;
//		this.count = count;
	}

	Thread GetState;
	Thread SetUsers;
	

	@Override
	public void run() {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		list = Userread(nick, in, out, users);
		nick = (String) list.get(0);
		users = (Map<String, Integer>) list.get(1);

		// 发送其他用户的信息
		// users ---> XML/JSON
		try {
			String json = JSON.toJSONString(users);
			out.write(json.getBytes());
			System.out.println("发送：" + json);
			out.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		int result = 1;

		// 接收用户状态和发送在线名单
		// 接收用户状态
		GetState = new Thread(new GetstateTask(users, socket, result, nick));
		GetState.start();

		SetUsers = new Thread(new SetusersTask(users, socket));
		SetUsers.start();

		while (true) {
			try {
				nick = (String) list.get(0);
				users = (Map<String, Integer>) list.get(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private List Userread(String nick, InputStream in, OutputStream out, Map<String, Integer> users) {
		String message = null;

		do {
			try {

				// 读取昵称、IP地址
				byte[] buf = new byte[64];
				int size = in.read(buf);
				String[] user = new String(buf, 0, size).split(",");
				nick = user[0];
				System.out.println("收到：" + nick);
				if (!users.keySet().contains(nick)) {

					int port = Integer.valueOf(user[1]);

					// 存储
					users.put(nick, port);
					// Thread.currentThread().notifyAll();
					list.add(nick);
					list.add(users);

					message = "用户已录入";
					out.write(message.getBytes());
					out.flush();

				} else {
					message = "昵称重复，请重新命名";
					out.write(message.getBytes());
					out.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (message.equals("昵称重复，请重新命名"));
		return list;
	}
}
