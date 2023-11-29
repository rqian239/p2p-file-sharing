import java.io.IOException;

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
        
        System.out.println("Peer " + peer.getPeerID() + " started successfully!");
    }
}