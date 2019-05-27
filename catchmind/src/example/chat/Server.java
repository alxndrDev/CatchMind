package example.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class Server {
	private static final int PORT = 12345;
	//private Socket socket;
	private ServerSocket ss;

	String nick = null;
	private BufferedReader br;

	public Server() {

	}

	public void Connection() {
		try {
			ss = new ServerSocket(PORT);
			System.out.println("Server Running....");
			HashMap<String, Object> map = new HashMap<String, Object>();
			while (true) {
				System.out.println("waiting....");
				Socket socket = ss.accept();
				
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				nick = br.readLine();
				System.out.println(nick + "접속");
				
				RunServer runServer = new RunServer(socket, nick, map);
				System.out.println("Connected to" + socket.getInetAddress());
				
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.Connection();
	}
}


class RunServer {
	private Socket socket;
	private HashMap<String, Object> map;
	private String nick = "";  
	private PrintWriter pw;
	
	RunServer(Socket socket, String nick, HashMap<String, Object> map) {
		try {
			this.map = map ;// 사용자 객체를 담을 HashMap
			this.nick = nick;
			this.socket = socket;
			
			pw = new PrintWriter(socket.getOutputStream());
			join_member(socket, pw, nick);  // HashMap에 저장 
			
			Receiver r= new Receiver(socket);
			r.start();
			
			System.out.println("runServer()");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void join_member(Socket socket, Object writer, String nick) {
		synchronized(map) {
			map.put(nick, writer);
		}
		System.out.println(map.size() + "map");
		
		System.out.println("join team");
	}

	// 클라이언트에게 전송

	
	
	// 클라이언트로 부터 받음
	class Receiver extends Thread {

		private Socket socket;
		private BufferedReader br;
		
		public Receiver(Socket socket) {
			this.socket = socket;
			try {
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override 
		public void run() {
			String str = "";
			System.out.println("Receiver Start");
				try {
					while(true) {
						if((str = br.readLine())!=null) {
							
							sendAll(str);
							System.out.println("client :" + str);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
				synchronized (map) {
					map.remove(nick);
				}
				try {
					if(socket != null) {
						socket.close();
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		// 모든 사용자에게 전파
		public void sendAll(String str) {
			int i = 0;
			synchronized (map) {
				Collection<Object> collection = map.values();
				Iterator<?> iter = collection.iterator();
				while(iter.hasNext()) {
					
					PrintWriter pw = (PrintWriter) iter.next();
					pw.println(str);
					pw.flush();
					System.out.println("send complete" + i);
					i++;
				}
			}
		}
	}
}