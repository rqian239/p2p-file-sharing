import messages.*;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;


public class PeerProcess {

    // Config filenames
    static final String COMMON_CONFIG_FILE = "Common.cfg";
    static final String PEER_INFO_CONFIG_FILE = "PeerInfo.cfg";



    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("PeerID not specified!");
        }

        int peerID = Integer.parseInt(args[0]);

        // Create a Peer object
        Peer peer = createPeer(peerID);

        // Parse Common.cfg file
        Properties properties = new Properties();
        try(FileInputStream in = new FileInputStream(COMMON_CONFIG_FILE)) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numberOfPreferredNeighbors = Integer.parseInt(properties.getProperty("NumberOfPreferredNeighbors"));
        int unchokingInterval = Integer.parseInt(properties.getProperty("UnchokingInterval"));
        int optimisticUnchokingInterval = Integer.parseInt(properties.getProperty("OptimisticUnchokingInterval"));
        String dataFilename = properties.getProperty("FileName");
        int fileSize = Integer.parseInt(properties.getProperty("FileSize"));
        int pieceSize = Integer.parseInt(properties.getProperty("PieceSize"));






        // Start the peer process
        try {
            startPeerProcess(peer);
            checkInterested(peer);
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

    private static void checkInterested(Peer peer){
        for(int i = 0; i < peer.getNumPieces(); i++){
            if(peer.hasPiece(i) && ){
                System.out.println("Peer " + peer.getPeerID() + " has piece " + i);
            }
        }
    }
}