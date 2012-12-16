package com.ura.testutils.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelStateEvent;
import io.netty.channel.ExceptionEvent;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelHandler;

import org.apache.log4j.Logger;

public class MessageSendingHandler extends SimpleChannelHandler {

	Logger logger = Logger.getLogger(MessageSendingClient.class);
	private static String message ;
	
	public static String getMessage(){
		return message;
	}
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelConnected(ctx, e);
		logger.info("Client connected");
		e.getChannel().write("Hello World");
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
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}