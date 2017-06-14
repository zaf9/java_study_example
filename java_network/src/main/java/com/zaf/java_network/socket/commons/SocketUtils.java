package com.zaf.java_network.socket.commons;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public final class SocketUtils {

	private static final String zeroIp = "0.0.0.0";
	private static final int zeroPort = 0;

	private SocketUtils() {

	}

	public final static String getRemoteIp(final SocketChannel channel) {
		return getRemoteIp(channel.socket());
	}

	public final static int getRemotePort(final SocketChannel channel) {
		return getRemotePort(channel.socket());
	}

	public final static String getLocalIp(final SocketChannel channel) {
		return getLocalIp(channel.socket());
	}

	public final static int getLocalPort(final SocketChannel channel) {
		return getLocalPort(channel.socket());

	}

	public final static String getRemoteIp(final Socket socket) {
		return getIp((InetSocketAddress) socket.getRemoteSocketAddress());
	}

	public final static int getRemotePort(final Socket socket) {
		return getPort((InetSocketAddress) socket.getRemoteSocketAddress());

	}

	public final static String getLocalIp(final Socket socket) {
		return getIp((InetSocketAddress) socket.getLocalSocketAddress());
	}

	public final static int getLocalPort(final Socket socket) {
		return getPort((InetSocketAddress) socket.getLocalSocketAddress());
	}

	private final static String getIp(InetSocketAddress inetSocketAddress) {
		if (inetSocketAddress == null)
			return zeroIp;
		else
			return inetSocketAddress.getAddress().getHostAddress();
	}

	private final static int getPort(InetSocketAddress inetSocketAddress) {

		if (inetSocketAddress == null)
			return zeroPort;
		else
			return inetSocketAddress.getPort();
	}
}
