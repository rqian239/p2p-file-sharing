import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {


    private static int currpeerID;
    private PrintWriter logWriter;

    public Logger(int peerID) {
        this.currpeerID = peerID;        
        String logFileName = "log_peer_" + peerID + ".log";
        try {
            logWriter = new PrintWriter(new FileWriter(logFileName, true));
        } 
        catch (IOException e) {
            System.out.println("failed to create logwriter");
            e.printStackTrace();
        }

    }
    
    //true for receivehandshake and false for sendhandshake pretty sure
    //can get rid of peerID1 if currpeerID will always be peerID1
    public void logConnection(int peerID1, int peerID2, boolean connectedFrom) {
        String logMessage;
        if (connectedFrom) {
            //receives handshake
            logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " is connected from Peer " + peerID2 + ".";
        } 
        else {
            //sendshandshake
            logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " makes a connection to Peer " + peerID2 + ".";
        }
        logWriter.println(logMessage);
        logWriter.flush();
        // return logMessage;    
    }

    //pass in neighbors as ints, may change into peers later? 
    public void logChangePreferredNeighbors(int peerID1, int[] preferredNeighborIDs) {
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has the preferred neighbors ";
        StringBuilder neighborsList = new StringBuilder();
        for (int i = 0; i < preferredNeighborIDs.length; i++) {
            neighborsList.append(preferredNeighborIDs[i]);
            if (i != preferredNeighborIDs.length - 1) {
                neighborsList.append(", ");
            }
        }
        logMessage += "[" + neighborsList + "].";
        logWriter.println(logMessage);
        logWriter.flush();
    }
    public void logChangeOptUnchokeNeighbor(int peerID1, int optimisticUnchokedNeighbor){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has the optimistically unchoked neighbor " + optimisticUnchokedNeighbor;
        logWriter.println(logMessage);
        logWriter.flush();
    }

    public void logReceiveUnchoke(int peerID1, int peerID2){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " is unchoked by " + peerID2;
        logWriter.println(logMessage);
        logWriter.flush();
    }
    
    public void logReceiveChoke(int peerID1, int peerID2){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " is choked by " + peerID2;
        logWriter.println(logMessage);
        logWriter.flush();
    }

    public void logReceiveHave(int peerID1, int peerID2, int pieceIndex){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " received the 'have' message from " + peerID2 + " for the piece " + pieceIndex;
        logWriter.println(logMessage);
        logWriter.flush();
    }

    public void logReceiveInterested(int peerID1, int peerID2){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " received the ‘interested’ message from " + peerID2;
        logWriter.println(logMessage);
        logWriter.flush();
    }

    public void logReceiveNotInterested(int peerID1, int peerID2){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " received the ‘not interested’ message from " + peerID2;
        logWriter.println(logMessage);
        logWriter.flush();
    }

    public void logPieceDownloadedFrom(int peerID1, int peerID2, int pieceIndex, int totNumPieces){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has downloaded the piece " + pieceIndex + 
        " from "+ peerID2 + ". Now the number of pieces it has is " + totNumPieces + ".";
        logWriter.println(logMessage);
        logWriter.flush();

    }

    public void logFileDownloaded(int peerID1){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + currpeerID + " has downloaded the complete file.";
        logWriter.println(logMessage);
        logWriter.flush();
    }


    public static String getCurrentTime() {
        // Get the current time
        LocalDateTime currentTime = LocalDateTime.now();
        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // Format the current time
        return currentTime.format(formatter);
    }
}
