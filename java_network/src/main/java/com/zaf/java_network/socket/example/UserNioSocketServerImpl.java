package com.zaf.java_network.socket.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.nio.SerializableUtil;

public class UserNioSocketServerImpl implements IUserSocketServer {

	private static final Logger logger = LoggerFactory.getLogger(UserNioSocketServerImpl.class);

	private int port;

	public UserNioSocketServerImpl(int port) {
		this.port = port;
	}

	@Override
	public void startup() throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Selector selector = null;
				ServerSocketChannel serverSocketChannel = null;

				try {
					selector = Selector.open();
					serverSocketChannel = ServerSocketChannel.open();
					serverSocketChannel.configureBlocking(false);

					serverSocketChannel.socket().setReuseAddress(true);
					serverSocketChannel.socket().bind(new InetSocketAddress(port));

					serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

					while (selector.select() > 0) {
						try {
							Iterator<SelectionKey> it = selector.selectedKeys().iterator();
							while (it.hasNext()) {
								SelectionKey readyKey = it.next();
								it.remove();
								invoke((ServerSocketChannel) readyKey.channel());
							}
						} catch (Exception ex) {
							logger.error("Exception:", ex);
						}
					}
				} catch (Exception ex) {
					logger.error("Exception:", ex);
				} finally {
					try {
						selector.close();
					} catch (Exception ex) {
					}
					try {
						serverSocketChannel.close();
					} catch (Exception ex) {
					}
				}
			}
		}).start();

	}

	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub

	}

	private void invoke(ServerSocketChannel serverSocketChannel) throws Exception {
		SocketChannel socketChannel = null;
		try {
			socketChannel = serverSocketChannel.accept();
			IUserRequest myRequest = receiveData(socketChannel);
			IUserResponse myResponse = execute(myRequest);
			sendData(socketChannel, myResponse);
		} finally {
			try {
				socketChannel.close();
			} catch (Exception ex) {
			}
		}
	}

	private IUserResponse execute(IUserRequest request) throws Exception {
		Class clazz = request.getRequestClass();

		String methodName = request.getRequestMethod();
		Class<?>[] parameterTypes = request.getRequestParameterTypes();
		Method method = clazz.getDeclaredMethod(methodName, parameterTypes);

		Object[] parameterValues = request.getRequestParameterValues();
		final Object obj = method.invoke(clazz.newInstance(), parameterValues);

		return new GenericUserResponseImpl(obj);
	}

	private IUserRequest receiveData(SocketChannel socketChannel) throws IOException {
		IUserRequest myRequest = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		try {
			byte[] bytes;
			int size = 0;
			while ((size = socketChannel.read(buffer)) >= 0) {
				buffer.flip();
				bytes = new byte[size];
				buffer.get(bytes);
				baos.write(bytes);
				buffer.clear();
			}
			bytes = baos.toByteArray();
			Object obj = SerializableUtil.toObject(bytes);
			myRequest = (IUserRequest) obj;
		} finally {
			try {
				baos.close();
			} catch (Exception ex) {
			}
		}
		return myRequest;
	}

	private void sendData(SocketChannel socketChannel, IUserResponse myResponse) throws IOException {
		byte[] bytes = SerializableUtil.toBytes(myResponse);
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		socketChannel.write(buffer);
	}
}
