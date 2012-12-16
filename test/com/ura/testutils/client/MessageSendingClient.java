package com.ura.testutils.client;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.channel.Channels;
import io.netty.channel.socket.nio.NioClientSocketChannelFactory;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class MessageSendingClient {
	
	public void start(InetSocketAddress address){
         ChannelFactory factory =
             new NioClientSocketChannelFactory(
                     Executors.newCachedThreadPool(),
                     Executors.newCachedThreadPool());
 
         ClientBootstrap bootstrap = new ClientBootstrap(factory);
 
         bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
             public ChannelPipeline getPipeline() {
                 return Channels.pipeline(new StringEncoder(),new StringDecoder(),new MessageSendingHandler());
             }
         });
         
         bootstrap.setOption("tcpNoDelay", true);
         bootstrap.setOption("keepAlive", true);
 
         bootstrap.connect(address);
     }
}
