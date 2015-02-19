package ba.bitcamp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import ba.bitcamp.logger.Logger;

public class Connection implements Runnable {

	private Socket client;

	/**
	 * @param client
	 */
	public Connection(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {

		InputStream is = null;
		BufferedReader br = null;
		PrintStream write = null;
		try {
			is = client.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
			write = new PrintStream(client.getOutputStream());
		} catch (IOException e) {
			Logger.log("error", e.getMessage());
			closeClient();
			return;

		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				if (line.contains("GET") || line.isEmpty()) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!line.contains("GET")) {
			Logger.log("warning", "Was not GET request");
			Response.error(write, "Invalid request");
			closeClient();
			return;
		}
		String fileName = getFileName(line);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			Response.error(write, "This is not the page you are looking for");
			Logger.log("warning",
					"Client requested missing file " + e.getMessage());
			closeClient();
			return;
		}
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(
				fis));
		String fileLine = "";
		StringBuilder sb = new StringBuilder();
		try {
			while ((fileLine = fileReader.readLine()) != null) {
				sb.append(fileLine);
			}
		} catch (IOException e) {
			Logger.log("error", e.getMessage());
			Response.serverError(write, "A well trained group of monkeys is trying to fix the problem");
			closeClient();
			return;
			
		}
		Response.ok(write, sb.toString());
		closeClient();

	}

	private String getFileName(String request) {
		String[] parts = request.split(" ");
		String fileName = null;
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equals("GET")) {
				fileName = parts[i + 1];
				break;
			}
		}

		String basePath = "." + File.separator + "html" + File.separator;
		if (fileName == null || fileName.equals("/"))
			return basePath + "index.html";

		if (!(fileName.contains("."))) {
			fileName += ".html";
		}
		return basePath + fileName;
	}

	private void closeClient() {
		try {
			client.close();
		} catch (IOException e) {
			Logger.log("warning", e.getMessage());
		}
	}
}
