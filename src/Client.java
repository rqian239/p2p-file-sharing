import messages.Handshake;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;

    private final int thisPeerID;
    private int connectedPeerID;

    private ConnectionHandler connectionHandler;
    private Thread listenThread;

    public Client(int thisPeerID) {
        this.thisPeerID = thisPeerID;
    }

    public void connect(Peer connectToThisPeer) throws IOException {
        this.socket = new Socket(connectToThisPeer.getHostname(), connectToThisPeer.getPort());
        sendHandshake(socket, thisPeerID);
        System.out.println("[" + RunPeer.getCurrentTime() + "]: Peer [" + thisPeerID + "] makes a connection to Peer [" + connectToThisPeer.getPeerID() + "]. ");

    }

    public void sendHandshake(Socket socket, int thisPeerID) {
        Handshake handshake = new Handshake(thisPeerID);
        byte[] handshakeBytes = handshake.createHandshakeMessage();
        try {
            OutputStream out = socket.getOutputStream();
            out.write(handshakeBytes);
            out.flush();
//            out.close();  // TODO: Should we close this socket?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForIncomingMessages() {
        connectionHandler = new ConnectionHandler(socket, thisPeerID);
        connectionHandler.setClient(this);
        listenThread = new Thread(connectionHandler);
        listenThread.start();
    }

    public void sendMessage(Socket socket, byte[] messageBytes) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(messageBytes);
    }

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }
}
