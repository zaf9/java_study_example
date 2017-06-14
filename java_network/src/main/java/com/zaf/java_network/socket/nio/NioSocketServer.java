package com.zaf.java_network.socket.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaf.java_network.socket.commons.MyRequestObject;
import com.zaf.java_network.socket.commons.MyResponseObject;

public class NioSocketServer {
	
	private static final Logger logger = LoggerFactory.getLogger(NioSocketServer.class);

    public static void main(String[] args) throws InterruptedException {  
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
            	logger.info("begin selector.select > 0");
                // Someone is ready for I/O, get the ready keys  
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();  
      
                // Walk through the ready keys collection and process date requests.  
                while (it.hasNext()) {
                	logger.info("begin it.hasNext()");
                    SelectionKey readyKey = it.next();  
                    it.remove();  
                      
                    // The key indexes into the selector so you  
                    // can retrieve the socket that's ready for I/O  
                    execute((ServerSocketChannel) readyKey.channel());
                    logger.info("finish it.hasNext().");
                }
                logger.info("finish selector.select > 0");
            } 
            logger.info("exit selector.select() > 0");
        } catch (ClosedChannelException ex) {  
            logger.error("ClosedChannelException:",ex);
        } catch (IOException ex) {  
        	logger.error("IOException:",ex);
        } finally {  
            try {  
                selector.close();  
            } catch(Exception ex) {}  
            try {  
                serverSocketChannel.close();  
            } catch(Exception ex) {}  
        }  
    }  
  
    private static void execute(ServerSocketChannel serverSocketChannel) throws IOException, InterruptedException {  
        SocketChannel socketChannel = null;  
        try {  
            socketChannel = serverSocketChannel.accept();  
            MyRequestObject myRequestObject = receiveData(socketChannel);  
            logger.info("execute receiveData myRequestObject: {}",myRequestObject);  
              
            MyResponseObject myResponseObject = new MyResponseObject(  
                    "response for " + myRequestObject.getName(),   
                    "response for " + myRequestObject.getValue()); 
//            Thread.sleep(100000);
            sendData(socketChannel, myResponseObject);  
            logger.info("execute sendData myResponseObject: {}",myResponseObject); 
        } finally {  
            try {  
                socketChannel.close();  
            } catch(Exception ex) {}  
        }  
    }  
      
    private static MyRequestObject receiveData(SocketChannel socketChannel) throws IOException {  
        MyRequestObject myRequestObject = null;  
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
            myRequestObject = (MyRequestObject)obj;  
        } finally {  
            try {  
                baos.close();  
            } catch(Exception ex) {}  
        }  
        return myRequestObject;  
    }  
  
    private static void sendData(SocketChannel socketChannel, MyResponseObject myResponseObject) throws IOException {  
        byte[] bytes = SerializableUtil.toBytes(myResponseObject);  
        ByteBuffer buffer = ByteBuffer.wrap(bytes);  
        socketChannel.write(buffer);  
    }  
}
