//<클라이언트 : Client.java>

import java.net.*;
import java.io.*;

class Client
{
        static final String SERVER_HOST="127.0.0.1"; // 서버의 인터넷 주소를 지정한다.
        static int SERVER_PORT=4321;               // 서버의 포트를 지정한다.
        InputStream input = null;
        PrintStream output = null;
        public Client()
        {
                byte bytes[] = new byte[4096];
                Socket server = null;
                int c;
                try {
                        server = new Socket(SERVER_HOST, SERVER_PORT);
                        // 지정된 주소와 포트를 이용하여 소켓을 생성한 후 서버에 연결까지 한다.
                        input = server.getInputStream(); // 연결된 소켓에서 입력 스트림을 얻는다.
                        output = new PrintStream(server.getOutputStream());
                        // 연결된 소켓에서 얻은 출력 스트림으로 PrintStream 인스턴스를 생성한다.
                        int nbytes;
                        byte b[] = new byte[1024]; // 입력을 저장할 버퍼
                        while((nbytes = input.read(b,0,1024))!= -1)
                        // 버퍼 b에 오프셋 0, 최대 1024 바이트를 읽어들인다.
                        {
                                String str = new String(b,0,0,nbytes);
                                // byte 배열을 스트링으로 바꾼다.
                                System.out.println("Received from server: " + str);
                        }
                } catch(Exception exception) {
                        System.err.println("Exception:\n" + exception);
                        try {
                                server.close(); // 예외가 발생하면 연결된 소켓을 닫는다.
                        } catch(Exception e) {
                                System.err.println("Exception:\n" + e);
                                System.exit(1);
                        }
                        System.exit(1);
                }
        }
        public static void main(String args[])
        {
                new Client();
        }
}

