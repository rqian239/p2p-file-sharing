import messages.Handshake;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class RunPeer {

    int numberOfPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String dataFilename;
    int fileSize;
    int pieceSize;

    final Peer thisPeer;

    Server server;

    static ConcurrentHashMap<Integer, Peer> allPeers;

    // Threads
    Thread serverThread;

    // Constructor
    public RunPeer(int peerID) {
        // Create Hashmap to hold all peer objects
        allPeers = new ConcurrentHashMap<>();

        // Parse configs
        parseCommonConfig();
        parsePeerInfo();

        // Set current peer object
        thisPeer = allPeers.get(peerID);

        // Create server object to start listening to connections
        server = new Server(thisPeer.getPort(), thisPeer.getPeerID());

        if(thisPeer == null) {
            throw new RuntimeException("Invalid peerID: inputted peerID not found in PeerInfo.cfg!");
        }
    }

    public void run() {

        // Get current peer ID
        int thisPeerID = thisPeer.getPeerID();

        // Start listening
        serverThread = new Thread(server);
        serverThread.start();

        // Connect to every previous peer
        for(Integer id : allPeers.keySet()) {
            if(id < thisPeerID) {
                try {
                    connect(allPeers.get(id));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

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

    public static String getCurrentTime() {
        // Get the current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // Format the current time
        return currentTime.format(formatter);
    }

    private void connect(Peer connectToThisPeer) throws IOException {
        Socket socket = new Socket(connectToThisPeer.getHostname(), connectToThisPeer.getPort());
        sendHandshake(socket, thisPeer.getPeerID(), connectToThisPeer);
//        sendBitfield(socket, connectToThisPeer);
        System.out.println("[" + getCurrentTime() + "]: Peer [" + thisPeer.getPeerID() + "] makes a connection to Peer [" + connectToThisPeer.getPeerID() + "]. ");

    }

    private static void sendHandshake(Socket socket, int thisPeerID, Peer connectToThisPeer) {
        Handshake handshake = new Handshake(thisPeerID);
        byte[] handshakeBytes = handshake.createHandshakeMessage();
        try {
            OutputStream out = socket.getOutputStream();
            out.write(handshakeBytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
