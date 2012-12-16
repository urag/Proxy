  /*
    * Copyright 2012 The Netty Project
    *
   * The Netty Project licenses this file to you under the Apache License,
    * version 2.0 (the "License"); you may not use this file except in compliance
    * with the License. You may obtain a copy of the License at:
    *
    *   http://www.apache.org/licenses/LICENSE-2.0
    *
   * Unless required by applicable law or agreed to in writing, software
   * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
   * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
   * License for the specific language governing permissions and limitations
   * under the License.
   */

  package com.ura.proxy;
  import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.ClientSocketChannelFactory;
import io.netty.channel.socket.nio.NioClientSocketChannelFactory;
import io.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
  
  public class HexDumpProxy {
	  private String id ;
	  Logger logger = Logger.getLogger(HexDumpProxy.class);
   

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + localPort;
		result = prime * result
				+ ((remoteHost == null) ? 0 : remoteHost.hashCode());
		result = prime * result + remotePort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HexDumpProxy other = (HexDumpProxy) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (localPort != other.localPort)
			return false;
		if (remoteHost == null) {
			if (other.remoteHost != null)
				return false;
		} else if (!remoteHost.equals(other.remoteHost))
			return false;
		if (remotePort != other.remotePort)
			return false;
		return true;
	}

	public String getId() {
		return id;
	}

	public void setId(String name) {
		this.id = name;
	}

	private final int localPort;
      private final String remoteHost;
      public int getLocalPort() {
		return localPort;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public int getRemotePort() {
		return remotePort;
	}

	private final int remotePort;
      private ClientSocketChannelFactory factory;
      private Channel mainChannel ;
      public HexDumpProxy(String id ,int localPort, String remoteHost, int remotePort) {
          this.localPort = localPort;
          this.remoteHost = remoteHost;
          this.remotePort = remotePort;
          this.id = id;
      }
  
     
	public void start() {
          logger.info(
                  "Proxying *:" + localPort + " to " +
                  remoteHost + ':' + remotePort + " ...");
  
          // Configure the bootstrap.
          Executor executor = Executors.newCachedThreadPool();
          ServerBootstrap sb = new ServerBootstrap(
                  new NioServerSocketChannelFactory(executor, executor));
  
         // Set up the event pipeline factory.
          factory =
                  new NioClientSocketChannelFactory(executor, executor);
  
          sb.setPipelineFactory(
                  new HexDumpProxyPipelineFactory(factory, remoteHost, remotePort));
  
          // Start up the server.
          mainChannel = sb.bind(new InetSocketAddress(localPort));
          
      }
      
     
     public void stop(){
    	 logger.info(
                 "Stoping proxying *:" + localPort + " to " +
                 remoteHost + ':' + remotePort + " ...");
 
    	ChannelFuture closeChannelFuture = mainChannel.getCloseFuture();
 		ChannelFuture channelFuture = mainChannel.close();
 		channelFuture.awaitUninterruptibly();
        closeChannelFuture.awaitUninterruptibly();
        factory.releaseExternalResources();
     }
  
  }