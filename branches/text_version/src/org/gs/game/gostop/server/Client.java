//<Ŭ���̾�Ʈ : Client.java>

import java.net.*;
import java.io.*;

class Client
{
        static final String SERVER_HOST="127.0.0.1"; // ������ ���ͳ� �ּҸ� �����Ѵ�.
        static int SERVER_PORT=4321;               // ������ ��Ʈ�� �����Ѵ�.
        InputStream input = null;
        PrintStream output = null;
        public Client()
        {
                byte bytes[] = new byte[4096];
                Socket server = null;
                int c;
                try {
                        server = new Socket(SERVER_HOST, SERVER_PORT);
                        // ������ �ּҿ� ��Ʈ�� �̿��Ͽ� ������ ������ �� ������ ������� �Ѵ�.
                        input = server.getInputStream(); // ����� ���Ͽ��� �Է� ��Ʈ���� ��´�.
                        output = new PrintStream(server.getOutputStream());
                        // ����� ���Ͽ��� ���� ��� ��Ʈ������ PrintStream �ν��Ͻ��� �����Ѵ�.
                        int nbytes;
                        byte b[] = new byte[1024]; // �Է��� ������ ����
                        while((nbytes = input.read(b,0,1024))!= -1)
                        // ���� b�� ������ 0, �ִ� 1024 ����Ʈ�� �о���δ�.
                        {
                                String str = new String(b,0,0,nbytes);
                                // byte �迭�� ��Ʈ������ �ٲ۴�.
                                System.out.println("Received from server: " + str);
                        }
                } catch(Exception exception) {
                        System.err.println("Exception:\n" + exception);
                        try {
                                server.close(); // ���ܰ� �߻��ϸ� ����� ������ �ݴ´�.
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

