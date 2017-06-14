package com.zaf.java_network.socket.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiThreadSocketFileServer {

	private static final Logger logger = LoggerFactory.getLogger(MultiThreadSocketFileServer.class);

	private static final String sendFileName = "./src/main/java/com/zaf/java_study/socket/file/MultiThreadSocketFileClient.java";

	public static void main(String[] args) {
		Selector selector = null;
		ServerSocketChannel serverSocketChannel = null;

		try {
			// Selector for incoming time requests
			selector = Selector.open();

			// Create a new server socket and set to non blocking mode
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);

			// Bind the server socket to the local host and port
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.socket().bind(new InetSocketAddress(9999));

			// Register accepts on the server socket with the selector. This
			// step tells the selector that the socket wants to be put on the
			// ready list when accept operations occur, so allowing multiplexed
			// non-blocking I/O to take place.
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			// Here's where everything happens. The select method will
			// return when any operations registered above have occurred, the
			// thread has been interrupted, etc.
			while (selector.select() > 0) {
				// Someone is ready for I/O, get the ready keys
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();

				// Walk through the ready keys collection and process date
				// requests.
				while (it.hasNext()) {
					SelectionKey readyKey = it.next();
					it.remove();

					// The key indexes into the selector so you
					// can retrieve the socket that's ready for I/O
					doit((ServerSocketChannel) readyKey.channel());
				}
			}
		} catch (ClosedChannelException ex) {
			logger.info("ClosedChannelException:", ex);
		} catch (IOException ex) {
			logger.info("IOException:", ex);
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

	private static void doit(final ServerSocketChannel serverSocketChannel) throws IOException {

		File file = new File(sendFileName);
		if (!file.exists()) {
			logger.error("send file: {} don't exist!exit!", sendFileName);
			System.exit(2);
		}

		SocketChannel socketChannel = null;
		try {
			socketChannel = serverSocketChannel.accept();

			receiveFile(socketChannel, new File("c:/temp/server_receive.log"));
			sendFile(socketChannel, file);
		} finally {
			try {
				socketChannel.close();
			} catch (Exception ex) {
			}
		}

	}

	private static void receiveFile(SocketChannel socketChannel, File file) throws IOException {
		FileOutputStream fos = null;
		FileChannel channel = null;

		try {
			fos = new FileOutputStream(file);
			channel = fos.getChannel();
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

			int size = 0;
			while ((size = socketChannel.read(buffer)) != -1) {
				buffer.flip();
				if (size > 0) {
					buffer.limit(size);
					channel.write(buffer);
					buffer.clear();
				}
			}
		} finally {
			try {
				channel.close();
			} catch (Exception ex) {
			}
			try {
				fos.close();
			} catch (Exception ex) {
			}
		}
	}

	private static void sendFile(SocketChannel socketChannel, File file) throws IOException {
		FileInputStream fis = null;
		FileChannel channel = null;
		logger.info("sendFile name: {} size: {}",file.getAbsolutePath(),file.length());
		try {
			fis = new FileInputStream(file);
			channel = fis.getChannel();
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
			int size = 0;
			while ((size = channel.read(buffer)) != -1) {
				buffer.rewind();
				buffer.limit(size);
				socketChannel.write(buffer);
				buffer.clear();
			}
			socketChannel.socket().shutdownOutput();
		} finally {
			try {
				channel.close();
			} catch (Exception ex) {
			}
			try {
				fis.close();
			} catch (Exception ex) {
			}
		}
	}
}
