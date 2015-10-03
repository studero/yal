package ch.sulco.yal.dsp.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import ch.sulco.yal.dsp.AppConfig;
import ch.sulco.yal.dsp.Application;

public class SocketCommandReceiver extends Thread implements CommandReceiver {
	
	private final static Logger log = Logger.getLogger(SocketCommandReceiver.class.getName());

	private final AppConfig appConfig;

	private CommandListener commandListener;
	private ServerSocket socket;

	public SocketCommandReceiver(AppConfig appConfig) {
		this.appConfig = appConfig;
		try {
			log.info("Starting Server Socket on port " + this.appConfig.getCommandSocketPort());
			this.socket = new ServerSocket(this.appConfig.getCommandSocketPort());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.start();
	}

	@Override
	public void setCommandListener(CommandListener commandListener) {
		this.commandListener = commandListener;
	}
	
	@Override
	public void run() {
		try {
			Socket clientSocket = this.socket.accept();
			log.info("New Client Connection from " + clientSocket.getRemoteSocketAddress());
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String commandInput;
			while ((commandInput = inputReader.readLine()) != null) {
				// TODO: handle input
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}