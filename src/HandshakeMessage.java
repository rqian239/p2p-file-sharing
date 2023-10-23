package src;

import java.io.Serializable;

public class HandshakeMessage implements Serializable {
    
    private int peerID;
    private char[] zeroBits;
    private char[] handshakeHeader;

    HandshakeMessage(int peerID) {
        this.peerID = peerID;
        this.zeroBits = Constants.getZERO_BITS().toCharArray();
        this.handshakeHeader = Constants.getHANDSHAKE_HEADER().toCharArray();
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int id) {
        this.peerID = id;
    }

}
