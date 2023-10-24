package main.java.p2p;
import java.io.Serializable;


// This is a class that holds the HandshakeMessage
public class HandshakeMessage implements Serializable {
    
    private int peerID;
    private char[] zeroBits;
    private char[] handshakeHeader;

    // Constructor: create a handshake message with a peerID
    HandshakeMessage(int peerID) {
        this.peerID = peerID;
        this.zeroBits = Constants.getZERO_BITS().toCharArray();
        this.handshakeHeader = Constants.getHANDSHAKE_HEADER().toCharArray();
    }

    // Getters

    public int getPeerID() {
        return peerID;
    }

    // Setters

    public void setPeerID(int id) {
        this.peerID = id;
    }

}
