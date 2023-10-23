package src;

// This class stores message types and other related constants of the project

public class Constants {
    
    // Eight message types
    final private static byte CHOKE = 0;
    final private static byte UNCHOKE = 1;
    final private static byte INTERESTED = 2;
    final private static byte NOT_INTERESTED = 3;
    final private static byte HAVE = 4;
    final private static byte BITFIELD = 5;
    final private static byte REQUEST = 6;
    final private static byte PIECE = 7;

    // Handshake message constants
    final private static String ZERO_BITS = "0000000000";
    final private  String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";


    // Getters
    public static byte getChoke() {
        return CHOKE;
    }

    public static byte getUNCHOKE() {
        return UNCHOKE;
    }

    public static byte getINTERESTED() {
        return INTERESTED;
    }

    public static byte getNOT_INTERESTED() {
        return NOT_INTERESTED;
    }

    public static byte getHAVE() {
        return HAVE;
    }

    public static byte getBITFIELD() {
        return BITFIELD;
    }

    public static byte getREQUEST() {
        return REQUEST;
    }

    public static byte getPIECE() {
        return PIECE;
    }

    public static String getZERO_BITS() {
        return ZERO_BITS;
    }

    public static String getHANDSHAKE_HEADER() {
        return HANDSHAKE_HEADER;
    }

}
