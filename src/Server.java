import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int sPort;  //The server will be listening on this port number

    public Server(int sPort) {
        this.sPort = sPort;
    }

    public void run() {

        System.out.println("Server has started on port " + sPort + "...");
        try {
            ServerSocket serverSocket = new ServerSocket(sPort);

            // Listen for incoming connections
            while(true) {

                // Accept a new connection
                Socket clientSocket = serverSocket.accept();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
