import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Webserverthr {
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        System.out.println("**SERVER IN ESECUZIONE");

        serverSocket = new ServerSocket(80);

        while (true){
            try {
                Socket s = serverSocket.accept();
                System.out.println("connesso a: " + s.getRemoteSocketAddress());
                new ClientHandler(s);
            }catch (Exception e){};
        }
    }
}
