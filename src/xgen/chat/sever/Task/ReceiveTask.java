package xgen.chat.sever.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ReceiveTask implements Runnable {

	DatagramSocket udpSocket;

	public ReceiveTask(DatagramSocket socket) {
		this.udpSocket = socket;
	}

	/**
	 * TCP套接字
	 */
	Socket Tcpsocket;

	/**
	 * 获取其他用户发来的信息
	 */
	@Override
	public void run() {
		String msg = null;
		byte[] buf = new byte[1024 * 8];
		DatagramPacket packet;
		
		do{
			packet = new DatagramPacket(buf, buf.length);

			try {
				udpSocket.receive(packet);

				byte[] data = packet.getData();
				msg = new String(data, 0, packet.getLength());
				System.out.println("收到：" + msg);

			} catch (IOException e) {
				e.printStackTrace();
			}

		} while (!msg.toUpperCase().equals("BYE"));
		System.out.println("接收结束");

	}

}
