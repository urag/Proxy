package com.ura.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelStateEvent;
import io.netty.channel.ExceptionEvent;
import io.netty.channel.MessageEvent;
import io.netty.channel.SimpleChannelUpstreamHandler;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ura.installator.Installator;
import com.ura.proxy.HexDumpProxy;

public class WebSocketHandler extends SimpleChannelUpstreamHandler {

	Logger logger = Logger.getLogger(WebSocketHandler.class);
	Installator installator = new Installator();

	public WebSocketHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final String WEBSOCKET_PATH = "/websocket";
	private WebSocketServerHandshaker handshaker;

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelClosed(ctx, e);
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelOpen(ctx, e);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		super.messageReceived(ctx, e);
		Object frame = e.getMessage();
		try {
			if (frame instanceof HttpRequest) {
				HttpRequest req = (HttpRequest) frame;
				handleHttpShakeHand(ctx, req);

			} else if (frame instanceof WebSocketFrame) {
				handleMessage(frame);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Handles message by passing right parameters to installer
	 * 
	 * @param frame
	 */
	private void handleMessage(Object frame) {
		TextWebSocketFrame textframe = (TextWebSocketFrame) frame;
		JSONObject jsonObject = JSONObject.fromObject(textframe.getText());
		String type = jsonObject.getString("datatype");
		HexDumpProxy proxy = createProxyFromJson(jsonObject);
		if (type.equals("proxyupdate"))
			installator.installProxy(proxy);
		else if (type.equals("proxyremove"))
			installator.uninstallProxy(proxy);
	}

	private HexDumpProxy createProxyFromJson(JSONObject jsonObject) {
		int localPort = Integer.valueOf(jsonObject.getString("fromport"));
		String remoteHost = jsonObject.getString("toip");
		int remotePort = Integer.valueOf(jsonObject.getString("toport"));
		String id = jsonObject.getString("id");
		HexDumpProxy proxy = new HexDumpProxy(localPort, remoteHost, remotePort);
		proxy.setId(id);
		return proxy;
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		super.channelDisconnected(ctx, e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		super.exceptionCaught(ctx, e);
		logger.error("Exception ... closing the chanel");
		ctx.getChannel().close();
	}

	private void handleHttpShakeHand(ChannelHandlerContext ctx, HttpRequest req)
			throws Exception {

		// Handshake
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				this.getWebSocketLocation(req), null, false);
		this.handshaker = wsFactory.newHandshaker(req);
		if (this.handshaker == null) {
			wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
		} else {
			this.handshaker.performOpeningHandshake(ctx.getChannel(), req);
		}

		return;
	}

	private String getWebSocketLocation(HttpRequest req) {
		if (req.getHeader(HttpHeaders.Names.ORIGIN).startsWith("https:")) {
			return "wss://" + req.getHeader(HttpHeaders.Names.HOST)
					+ WEBSOCKET_PATH;
		} else {
			return "ws://" + req.getHeader(HttpHeaders.Names.HOST)
					+ WEBSOCKET_PATH;
		}
	}

}