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

import static io.netty.channel.Channels.pipeline;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineFactory;
import io.netty.handler.codec.http.HttpChunkAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author <a href="http://www.veebsbraindump.com/">Vibul Imtarnasan</a>
 * @author Tamir Klein
 * 
 * @version $Rev$, $Date$
 * This is a code which was taken from Netty project and enable the server class to perform lookup on Impl logic
 * For more information on Netty project refer to the attached url's
 */

public class WebSocketServerPipelineFactory implements ChannelPipelineFactory {

	private WebSocketHandler webSocketHandler;
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		
		ChannelPipeline pipeline = pipeline();
		
		// Create a default pipeline implementation.
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("handler", webSocketHandler);
		return pipeline;
	}
	
	public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}
	
}
