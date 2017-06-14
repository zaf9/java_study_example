package com.zaf.java_network.socket.longConn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.SocketUtils;
import com.zaf.java_network.socket.commons.UserPojo;

public class LongConnNioSocketServer {

	private static final Logger logger = LoggerFactory.getLogger(LongConnNioSocketServer.class);

	private Map<String, Long> heatTimeMap = new HashMap<String, Long>();

	public LongConnNioSocketServer(int port) {
		Selector selector = null;
		ServerSocketChannel serverChannel = null;

		String localIp = null;
		int localPort = 0;
		String remoteIp = null;
		int remotePort = 0;

		try {
			// 获取一个ServerSocket通道
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(port));
			// 获取通道管理器
			selector = Selector.open();
			// 将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
			// 只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			while (selector.select() > 0) {
				// 选择注册过的io操作的事件
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey readyKey = it.next();
					// 删除已选key，防止重复处理
					it.remove();
					if (readyKey.isAcceptable()) {
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) readyKey.channel();
						SocketChannel socketChannel = serverSocketChannel.accept();

						socketChannel.configureBlocking(false);
						// 连接成功后，注册接收服务器消息的事件
						socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						localIp = SocketUtils.getLocalIp(socketChannel);
						localPort = SocketUtils.getLocalPort(socketChannel);
						remoteIp = SocketUtils.getRemoteIp(socketChannel);
						remotePort = SocketUtils.getRemotePort(socketChannel);
						logger.info("isAcceptable localIp: {} localPort: {} remoteIp: {} remptePort: {}", localIp,
								localPort, remoteIp, remotePort);
					} else if (readyKey.isReadable()) {
						SocketChannel socketChannel = (SocketChannel) readyKey.channel();
						localIp = SocketUtils.getLocalIp(socketChannel);
						localPort = SocketUtils.getLocalPort(socketChannel);
						remoteIp = SocketUtils.getRemoteIp(socketChannel);
						remotePort = SocketUtils.getRemotePort(socketChannel);
						logger.info("isReadable status localIp: {} localPort: {} remoteIp: {} remptePort: {}", localIp,
								localPort, remoteIp, remotePort);

						logger.info(
								"isReadable readyKey status isValid: {} isWritable: {} isAcceptable: {} isReadable: {} isConnectable: {}",
								readyKey.isValid(), readyKey.isWritable(), readyKey.isAcceptable(),
								readyKey.isReadable(), readyKey.isConnectable());
						logger.info(
								"isReadable socketChannel status isBlocking: {} isConnected: {} isConnectionPending: {} isOpen: {} isRegistered: {}",
								socketChannel.isBlocking(), socketChannel.isConnected(),
								socketChannel.isConnectionPending(), socketChannel.isOpen(),
								socketChannel.isRegistered());
						Object obj = receiveData(socketChannel);
						String msg = "Server back:";
						if (obj instanceof String) {
							String id = obj.toString().split(",")[0];
							if (heatTimeMap.get(id) != null
									&& System.currentTimeMillis() - heatTimeMap.get(id) > 5000) {
								logger.info(
										"isReadable String channelTimeOut id: {} localIp: {} localPort: {} remoteIp: {} remptePort: {}",
										id, localIp, localPort, remoteIp, remotePort);
								socketChannel.socket().close();
							} else {
								long unixTime = System.currentTimeMillis();
								heatTimeMap.put(id, unixTime);
								logger.info(
										"isReadable String put heatTimeMap id: {} unixTime: {} localIp: {} localPort: {} remoteIp: {} remptePort: {}",
										id, unixTime, localIp, localPort, remoteIp, remotePort);
							}
							long time = System.currentTimeMillis();
							msg += time + "\n";
							sendData(socketChannel, msg);
						} else if (obj instanceof UserPojo) {
							logger.info(
									"isReadable UserPojo: {}  localIp: {} localPort: {} remoteIp: {} remptePort: {}",
									obj, localIp, localPort, remoteIp, remotePort);
							msg += ((UserPojo) obj).getName() + "\n";
							sendData(socketChannel, msg);
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				selector.close();
				if (serverChannel != null) {
					serverChannel.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Object receiveData(SocketChannel socketChannel) {
		Object obj = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteBuffer intBuffer = ByteBuffer.allocate(4);
		ByteBuffer objBuffer = ByteBuffer.allocate(1024);
		int size = 0;
		int sum = 0;
		int objlen = 0;
		byte[] bytes = null;
		try {
			while ((size = socketChannel.read(intBuffer)) > 0) {
				intBuffer.flip();
				bytes = new byte[size];
				intBuffer.get(bytes);
				baos.write(bytes);
				intBuffer.clear();
				if (bytes.length == 4) {
					objlen = bytesToInt(bytes, 0);
				}
				if (objlen > 0) {
					byte[] objByte = new byte[0];
					while (sum != objlen) {
						size = socketChannel.read(objBuffer);
						if (size > 0) {
							objBuffer.flip();
							bytes = new byte[size];
							objBuffer.get(bytes, 0, size);
							baos.write(bytes);
							objBuffer.clear();
							objByte = ArrayUtils.addAll(objByte, bytes);
							sum += bytes.length;
						}
					}
					obj = ByteToObject(objByte);
					break;
				}
			}

		} catch (Exception e) {
			logger.info("receiveData generate exception:", e);
		} finally {
			try {
				baos.close();
			} catch (Exception e) {
				logger.info("baos close generate exeption:", e);
			}
		}
		return obj;
	}

	private static void sendData(SocketChannel socketChannel, Object obj) {
		byte[] bytes = ObjectToByte(obj);

		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		try {
			socketChannel.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序。
	 * 
	 * @param ary
	 *            byte数组
	 * @param offset
	 *            从数组的第offset位开始
	 * @return int数值
	 */
	public static int bytesToInt(byte[] ary, int offset) {
		int value;
		value = (int) ((ary[offset] & 0xFF) | ((ary[offset + 1] << 8) & 0xFF00) | ((ary[offset + 2] << 16) & 0xFF0000)
				| ((ary[offset + 3] << 24) & 0xFF000000));
		return value;
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
			// e.printStackTrace();
		}
		return obj;
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

	public static void main(String[] args) {
		LongConnNioSocketServer server = new LongConnNioSocketServer(55555);
	}

}
