package example.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import gui.play.MakeRoom;


public class Client {
	private static final int PORT = 12345;
	private MakeRoom makeRoom;
	public Client() {
		makeRoom = new MakeRoom(this);
		makeRoom.display();
		
		System.out.println("Input your Nickname");
		Scanner s = new Scanner(System.in);
		String nick = s.nextLine();
		System.out.println("Enjoy>>>>>>>>>>");
		try {
			Socket socket = new Socket("192.168.0.6", PORT);

			PrintWriter pw = new PrintWriter(socket.getOutputStream());

			// pw = new PrintWriter(socket.getOutputStream());
			pw.println(nick);
			pw.flush(); // 첫 실행시 닉네임 설정을 위한 전송

			GetStr gs = new GetStr(socket); // 서버로 부터 데이터를 받기 위한 쓰레드
			gs.start();
			SendStr ss = new SendStr(socket, nick); // 서버로 채팅을 보내기 위한클래
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Client();
	}

	class SendStr {
		private Socket socket;
		private PrintWriter pw;
		private String nick;
		// private Play play;

		SendStr(Socket socket, String nick) {
			this.socket = socket;
			this.nick = nick;

			try {
				pw = new PrintWriter(socket.getOutputStream()); // 서버에게 보내기 위해 스트림을 얻는
				go_chat();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void go_chat() {
			BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in)); // 키보드 입력을 위한 버
			String line = "";
			try {
				while ((line = keyboard.readLine()) != null) {
					pw.println(line);
					pw.flush();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String user_draw() {// 사용자가 그림을 그릴때 서버로 좌표값을 전송하는 메소드

			return "";
		}

	}

	class GetStr extends Thread { // 클라이언트는 서버로 부터 정보를 받아서 그린다.
		private Socket socket;
		private BufferedReader br;
		private StringTokenizer st;

		GetStr(Socket socket) {
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
			String msg = "";
			int num = 0;
			String message = "";
			try {
				while (true) {
					if ((msg = br.readLine()) != null) {
						st = new StringTokenizer(msg, "#");
						num = Integer.parseInt(st.nextToken());
						
						message = st.nextToken();
						
						switch (num) {
						case 200:
							st = new StringTokenizer(message,"#");
							String id = st.nextToken();
							if(st.hasMoreTokens()) {
								String note = st.nextToken();	
								System.out.println("note : "+note);
							}
							
							
							makeRoom.lb_userName1.setText(id);
							break;
						}
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
