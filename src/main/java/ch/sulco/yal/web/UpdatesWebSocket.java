package ch.sulco.yal.web;

import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class UpdatesWebSocket {
	private final static Logger log = Logger.getLogger(UpdatesWebSocket.class.getName());

	private Session session;

	private static UpdatesWebSocket instance;

	public UpdatesWebSocket() {
		instance = this;
	}

	public static UpdatesWebSocket getInstance() {
		if(instance == null){
			new UpdatesWebSocket();
		}
		return instance;
	}

	@OnWebSocketConnect
	public void connected(Session session) {
		this.session = session;
	}

	@OnWebSocketClose
	public void closed(int statusCode, String reason) {
		this.session = null;
	}

	@OnWebSocketMessage
	public void message(String message) throws IOException {
		log.info("Got: " + message);
		this.session.getRemote().sendString(message);
	}

	public void send(String message) {
		try {
			if (this.session != null)
				this.session.getRemote().sendString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
