import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class RunPeer {

    int numberOfPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String dataFilename;
    int fileSize;
    int pieceSize;

    Peer thisPeer;

    public RunPeer(int peerID) {

    }

    public void parseCommonConfig() {
        // Parse Common.cfg file
        Properties properties = new Properties();
        try(FileInputStream in = new FileInputStream(Constants.COMMON_CONFIG_FILE)) {
            properties.load(in);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.numberOfPreferredNeighbors = Integer.parseInt(properties.getProperty("NumberOfPreferredNeighbors"));
        this.unchokingInterval = Integer.parseInt(properties.getProperty("UnchokingInterval"));
        this.optimisticUnchokingInterval = Integer.parseInt(properties.getProperty("OptimisticUnchokingInterval"));
        this.dataFilename = properties.getProperty("FileName");
        this.fileSize = Integer.parseInt(properties.getProperty("FileSize"));
        this.pieceSize = Integer.parseInt(properties.getProperty("PieceSize"));

    }

    public void parsePeerInfo(Hashtable<Integer, Peer> Peers){
        // File peerConfig = new File("../PeerInfo.cfg");

//        Scanner scnr = new Scanner("../PeerInfo.cfg");
//        while(scnr.hasNextLine()) {
//            String line = scnr.nextLine();
//            String [] variables = line.split(" ");
//            int peerID = Integer.parseInt(variables[0]);
//            String hostname = variables[1];
//            int port = Integer.parseInt(variables[2]);
//            boolean hasFile = Integer.parseInt(variables[3]) == 1;
//            Peer peer = new Peer(peerID, hostname, port, hasFile, 306);
//            System.out.println("Peer stuff" + peer.getPeerID() + " " + peer.getHostname() + " " + peer.getPort() + " " + peer.isHasFile());
//            Peers.put(peer.getPeerID(), peer);
//        }
//        scnr.close();
    }
}
