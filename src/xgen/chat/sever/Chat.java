package xgen.chat.sever;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;

import xgen.chat.sever.Task.ReceiveTask;
import xgen.chat.sever.Task.SendTask;
import xgen.chat.sever.Task.UsersreadTask;

public class Chat {

	Thread sender;
	Thread receiver;
	Thread usersReader;

	DatagramSocket udpSocket;

	Socket tcpSocket;

	public Chat() {
		try {
			// UDP 端到端 p2p
			udpSocket = new DatagramSocket();
			String json = null;
			Map<String, Integer> users = null;
			String nick = null;
			int size;
			byte[] buf;
			String caution = null;
			tcpSocket = new Socket("127.0.0.1", 9000);
			InputStream in = tcpSocket.getInputStream();
			OutputStream out = tcpSocket.getOutputStream();

			// 创建用户
			nick = createUser(nick, in, out);

			// 获取用户列表
			in = tcpSocket.getInputStream();
			buf = new byte[1024 * 8];
			size = in.read(buf);
			json = new String(buf, 0, size);
			users = JSON.parseObject(json, HashMap.class);
			System.out.println("在线列表：" + users);

			// 实时获取在线列表
			UsersreadTask usesreadTask = new UsersreadTask(in, out, users);
			usersReader = new Thread(usesreadTask);
			usersReader.start();
			try {
				usersReader.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// TODO 把用户列表传给 SendTask
			SendTask sendTask = new SendTask(udpSocket, users, nick, tcpSocket);
			sender = new Thread(sendTask);

			// 接收信息
			receiver = new Thread(new ReceiveTask(udpSocket));

			sender.start();
			receiver.start();

			while (true) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				users = usesreadTask.getUsers();
				sendTask.setUsers(users);
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String createUser(String nick, InputStream in, OutputStream out) throws IOException {
		String json;
		int size;
		byte[] buf;
		do {
			Scanner scanner = new Scanner(System.in);
			System.out.println("昵称：");
			nick = scanner.nextLine();
			System.out.println("尝试创建账号...");

			// TODO C/S
			String port = String.valueOf(udpSocket.getLocalPort());
			String name = String.format("%s,%s", nick, port);
			out.write(name.getBytes());
			out.flush();

			buf = new byte[1024 * 8];
			size = in.read(buf);
			json = new String(buf, 0, size);

			if (!json.equals("昵称重复，请重新命名")) {
				System.out.println("账号创建成功！");
			} else {
				System.out.println("昵称重复，请重新命名");
			}
		} while (json.equals("昵称重复，请重新命名"));
		return nick;
	}

	public static void main(String[] args) {

		Chat chat = new Chat();

	}
}
