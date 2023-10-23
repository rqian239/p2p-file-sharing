package src;

// This class stores message types and other related constants of the project

public class Constants {
    
    // Eight message types
    final private byte CHOKE = 0;
    final private byte UNCHOKE = 1;
    final private byte INTERESTED = 2;
    final private byte NOT_INTERESTED = 3;
    final private byte HAVE = 4;
    final private byte BITFIELD = 5;
    final private byte REQUEST = 6;
    final private byte PIECE = 7;

    // Handshake message constants
    final private String ZERO_BITS = "0000000000";
    final private String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";


    // Getters
    public byte getChoke() {
        return CHOKE;
    }

    public byte getUNCHOKE() {
        return UNCHOKE;
    }

    public byte getINTERESTED() {
        return INTERESTED;
    }

    public byte getNOT_INTERESTED() {
        return NOT_INTERESTED;
    }

    public byte getHAVE() {
        return HAVE;
    }

    public byte getBITFIELD() {
        return BITFIELD;
    }

    public byte getREQUEST() {
        return REQUEST;
    }

    public byte getPIECE() {
        return PIECE;
    }

    public String getZERO_BITS() {
        return ZERO_BITS;
    }

    public String getHANDSHAKE_HEADER() {
        return HANDSHAKE_HEADER;
    }

}
