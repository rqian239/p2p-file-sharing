import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ConnectionHandler implements Runnable {

    Socket socket;
    int connectedPeerID;
    int thisPeerID;

    Client client = null;

    boolean handshakeReceived = false;

    private int currentConnectionState;

    public ConnectionHandler(Socket socket, int thisPeerID) {
        this.socket = socket;
        this.thisPeerID = thisPeerID;
    }

    @Override
    public void run() {
        System.out.println("Socket handler is working...");

        // Expect to receive handshake
        try {
//            if(!handshakeReceived) {
//                receiveHandshake();
//                if(client == null) {
//                    createClient();
//                }
//                returnHandshake();
//            }
//            switch(currentConnectionState) {
//
//                // This case occurs when this peer initiates a connection and waits for a handshake in return
//                case Constants.SENT_HANDSHAKE_AWAITING_HANDSHAKE:
//                    receiveHandshake();
//                    // TODO: send bitfield here
//                    break;
//
//                // This case occurs when this peer did not initiate the connection and receives a handshake from a different peer
//                case Constants.HAVE_NOT_SENT_HANDSHAKE_AWAITING_HANDSHAKE:
//                    receiveHandshake();
//                    if(client == null) {
//                        createClient();
//                    }
//                    returnHandshake();
//                    break;
//
//                case Constants.SENT_BITFIELD_AWAITING_BITFIELD:
//
//                    break;
//
//                case Constants.HAVE_NOT_SENT_BITFIELD_AWAITING_BITFIELD:
//
//
//            }

            if(!handshakeReceived) {

                receiveHandshake();
                if(client == null) {
                    createClient();
                }

                // If we receive a handshake from a peer with a lower ID
                if(connectedPeerID < thisPeerID) {
                    // TODO: return a bitfield
                } else {
                    returnHandshake();
                }


            } else {

                // TODO: Read other messages

            }

        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ConnectionHandler exiting...");

    }

    public void returnHandshake() {
        client.sendHandshake(socket, thisPeerID);
    }

    public void createClient() {
        client = new Client(thisPeerID);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setConnectionState(int connectionState) {
        this.currentConnectionState = connectionState;
    }

    public void receiveHandshake() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        // Expecting a 32-byte handshake message
        byte[] receivedHandshake = new byte[32];
        in.readFully(receivedHandshake);

        // Validate the handshake message
        if (isValidHandshake(receivedHandshake)) {
            handshakeReceived = true;
            System.out.println("[" + RunPeer.getCurrentTime() + "]: Peer [" + thisPeerID + "] received a handshake from Peer [" + connectedPeerID + "]. ");
        } else {
            System.err.println("INVALID HANDSHAKE RECEIVED FROM PEER " + connectedPeerID);
        }

    }

    private boolean isValidHandshake(byte[] handshake) {
        String header = new String(handshake, 0, 18);

        // Check if the header is correct and peer ID matches expected value
        connectedPeerID = ByteBuffer.wrap(handshake, 28, 4).getInt();
        return header.equals("P2PFILESHARINGPROJ");
    }

    
}
