//<���� : GenericServer.java>

/*      Author: Thomas Lea  (leat@goodnet.com,  http://www.goodnet.com/~leat)
 *      Date: 12/7/95
 *      Version: 1.0  (for Beta JDK API)
 */
import java.net.*;
import java.io.*;
import java.util.Vector;

class GenericServer
{
        private static final int DEFAULT_PORT=4321;  // ������ bind�ϴ� �ʱ� ��Ʈ ��
        private ConnectionManager cm = null;       // ������ �����ϴ� ������
        public GenericServer(int port)
        {
                System.out.println("Server is initializing to port " + port);
                cm = new ConnectionManager(port); 
                // ������ �����ϴ� ������ ����.
                cm.start();
                // ConnectionManager�� run() ���� ȣ��
        }
        public static void main(String args[])
        {
                int server_port;
                try {
                        server_port = Integer.parseInt(args[0],10);
                        // ù ��° ���ڷ� ���� ��Ʈ�� �޴´�.
                } catch(Exception e) {
                        System.out.println("Defaulting to port " + DEFAULT_PORT);
                        server_port = DEFAULT_PORT;
                        // ù ��° ���ڰ� ���ų� �߸� �ԷµǸ� ��Ʈ�� �⺻������
                }
                new GenericServer(server_port); // ���� ����
        }
}

/* ConnectionManager�� ������ ��ٸ��ٰ� Ŭ���̾�Ʈ�� �����ؿ��� �ش� Ŭ���̾�Ʈ�� ó���� ServerConnection 
�����带 �����Ѵ�. ������ �������� ��Ƽ�÷����̴�. */
class ConnectionManager extends Thread
{
        private static int _port;
        private static Vector _my_threads = new Vector(5,2); 
        /* �ʱ�ġ 5, ����ġ 2�� ���͸� �����Ѵ�. �� ���Ϳ��� ������ ����Ǹ� �����Ǵ� ServerConnection ����
�� �ν��Ͻ����� ����ȴ�. */
        private ServerSocket _main_socket = null;
        public ConnectionManager(int port)
        {
                _port = port;
        }
        public void run()
        {
                serveRequests();
                // ���� ������ �����Ͽ� ���� ������ ���ٰ� Ŭ���̾�Ʈ���� �����ؿ��� ������ �����Ͽ�
                // ������ ���ῡ �ϳ��� �����带 ������ִ� ��ƾ�̴�.
        }
        private void serveRequests()
        {
                try {
                        _main_socket = new ServerSocket(_port); // ���� ���� ����
                } catch(Exception e) {
                        System.err.println(e); System.exit(1);
                }
                ServerConnection temp_sc = null;
                while (true)     // ���� ����. �� ������ ��Ʈ�� ������ ������ ��Ʈ�̴�.
                {
                        try {
                                Socket this_connection = _main_socket.accept();
                                // ���� ������ Ŭ���̾�Ʈ�� ��ٷȴٰ� ������ �����Ѵ�.
                                // accept()�� ��ȯ���� Socket �ν��Ͻ��̰� ������ ����� Ŭ���̾�Ʈ��
                                // ��ȭ�� �� �ν��Ͻ��� ���ؼ� �Ѵ�.
                                temp_sc = new ServerConnection(this_connection);
                                // ������ �����Ǹ� �� Socket �ν��Ͻ��� ServerConnection �����带 ����
                                temp_sc.start();
                                // ServerConnection �ν��Ͻ��� run() ȣ��
                                _my_threads.addElement(temp_sc);
                                // ServerConnection �ν��Ͻ��� ���Ϳ� �߰�
/* ������ ũ�⸸ŭ ������ �ݺ��Ѵ�. ���Ϳ� ����Ǿ� �ִ� ��  ServerConnection �����尡 ���� ������ �˻��ϰ� 
�����尡 �����Ͽ����� ���Ϳ��� �����Ѵ�. ServerConnection ������� ������ �����ϸ� ������ �ߴ��Ѵ�. */
                                for(int i=0;i<ConnectionManager._my_threads.size();i++)
                                        if(!((ServerConnection)(_my_threads.elementAt(i))).isAlive())
                                                _my_threads.removeElementAt(i);
                        } catch(Exception e) {
                                System.err.println("Exception:\n" + e);
                        }
                }
        }
}

class ServerConnection extends Thread // ������ ������ �� ���� Ŭ���̾�Ʈ�� ��ȭ�� �ϴ� ������
{
	private Socket _mysocket;
	private PrintStream _output;
	private InputStream _input;
	public ServerConnection(Socket s)
	{
		_mysocket = s;  // Ŭ���̾�Ʈ�� ������ �� ���� �ν��Ͻ��� �Ѱ� �޴´�.
	}
	private void doServerWork()
	{
		/* ���� ������ �ϴ� �ϵ��� ���⿡ ����. �� ���� ������ �� ������ �����Ѵ�.
		 * �� ���� �����带 �����Ѵ�.(��, stop()�� ȣ��)
		 * ���⼭�� �����ϰ� �޽����� PrintStream���� 10�� ��������.
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
				// Math.random()�� 0.0�� 1.0 ������ ���� ���� �߻�
			}
		} catch(Exception e) { }
	}
	public void run()
	{
		System.out.println("Connected to: " + _mysocket.getInetAddress() +":"+ _mysocket.getPort());
		// ����� ������ ���ͳ� �ּҿ� ��Ʈ�� ǥ�� ������� ����Ѵ�.
		try {
			_output = new PrintStream(_mysocket.getOutputStream());
			/* ����� ������ ��� ��Ʈ������ PrintStream�� �����Ѵ�. ������ ��� ��Ʈ����
			 * �������� Ŭ���̾�Ʈ�� ���޵Ǵ� ��Ʈ���̴�.
			 * �ݴ�� �Է� ��Ʈ���� Ŭ���̾�Ʈ���� ������ ���޵Ǵ� ��Ʈ���̴�.
			 */
			_input = _mysocket.getInputStream(); // �Է� ��Ʈ���� ��´�.
			// �����μ��� ���� �Ѵ�.
			doServerWork();
			// �۾��� ������ ������ �����Ѵ�.
			_mysocket.close();
		} catch ( Exception e ) {
			System.err.println( "Exception:\n" + e );
		}
		System.out.println("Disconnecting: " + _mysocket.getInetAddress() +":"+ _mysocket.getPort());
		stop();  // �����带 �ߴ��Ѵ�.
		/* �������� stop() ������ ThreadDeath ��ü �ν��Ͻ��� ���� ��ü���� ���� �ش�.
		 * ThreadDeath ��ü�� Exception Ŭ�������� �Ļ��� ���� �ƴ϶� Error Ŭ��������
		 * �Ļ��Ǿ���. Ư���� ���� ���ٸ� catch�� �� �ʿ䰡 ����.
		 * catch�� ���� ������ �ߴܵ� �����尡 ������ ����ȴ�.
		 */
	}
}

