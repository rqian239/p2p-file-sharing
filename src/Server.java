import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private final int sPort;  //The server will be listening on this port number
    private final int thisPeerID;

    // TODO: make a connectedTo and connectedFrom list
    ArrayList<ConnectionHandler> connectedFrom;

    ExecutorService pool = Executors.newCachedThreadPool();

    public Server(int sPort, int thisPeerID) {
        this.sPort = sPort;
        this.thisPeerID = thisPeerID;
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
                ConnectionHandler connectionHandlerForIncomingConnection = new ConnectionHandler(clientSocket, thisPeerID);
                connectedFrom.add(connectionHandlerForIncomingConnection);

                pool.execute(connectionHandlerForIncomingConnection);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
