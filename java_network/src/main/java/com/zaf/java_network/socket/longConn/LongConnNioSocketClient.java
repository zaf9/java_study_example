package com.zaf.java_network.socket.longConn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.SocketUtils;
import com.zaf.java_network.socket.commons.UserPojo;

public class LongConnNioSocketClient {

	private static final Logger logger = LoggerFactory.getLogger(LongConnNioSocketClient.class);

	private Socket socket;

	private String ip;

	private int port;

	private String id;

	private String localIp;
	private int localPort;

	DataOutputStream dos;

	DataInputStream dis;

	public LongConnNioSocketClient(String ip, int port, String id) {
		try {
			this.ip = ip;
			this.port = port;
			this.id = id;
			this.socket = new Socket(ip, port);
			// this.socket.setKeepAlive(true);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			new Thread(new heartThread()).start();
			new Thread(new MsgThread()).start();
			this.localIp = SocketUtils.getLocalIp(socket);
			this.localPort = SocketUtils.getLocalPort(socket);
			logger.info("LongConnNioSocketClient id: {} remoteIp: {} remotePort: {} localIp: {} localPort: {}", id, ip,
					port, this.localIp, this.localPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(Object content) {
		try {
			int len = ObjectToByte(content).length;

			ByteBuffer dataLenBuf = ByteBuffer.allocate(4);
			dataLenBuf.order(ByteOrder.LITTLE_ENDIAN);
			dataLenBuf.putInt(0, len);
			dos.write(dataLenBuf.array(), 0, 4);
			dos.flush();
			dos.write(ObjectToByte(content));
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			closeSocket();
		}
	}

	public void closeSocket() {
		try {
			socket.close();
			dos.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] ObjectToByte(Object obj) {
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			bytes = bo.toByteArray();
			bo.close();
			oo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static Object ByteToObject(byte[] bytes) {
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);
			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	class heartThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					long time = System.currentTimeMillis();
					logger.info("client: {} localIp: {} localPort: {} sendMsg time: {}", id, localIp, localPort, time);
					sendMsg("Client" + id + "," + time);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	class MsgThread implements Runnable {
		@Override
		public void run() {
			int temp;
			while (true) {
				try {
					if (socket.getInputStream().available() > 0) {
						byte[] bytes = new byte[1024];
						int len = 0;
						while ((char) (temp = dis.read()) != '\n') {
							bytes[len] = (byte) temp;
							len++;
						}
						logger.info("client: {} localIp: {} localPort: {} received msg: {}", id, localIp, localPort,
								ByteToObject(bytes));
					}
				} catch (Exception e) {
					closeSocket();
				}
			}
		}
	}

	public static void main(String[] args) {
		LongConnNioSocketClient client1 = new LongConnNioSocketClient("127.0.0.1", 55555, "1");
		client1.sendMsg(new UserPojo("songfeng", 26, new ArrayList<String>()));
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LongConnNioSocketClient client2 = new LongConnNioSocketClient("127.0.0.1", 55555, "2");
	}
}
