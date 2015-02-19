package ba.bitcamp.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ba.bitcamp.logger.Logger;

public class ConnectionListener {
	private static final int port = 8080;

	public static void main(String[] args) {

		ExecutorService pool = Executors.newFixedThreadPool(10);
		HashMap<String, String> logs = new HashMap<String, String>();
		logs.put("applicationLog", "applicationLog");
		logs.put("warning", "warning");
		logs.put("error", "error");
		try {
			new Logger(logs);
		} catch (FileNotFoundException e1) {
			System.err.println("Could not initialize logger");
			System.exit(0);
		}

		try {
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket client = server.accept();
				Logger.log("applicationLog", client.getInetAddress()
						.getHostAddress() + " just connected.");
				pool.submit(new Connection(client));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}