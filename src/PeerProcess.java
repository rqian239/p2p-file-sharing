import messages.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;


public class PeerProcess {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java PeerProcess <peerID>");
            System.exit(1);
        }

        int peerID = Integer.parseInt(args[0]);

        // Create a Peer object
        Peer peer = createPeer(peerID);

        // Start the peer process
        try {
            startPeerProcess(peer);
        } catch (IOException e) {
            System.err.println("Error starting the peer process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Peer createPeer(int peerID) {
        
        String hostname = "localhost";  // Update with actual hostname
        int port = 6008;  // Update with actual port
        boolean hasFile = false;  // Update based on configuration file
        int numPieces = 306;  // Update based on configuration file

        return new Peer(peerID, hostname, port, hasFile, numPieces);
    }

    private static void startPeerProcess(Peer peer) throws IOException {
        
        Socket socket = new Socket(peer.getHostname(), peer.getPort());
        sendHandshake(socket, peer);
        sendBitfield(socket, peer);
        System.out.println("Peer " + peer.getPeerID() + " started successfully!");
    }

    private static void sendHandshake(Socket socket, Peer peer) {
        messages.Handshake handshake = new messages.Handshake(peer.getPeerID());
        byte[] handshakeBytes = handshake.createHandshakeMessage();
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(handshakeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void sendBitfield(Socket socket, Peer peer) {
        try {
            // Convert the BitSet to a byte array
            byte[] bitfieldBytes = peer.getBitmap().toByteArray();

            // Prepare the message
            int messageLength = 1 + bitfieldBytes.length;  // 1 byte for message type
            ByteBuffer messageBuffer = ByteBuffer.allocate(4 + messageLength);
            messageBuffer.putInt(messageLength);  // Message length
            messageBuffer.put((byte) Constants.BITFIELD);  // Message type
            messageBuffer.put(bitfieldBytes);  // Bitfield

            // Send the message
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageBuffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}