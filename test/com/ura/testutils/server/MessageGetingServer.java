package com.ura.testutils.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.channel.Channels;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * Util class for testing.
 * The purpose of this class is to listen on specified port 
 * @author Ura
 *
 */
public class MessageGetingServer {
	Logger logger = Logger.getLogger(MessageGetingServer.class);

	public void start(int port) {
		ChannelFactory factory = new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(),new StringEncoder(),new MessageHolderHandler());
			}
		});
		
		 bootstrap.setOption("child.tcpNoDelay", true);
         bootstrap.setOption("child.keepAlive", true);
         bootstrap.bind(new InetSocketAddress(port));
	}

}
