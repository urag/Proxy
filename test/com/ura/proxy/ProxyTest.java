package com.ura.proxy;

import java.io.File;
import java.net.InetSocketAddress;

import org.junit.Test;

import com.ura.installator.Installator;
import com.ura.testutils.client.MessageSendingClient;
import com.ura.testutils.client.MessageSendingHandler;
import com.ura.testutils.server.MessageGetingServer;
import com.ura.testutils.server.MessageHolderHandler;
import static org.junit.Assert.assertEquals;
public class ProxyTest {

	@Test
	/**
	 * Sends message to one port an reads it from different port
	 */
	public void testSending(){
		MessageGetingServer mgs = new MessageGetingServer();
		/*Start to listen on port */
		mgs.start(9006);
		@SuppressWarnings("unused")
		Installator instalator = new Installator(new File("test/proxiesForRun.xml"));
		MessageSendingClient msc =  new MessageSendingClient();
		msc.start(new InetSocketAddress("127.0.0.1", 9005));
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String messageOnServer = MessageHolderHandler.getMessage();
		assertEquals(messageOnServer,"Hello World");
		String messageOnClient = MessageSendingHandler.getMessage();
		assertEquals(messageOnClient,"Hi frend !!!");
		
	}
}
