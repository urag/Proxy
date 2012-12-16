package com.ura.testutils.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ExceptionEvent;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelHandler;

import org.apache.log4j.Logger;
/**
 * Util class.
 * The purpose of this class is to handle message that comes to server and hold it for farther assertion.
 * @author Ura
 *
 */
public class MessageHolderHandler extends SimpleChannelHandler {

	private static String message ;
	Logger logger = Logger.getLogger(MessageHolderHandler.class);
	
	public static String getMessage(){
		return message;
	}
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		super.messageReceived(ctx, e);
		String messageF = (String) e.getMessage();
		logger.info("Message recievd");
			message = messageF;
			logger.info("Message is "+message);
			e.getChannel().write("Hi frend !!!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, e);
	}
}
