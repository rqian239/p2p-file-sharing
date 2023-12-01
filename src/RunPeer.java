import messages.Handshake;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
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
    static ConcurrentHashMap<Integer, Client> allClients;   //Key is peerID we connect to - Value is the client object
    // Threads
    Thread serverThread;

    // Constructor
    public RunPeer(int peerID) {
        // Create Hashmap to hold all peer objects
        allPeers = new ConcurrentHashMap<>();

        // Create Hashmap to hold all clients
        allClients = new ConcurrentHashMap<>();

        // Parse configs
        parseCommonConfig();
        parsePeerInfo();

        // Set current peer object
        thisPeer = allPeers.get(peerID);
        if(thisPeer == null) {
            throw new RuntimeException("Invalid peerID: inputted peerID not found in PeerInfo.cfg!");
        }

        // Create server object to start listening to connections
        server = new Server(thisPeer.getPort(), thisPeer.getPeerID());
    }

    public void run() {
        // Get current peer ID
        int thisPeerID = thisPeer.getPeerID();
        // Start listening
        serverThread = new Thread(server);
        serverThread.start();

        // Connect to every previous peer
        for(Integer connectToThisID : allPeers.keySet()) {
            if(connectToThisID < thisPeerID) {
                try {
                    Client newClient = new Client(thisPeerID);
                    allClients.put(connectToThisID, newClient);
                    newClient.connect(allPeers.get(connectToThisID));
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

    private static void sendBitfield(Socket socket, Peer peer) {
        try {
            // Convert the BitSet to a byte array
            byte[] bitfieldBytes = peer.getBitmap().toByteArray();

            // Prepare the message
            messages.Message bitfieldMessage = new messages.Message((byte) 5, bitfieldBytes);
            byte[] messageBytes = bitfieldMessage.createMessageBytes();

            // Send the message
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageBytes);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkInterested(Peer peer, Map<Integer, AbstractMap.SimpleEntry<Peer, Socket>> peerMap){
        for(int i = 0; i < peer.getNumPieces(); i++){
            for(Map.Entry<Integer, AbstractMap.SimpleEntry<Peer, Socket>> entry : peerMap.entrySet()){
                if(!peer.hasPiece(i) && entry.getValue().getKey().hasPiece(i)){
                    System.out.println("Peer " + peer.getPeerID() + " is interested in " + entry.getValue().getKey().getPeerID());
                    //send interested message
                    try {
                        messages.Message interested = new messages.Message((byte) 2);
                        OutputStream outputStream;
                        outputStream = entry.getValue().getValue().getOutputStream();
                        outputStream.write(interested.createMessageBytes());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("Peer " + peer.getPeerID() + " is not interested in " + entry.getValue().getKey().getPeerID());
                }
            }

        }
    }

    private static void checkReceivedBitfield(Socket socket, BitSet peerBitSet) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            // reads message length- 4 bytes
            int messageLength = in.readInt();
            // reads message type - 1 byte
            byte messageType = in.readByte();
            
            if (messageType == (byte) 5) {
                // Read the bitfield bytes
                byte[] receivedBitfield = new byte[messageLength - 1]; // 1 byte for message type
                in.readFully(receivedBitfield);
    
                BitSet receivedBitSet = BitSet.valueOf(receivedBitfield);
    
                boolean interested = false;
                for (int i = 0; i < receivedBitSet.length(); i++) {
                    if (receivedBitSet.get(i) && !peerBitSet.get(i)) {
                        interested = true;
                        break;
                    }
                }
    
                // Prepare and send the appropriate message
                OutputStream outputStream = socket.getOutputStream();
                if (interested) {
                    //send interested message
                    byte messageBytes = (byte)2; 
                    //  Write the message to the output stream
                    outputStream.write(messageBytes);
                    outputStream.flush();
                    outputStream.close();
                } 
                else {
                    //not interested
                    byte messageBytes = (byte)3; 
                    //  Write the message to the output stream
                    outputStream.write(messageBytes);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
