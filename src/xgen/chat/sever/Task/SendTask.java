package xgen.chat.sever.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;

public class SendTask implements Runnable {

	DatagramSocket Udpsocket;
	Map<String, Integer> users;
	String nick;
	Socket tcpSocket;

	public SendTask(DatagramSocket socket, Map<String, Integer> users, String nick, Socket tcpSocket) {
		this.Udpsocket = socket;
		this.users = users;
		this.nick = nick;
		this.tcpSocket = tcpSocket;
	}

	public Map<String, Integer> getUsers() {
		return users;
	}

	public void setUsers(Map<String, Integer> users) {
		this.users = users;
	}

	/**
	 * 实现信息发送
	 */
	@Override
	public void run() {

		String msg = null;
		DatagramPacket packet = null;
		String order = null;

		// 目标端口号
		int port = 0;

		// 目标昵称
		String targetNick = null;

		// 内容集合
		String gather;

		Thread SetState;

		do {
			int p = Udpsocket.getLocalPort();

			do {
				SetState = new Thread(new SetstateTask(tcpSocket));
				SetState.start();

				System.out.println("聊天格式：/xxx+空格+内容");
				System.out.print("聊天栏：/");
				Scanner sc = new Scanner(System.in);
				gather = sc.nextLine();
				if (-1 != gather.indexOf(" ")) {

					targetNick = gather.substring(0, gather.indexOf(" "));

					if (users.containsKey(targetNick)) {
						port = users.get(targetNick);

					} else if (targetNick.equalsIgnoreCase("system")) {
						break;
					} else {
						System.out.println(users);
						System.out.printf("%s不在线，请重新输入。", targetNick);

					}
				} else {
					System.err.println("格式错误，请重新输入");
				}
			} while (!users.containsKey(targetNick));

			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			// 发送内容
			msg = String.format("%s (from %s)", gather.substring(gather.indexOf(" ") + 1), nick);
			order = gather.substring(gather.indexOf(" ") + 1);
			if (!targetNick.equalsIgnoreCase("system") && !order.equalsIgnoreCase("exit")) {
				try {
					byte[] data = msg.getBytes();
					// 创建数据包
					packet = new DatagramPacket(data, data.length, InetAddress.getByName("127.0.0.1"), port);

					// 发送
					Udpsocket.send(packet);

				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (targetNick.equalsIgnoreCase("system") && !order.equalsIgnoreCase("exit")) {
				System.out.println("错误指令，请重新发送");
			} else if (targetNick.equalsIgnoreCase("system") && order.equalsIgnoreCase("exit")) {
				try {
					OutputStream o = tcpSocket.getOutputStream();
					o.write(0);
					o.flush();
					System.out.println("与服务器断开连接");
					Thread.sleep(1000);
					break;
				} catch (IOException e) {
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		} while (!msg.toUpperCase().equals("BYE"));

		System.err.println("对话结束");
		Thread.yield();

	}

}
