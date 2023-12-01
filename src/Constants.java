public class Constants {

    // Constants class should not be instantiated
    private Constants() {}

    // Message types
    public static final byte CHOKE = 0;
    public static final byte UNCHOKE = 1;
    public static final byte INTERESTED = 2;
    public static final byte NOT_INTERESTED = 3;
    public static final byte HAVE = 4;
    public static final byte BITFIELD = 5;
    public static final byte REQUEST = 6;
    public static final byte PIECE = 7;

    // Filepaths
    static final String COMMON_CONFIG_FILE = "../Common.cfg";
    static final String PEER_INFO_CONFIG_FILE = "../PeerInfo.cfg";

    // Connection states
    public static final int SENT_HANDSHAKE_AWAITING_HANDSHAKE = 0;
    public static final int HAVE_NOT_SENT_HANDSHAKE_AWAITING_HANDSHAKE = 1;
    public static final int SENT_BITFIELD_AWAITING_BITFIELD = 2;
    public static final int HAVE_NOT_SENT_BITFIELD_AWAITING_BITFIELD = 3;
}
