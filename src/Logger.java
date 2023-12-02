import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {


    private static int currpeerID;
    public Logger(int peerID) {
        // this.currpeerID = peerID;
        //eventually write to file

        String logFileName = "log_peer_" + peerID + ".log";

    }
    
    //true for receivehandshake and false for sendhandshake pretty sure
    //can get rid of peerID1 if currpeerID will always be peerID1
    public static String logConnection(int peerID1, int peerID2, boolean connectedFrom) {
        String logMessage;
        if (connectedFrom) {
            //receives handshake
            logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " is connected from Peer " + peerID2 + ".";
        } 
        else {
            //sendshandshake
            logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " makes a connection to Peer " + peerID2 + ".";
        }
        return logMessage;    
    }

    //pass in neighbors as ints, may change into peers later? 
    public static String logChangePreferredNeighbors(int peerID1, int[] preferredNeighborIDs) {
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has the preferred neighbors ";
        StringBuilder neighborsList = new StringBuilder();
        for (int i = 0; i < preferredNeighborIDs.length; i++) {
            neighborsList.append(preferredNeighborIDs[i]);
            if (i != preferredNeighborIDs.length - 1) {
                neighborsList.append(", ");
            }
        }
        logMessage += "[" + neighborsList + "].";
        return logMessage;

    }
    public static String logChangeOptUnchokeNeighbor(int peerID1, int optimisticUnchokedNeighbor){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has the optimistically unchoked neighbor " + optimisticUnchokedNeighbor;
        return logMessage;
    }

    public static String logReceiveUnchoke(int peerID1, int peerID2){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " is unchoked by " + peerID2;
        return logMessage;
    }
    
    public static String logReceiveChoke(int peerID1, int peerID2){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " is choked by " + peerID2;
        return logMessage;
    }

    public static String logReceiveHave(int peerID1, int peerID2, int pieceIndex){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " received the 'have' message from " + peerID2 + " for the piece " + pieceIndex;
        return logMessage;
    }

    public static String logReceiveInterested(int recipientPeer, int senderPeer){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + recipientPeer + " received the 'interested' message from " + senderPeer;
        return logMessage;
    }

    public static String logReceiveNotInterested(int recipientPeer, int senderPeer){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + recipientPeer + " received the 'not interested' message from " + senderPeer;
        return logMessage;
    }

    public static String logPieceRequestedFrom(int peerID1, int peerID2, int pieceIndex) {
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has requested the piece " + pieceIndex +
                " from " + peerID2 + ".";
        return logMessage;

    }

    public static String logPieceDownloadedFrom(int peerID1, int peerID2, int pieceIndex, int totNumPieces) {
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has downloaded the piece " + pieceIndex +
                " from " + peerID2 + ". Now the number of pieces it has is " + totNumPieces + ".";
        return logMessage;

    }

    public static String logFileDownloaded(int peerID1){
        String logMessage = "[" + getCurrentTime() + "]: Peer " + peerID1 + " has downloaded the complete file.";
        return logMessage;
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
