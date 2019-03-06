package xgen.chat.sever.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SetstateTask implements Runnable {

	Socket tcpSocket;
	
	public SetstateTask(Socket tcpSocket) {
		this.tcpSocket = tcpSocket;
	}

	/**
	 * 向服务器发送用户在线状态
	 */
	@Override
	public void run() {
		OutputStream out;
		try {
			out = tcpSocket.getOutputStream();
			out.write(1);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
