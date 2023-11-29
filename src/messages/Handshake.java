package messages;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Handshake {
    private static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    private static final int ZERO_BITS_LENGTH = 10;
    private final int peerID;

    public Handshake(int peerID) {
        this.peerID = peerID;
    }

    public byte[] createHandshakeMessage() {

        // Calculate length of handshake message
        int messageLength = HANDSHAKE_HEADER.length() + ZERO_BITS_LENGTH + Integer.BYTES;

        // Check that the length of handshake message is 32
        if(messageLength == 32) {

            // Create a ByteBuffer to combine all parts of the Handshake message
            ByteBuffer buffer = ByteBuffer.allocate(messageLength);

            // Add Handshake header
            buffer.put(HANDSHAKE_HEADER.getBytes(StandardCharsets.UTF_8));

            // Offset buffer by 10 to add 0 bits
            buffer.position(buffer.position() + ZERO_BITS_LENGTH);

            // Write peer ID
            buffer.putInt(peerID);

            return buffer.array();

        } else {
            throw new RuntimeException("Handshake message is not 32 bytes!");
        }

    }

    public int getPeerID() {
        return peerID;
    }
}
