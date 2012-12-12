/*
 * Copyright 2010 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ura.websocket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;


/**
 * A HTTP server which serves Web Socket requests in our application
 * This server support different web socket specification
 * versions and will work with:
 * 
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>
 * </ul>
 * 
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author <a href="http://www.veebsbraindump.com/">Vibul Imtarnasan</a>
 * @author Tamir Klein
 * 
 * @version $Rev$, $Date$
 */
public class WebSocketServer {
	
	private Logger logger = Logger.getLogger(WebSocketServer.class);
	//private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServer.class);
	
	private static Channel mainChannel;
	private static NioServerSocketChannelFactory factory;
	
	private WebSocketServerPipelineFactory webSocketServerPipelineFactory;
	
	public void start(int port) {
		
		logger.debug("Starting WS server thread");
		
	
		
		//ConsoleHandler ch = new ConsoleHandler();
		//ch.setLevel(Level.FINE);
		//Logger.getLogger("").addHandler(ch);
		//Logger.getLogger("").setLevel(Level.FINE);
		
		Executor bossExecutor = Executors.newCachedThreadPool();
		Executor workerExecutor = Executors.newCachedThreadPool();
		
		factory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
		
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(factory);

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(webSocketServerPipelineFactory);

		// Bind and start to accept incoming connections.
		mainChannel = bootstrap.bind(new InetSocketAddress(port));
		//ChannelFuture channelFuture = mainChannel.getCloseFuture();
		//channelFuture.awaitUninterruptibly();
		//channelFuture.getCause();
		logger.info("Finished starting port: " + port + " is now open");
		
	}
	
	public  void stop() {
		
		final String funcName = "stop - ";
		logger.trace(funcName + "start");
		
        ChannelFuture closeChannelFuture = mainChannel.getCloseFuture();
		ChannelFuture channelFuture = mainChannel.close();
		channelFuture.awaitUninterruptibly();
        closeChannelFuture.awaitUninterruptibly();
        factory.releaseExternalResources();
        
		logger.trace(funcName + "end");
	}

	public void setWebSocketServerPipelineFactory(WebSocketServerPipelineFactory webSocketServerPipelineFactory) {
		this.webSocketServerPipelineFactory = webSocketServerPipelineFactory;
	}
	
}
