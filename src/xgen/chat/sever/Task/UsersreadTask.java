package xgen.chat.sever.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class UsersreadTask implements Runnable {

	InputStream in;
	OutputStream out;
	Map<String, Integer> users;

	public UsersreadTask(InputStream in, OutputStream out, Map<String, Integer> users) {
		this.in = in;
		this.out = out;
		this.users = users;
	}

	/**
	 * 用户获取在线列表
	 */
	@Override
	public void run() {
		while (true) {
			String state;
			byte[] buf = new byte[1024 * 8];
			int size = 0;
			try {
				size = in.read(buf);
				state = new String(buf, 0, size);
				this.users = JSON.parseObject(state, HashMap.class);
				System.out.println("在线列表：" + state);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, Integer> getUsers() {
		return users;
	}

	public void setUsers(Map<String, Integer> users) {
		this.users = users;
	}

}
