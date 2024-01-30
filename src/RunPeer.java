import messages.Message;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RunPeer {
    static int numberOfPreferredNeighbors;
    static int unchokingInterval;
    static int optimisticUnchokingInterval;
    static String dataFilename;
    static int fileSize;
    static int pieceSize;
    final Peer thisPeer;
    static int hasFilePeerID;
    static int numTotalPieces;
    static int lastPeerToConnect = 0;
    Server server;

    static boolean runningTimer = false;
    static ConcurrentHashMap<Integer, Peer> allPeers;
    static ConcurrentHashMap<Integer, ConnectionHandler> allConnections;
    static ConcurrentHashMap<Integer, BitSet> allBitmaps;
    // Threads
    Thread serverThread;

    static ConcurrentHashMap<Integer,Double> downloadingRatesMap = new ConcurrentHashMap<>();
    static ArrayList<Integer> preffNeighbors;
    public static ConcurrentHashMap<Integer, Boolean> chokedNeighbors = new ConcurrentHashMap<>();
    static int optimisticPeerNeighbor;
    static ConcurrentHashMap<Integer, Integer> sentPiecesMap = new ConcurrentHashMap<>();

    //TODO: Thread-safe set for requesting next piece
    static ConcurrentHashMap<Integer, Byte> piecesWeDontHave; // the key is the piece index, the byte has no meaning (I wanted a thread-safe set but Java only has ConcurrentHashMap)

    //TODO: Thread-safe boolean for termination
    static boolean sentTermination = false;
    static boolean neighborsSent = false;

    // Constructor
    public RunPeer(int peerID) {
        // Create Hashmap to hold all peer objects
        allPeers = new ConcurrentHashMap<>();

        // Create Hashmap to hold all clients
        allConnections = new ConcurrentHashMap<>();

        // Create Hashmap to hold all bitmaps
        allBitmaps = new ConcurrentHashMap<>();

        // Parse configs
        parseCommonConfig();
        parsePeerInfo();

        // Set current peer object
        thisPeer = allPeers.get(peerID);
        if(thisPeer == null) {
            throw new RuntimeException("Invalid peerID: inputted peerID not found in PeerInfo.cfg!");
        }

        // Set up piecesWeDontHave HashMap
        piecesWeDontHave = new ConcurrentHashMap<>();
        if(!thisPeer.isHasFile()) {
            for(int i = 0; i < calculateNumPieces(); i++) {
                piecesWeDontHave.put(i, (byte)0);
            }
        }

        // Print out log to show that peer has been set up
        System.out.println("Peer " + thisPeer.getPeerID() + " start. Set peer variables");
        System.out.println("Hostname: " + thisPeer.getHostname());
        System.out.println("Port Number: " + thisPeer.getPort());
        System.out.println("Has File? " + thisPeer.isHasFile());
        System.out.println("----------------------------------------------------------------");

        // Create server object to start listening to connections
        server = new Server(thisPeer.getPort(), thisPeer.getPeerID());

        preffNeighbors = new ArrayList<>(numberOfPreferredNeighbors);
        for (int i : allConnections.keySet()) {
            chokedNeighbors.put(i, true);
        }
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
                    newClient.connect(allPeers.get(connectToThisID));
                    newClient.listenForIncomingMessages();
                    allConnections.put(connectToThisID, newClient.getConnectionHandler());
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

        // Print out logs
        System.out.println("COMMON CONFIG VARIABLES SET.");
        System.out.println("NumberOfPreferredNeighbors: " + numberOfPreferredNeighbors);
        System.out.println("UnchokingInterval: " + unchokingInterval);
        System.out.println("OptimisticUnchokingInterval: " + optimisticUnchokingInterval);
        System.out.println("FileName: " + dataFilename);
        System.out.println("FileSize: " + fileSize);
        System.out.println("PieceSize: " + pieceSize);
        numTotalPieces = calculateNumPieces();
        System.out.println("CALCULATED NUMBER OF PIECES: " + numTotalPieces);
        System.out.println("----------------------------------------------------------------");
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
                allBitmaps.put(peer.getPeerID(), peer.getBitmap());
//                Peer current = allPeers.get(peerID);
//                System.out.println("PARSED PEER INFO : " + current.getPeerID() + " " + current.getHostname() + " " + current.getPort() + " " + current.isHasFile());

                if(hasFile) {
                    hasFilePeerID = peer.getPeerID();
                }

                if(peer.getPeerID() > lastPeerToConnect) {
                    lastPeerToConnect = peer.getPeerID();
                }

                makePeersDirectory(peer.getPeerID());

            }
            scnr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void makePeersDirectory(int peerID) {

        Path directoryPath = Paths.get(peerID + "/");

        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
//                System.out.println("Directory created successfully.");
            } catch (IOException e) {
                System.out.println("Failed to create directory: " + e.getMessage());
            }
        } else {
//            System.out.println("Directory already exists.");
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

    public static synchronized int getRandomPieceIndex() {

//        Iterator<Integer> iterator = piecesWeDontHave.keySet().iterator();
//
//        if(iterator.hasNext()){
//            try {
//                int index = iterator.next();
//                piecesWeDontHave.remove(index);
//                return index;
//            } catch (NoSuchElementException e) {
//                // We run out of pieces we don't have
//                return -1;
//            }
//        } else {
//            // RETURN -1 IF WE GOT ALL THE PIECES
//            return -1;
//        }

        Set<Integer> setOfMissingPieces = piecesWeDontHave.keySet();

        while(!piecesWeDontHave.isEmpty()){
            try {
                int index = selectRandomIndexFromSet(setOfMissingPieces);
                piecesWeDontHave.remove(index);
                return index;
            } catch (NoSuchElementException e) {
                // Pick a different index
            }
        }

        return -1;

    }

    public static synchronized void replaceBitMap(int peerID, BitSet bitmap) {
        allBitmaps.put(peerID, bitmap);
    }

    public static synchronized void setBitsInBitMap(int peerID, int pieceIndex, boolean setValue) {
        allBitmaps.get(peerID).set(pieceIndex, setValue);
    }

    public static synchronized BitSet getBitmap(int peerID) {
        return allBitmaps.get(peerID);
    }

    public static int selectRandomIndexFromSet(Set<Integer> set) {
        int size = set.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(Integer index : set)
        {
            if (i == item) {
                return index;
            }
            i++;
        }

        return -1;
    }

    public static synchronized boolean sendTerminationToAll() throws IOException {

        if(!sentTermination) {

            Message terminationMessage = new Message(Constants.TERMINATE);

            for(Integer peerID : allConnections.keySet()) {

                Socket sendToThisSocket = allConnections.get(peerID).socket;
                byte[] terminationMessageBytes = terminationMessage.createMessageBytes();
                OutputStream outputStream = sendToThisSocket.getOutputStream();
                outputStream.write(terminationMessageBytes);

            }

        }

        return true;

    }

    public static void terminate() {
        System.out.println(Logger.allPeersHaveTheFile());
        System.exit(0);
    }

}
