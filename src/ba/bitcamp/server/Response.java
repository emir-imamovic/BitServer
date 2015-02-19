package ba.bitcamp.server;

import java.io.PrintStream;

public class Response {
	
	
	private static void sendContent(PrintStream write, String content) {
		write.println("Content-Type: text/html");
		write.println();
		write.println(content);
	}

	public static void ok(PrintStream write, String content) {
		write.println("HTTP/1.1 200 OK");
		sendContent(write, content);
	}

	public static void error(PrintStream write, String content) {
		write.println("HTTP/1.1 404 Not Found");
		sendContent(write, content);
	}

	public static void serverError(PrintStream write, String content) {
		write.println("HTTP/1.1 500 Internal Server Error");
		sendContent(write, content);
	}
}
