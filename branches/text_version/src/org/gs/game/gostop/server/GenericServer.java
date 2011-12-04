//<서버 : GenericServer.java>

/*      Author: Thomas Lea  (leat@goodnet.com,  http://www.goodnet.com/~leat)
 *      Date: 12/7/95
 *      Version: 1.0  (for Beta JDK API)
 */
import java.net.*;
import java.io.*;
import java.util.Vector;

class GenericServer
{
        private static final int DEFAULT_PORT=4321;  // 서버가 bind하는 초기 포트 값
        private ConnectionManager cm = null;       // 연결을 관리하는 쓰레드
        public GenericServer(int port)
        {
                System.out.println("Server is initializing to port " + port);
                cm = new ConnectionManager(port); 
                // 접속을 관리하는 쓰레드 생성.
                cm.start();
                // ConnectionManager의 run() 도구 호출
        }
        public static void main(String args[])
        {
                int server_port;
                try {
                        server_port = Integer.parseInt(args[0],10);
                        // 첫 번째 인자로 서버 포트를 받는다.
                } catch(Exception e) {
                        System.out.println("Defaulting to port " + DEFAULT_PORT);
                        server_port = DEFAULT_PORT;
                        // 첫 번째 인자가 없거나 잘못 입력되면 포트를 기본값으로
                }
                new GenericServer(server_port); // 서버 생성
        }
}

/* ConnectionManager는 접속을 기다리다가 클라이언트가 연결해오면 해당 클라이언트를 처리할 ServerConnection 
쓰레드를 생성한다. 일종의 동기적인 멀티플렉싱이다. */
class ConnectionManager extends Thread
{
        private static int _port;
        private static Vector _my_threads = new Vector(5,2); 
        /* 초기치 5, 증가치 2인 벡터를 생성한다. 이 벡터에는 소켓이 연결되면 생성되는 ServerConnection 쓰레
드 인스턴스들이 저장된다. */
        private ServerSocket _main_socket = null;
        public ConnectionManager(int port)
        {
                _port = port;
        }
        public void run()
        {
                serveRequests();
                // 서버 소켓을 생성하여 무한 루프를 돌다가 클라이언트들이 접속해오면 연결을 생성하여
                // 각각의 연결에 하나의 쓰레드를 만들어주는 루틴이다.
        }
        private void serveRequests()
        {
                try {
                        _main_socket = new ServerSocket(_port); // 서버 소켓 생성
                } catch(Exception e) {
                        System.err.println(e); System.exit(1);
                }
                ServerConnection temp_sc = null;
                while (true)     // 무한 루프. 이 루프의 포트는 지정한 서버의 포트이다.
                {
                        try {
                                Socket this_connection = _main_socket.accept();
                                // 서버 소켓이 클라이언트를 기다렸다가 연결을 생성한다.
                                // accept()의 반환값은 Socket 인스턴스이고 앞으로 연결된 클라이언트와
                                // 대화는 이 인스턴스를 통해서 한다.
                                temp_sc = new ServerConnection(this_connection);
                                // 연결이 생성되면 이 Socket 인스턴스로 ServerConnection 쓰레드를 생성
                                temp_sc.start();
                                // ServerConnection 인스턴스의 run() 호출
                                _my_threads.addElement(temp_sc);
                                // ServerConnection 인스턴스를 벡터에 추가
/* 벡터의 크기만큼 루프를 반복한다. 벡터에 저장되어 있는 각  ServerConnection 쓰레드가 실행 중인지 검사하고 
쓰레드가 종료하였으면 벡터에서 제거한다. ServerConnection 쓰레드는 소켓을 종료하면 실행을 중단한다. */
                                for(int i=0;i<ConnectionManager._my_threads.size();i++)
                                        if(!((ServerConnection)(_my_threads.elementAt(i))).isAlive())
                                                _my_threads.removeElementAt(i);
                        } catch(Exception e) {
                                System.err.println("Exception:\n" + e);
                        }
                }
        }
}

class ServerConnection extends Thread // 연결이 생성된 후 실제 클라이언트와 대화를 하는 쓰레드
{
	private Socket _mysocket;
	private PrintStream _output;
	private InputStream _input;
	public ServerConnection(Socket s)
	{
		_mysocket = s;  // 클라이언트와 연결이 된 소켓 인스턴스를 넘겨 받는다.
	}
	private void doServerWork()
	{
		/* 실제 서버가 하는 일들이 여기에 들어간다. 이 일을 수행한 후 소켓을 종료한다.
		 * 그 다음 쓰레드를 종료한다.(즉, stop()을 호출)
		 * 여기서는 간단하게 메시지를 PrintStream으로 10번 내보낸다.
		 */
		try {
			for(int i=0;i<10;i++)
			{
				//_output.println("This is a message from the server");
				String s;
				while ((s = _input.readLine()) != null) {
					_output.println(s);
				}
				//                               sleep((int)(Math.random() * 4000));
				// Math.random()은 0.0과 1.0 사이의 수를 난수 발생
			}
		} catch(Exception e) { }
	}
	public void run()
	{
		System.out.println("Connected to: " + _mysocket.getInetAddress() +":"+ _mysocket.getPort());
		// 연결된 소켓의 인터넷 주소와 포트를 표준 출력으로 출력한다.
		try {
			_output = new PrintStream(_mysocket.getOutputStream());
			/* 연결된 소켓의 출력 스트림으로 PrintStream을 생성한다. 소켓의 출력 스트림은
			 * 서버에서 클라이언트로 전달되는 스트림이다.
			 * 반대로 입력 스트림은 클라이언트에서 서버로 전달되는 스트림이다.
			 */
			_input = _mysocket.getInputStream(); // 입력 스트림을 얻는다.
			// 서버로서의 일을 한다.
			doServerWork();
			// 작업이 끝나면 소켓을 종료한다.
			_mysocket.close();
		} catch ( Exception e ) {
			System.err.println( "Exception:\n" + e );
		}
		System.out.println("Disconnecting: " + _mysocket.getInetAddress() +":"+ _mysocket.getPort());
		stop();  // 쓰레드를 중단한다.
		/* 쓰레드의 stop() 도구는 ThreadDeath 객체 인스턴스를 목적 객체에게 던져 준다.
		 * ThreadDeath 객체는 Exception 클래스에서 파생된 것이 아니라 Error 클래스에서
		 * 파생되었다. 특별한 일이 없다면 catch를 할 필요가 없다.
		 * catch를 하지 않으면 중단된 쓰레드가 실제로 종료된다.
		 */
	}
}

