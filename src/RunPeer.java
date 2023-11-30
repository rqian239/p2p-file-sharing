import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

public class RunPeer {

    int numberOfPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String dataFilename;
    int fileSize;
    int pieceSize;

    Peer thisPeer;

    HashMap<Integer, Peer> allPeers;

    // Constructor
    public RunPeer(int peerID) {
        allPeers = new HashMap<>();
        parseCommonConfig();
        parsePeerInfo();
        thisPeer = allPeers.get(peerID);
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

    public void parsePeerInfo(){
        File peerConfigFile = new File(Constants.PEER_INFO_CONFIG_FILE);

        try {
            Scanner scnr = new Scanner(peerConfigFile);
            while (scnr.hasNextLine()) {
                String line = scnr.nextLine();
                String[] variables = line.split(" ");
                int peerID = Integer.parseInt(variables[0]);
                String hostname = variables[1];
                int port = Integer.parseInt(variables[2]);
                boolean hasFile = Integer.parseInt(variables[3]) == 1;
                Peer peer = new Peer(peerID, hostname, port, hasFile, calculateNumPieces());
                allPeers.put(peer.getPeerID(), peer);
                Peer current = allPeers.get(peerID);
                System.out.println("PARSED PEER INFO : " + current.getPeerID() + " " + current.getHostname() + " " + current.getPort() + " " + current.isHasFile());
            }
            scnr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int calculateNumPieces() {
        return (int) Math.ceil((double)fileSize / (double)pieceSize);
    }
}
