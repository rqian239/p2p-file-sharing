import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private final int sPort;  //The server will be listening on this port number

    // TODO: make a connectedTo and connectedFrom list
    ArrayList<ServerConnectionHandler> connectedFrom;

    ExecutorService pool = Executors.newCachedThreadPool();

    public Server(int sPort) {
        this.sPort = sPort;
        connectedFrom = new ArrayList<>();
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(sPort);
            System.out.println("SERVER started on port " + sPort);

            // Listen for incoming connections
            while (true) {
                // Accept a new connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("SERVER on port " + sPort + " connected to client!");
                ServerConnectionHandler clientThread = new ServerConnectionHandler(clientSocket);
                connectedFrom.add(clientThread);

                pool.execute(clientThread);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
