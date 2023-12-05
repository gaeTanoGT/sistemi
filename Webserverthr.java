import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

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

class ClientHandler extends Thread{
    private Socket socket;

    public ClientHandler(Socket s){
        socket = s;
        start();
    }

    @Override
    public void run(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()
            ));
            PrintStream out = new PrintStream(new BufferedOutputStream(
                    socket.getOutputStream()
            ));
            String s = in.readLine();
            System.out.println("richiesta: "+ s);
            String fileName = "";
            StringTokenizer st = new StringTokenizer(s);
            try {
                if(st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET") && st.hasMoreElements()){
                    fileName = st.nextToken();
                    System.out.println("filename = " +fileName);
                }else {
                    throw new FileNotFoundException();
                }
                if(fileName.endsWith("/"))
                    fileName += "index.html";
                while (fileName.indexOf('|') >= 0)
                    throw new FileNotFoundException();
                if(new File(fileName).isDirectory()){
                    fileName= fileName.replace('\\', '/');
                    out.print("HTTP/1.0 301 Moved Permanently\r\n" + "Location:/" + fileName + "/\r\n\r\n");
                    out.close();
                    return;
                }

                System.out.println("filename to open = " + fileName);
                InputStream f = new FileInputStream(fileName);
                String mineType = "text/plain";
                if(fileName.endsWith(".html") || fileName.endsWith(".htm"))
                    mineType = "html";
                else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
                    mineType="image/jpeg";
                else if (fileName.endsWith(".gif"))
                    mineType="image/gif";
                else if (fileName.endsWith(".class"))
                    mineType="application/octet-stream";
                out.print("HTTP/1.0 200 OK \r\n" + "Content-type: " + mineType + "\r\n\r\n");

                byte[] a = new byte[1604096];
                int n;
                while((n=f.read(a)) > 0)
                    out.write(a, 0, n);
                out.close();
            }catch (FileNotFoundException x){
                out.println("HTTP/1.0 404 Not Found\r\n" + "Content-type: text/html\r\n\r\n" + "<html><head></head><body>"+fileName+" not found</body><html>\n");
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }
}

