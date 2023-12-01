import messages.Handshake;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    Socket socket;

    int thisPeerID;
    int connectedPeerID;

    public Client(int thisPeerID) {
        this.thisPeerID = thisPeerID;
    }

    public void connect(Peer connectToThisPeer) throws IOException {
        this.socket = new Socket(connectToThisPeer.getHostname(), connectToThisPeer.getPort());
        sendHandshake(socket, thisPeerID, connectToThisPeer);
//        sendBitfield(socket, connectToThisPeer);
        System.out.println("[" + RunPeer.getCurrentTime() + "]: Peer [" + thisPeerID + "] makes a connection to Peer [" + connectToThisPeer.getPeerID() + "]. ");

    }

    private static void sendHandshake(Socket socket, int thisPeerID, Peer connectToThisPeer) {
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

}
