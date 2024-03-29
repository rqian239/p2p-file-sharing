import java.util.BitSet;

public class Peer {
    private final int peerID;
    private final String hostname;
    private final int port;
    private boolean hasFile;
    private final int numPieces;  // also the number of bits in the bitmap

    private final BitSet bitmap;

    private boolean choked = false;

    public Peer(int peerID, String hostname, int port, boolean hasFile, int numPieces) {
        this.peerID = peerID;
        this.hostname = hostname;
        this.port = port;
        this.hasFile = hasFile;
        this.numPieces = numPieces;

        this.bitmap = new BitSet(numPieces);

        if(hasFile) {
            // Set all bits to 1
            bitmap.set(0, numPieces, true);
        } else {
            // Set all bits to 0
            bitmap.set(0, numPieces, false);
        }
    }

    // Bitmap functions
    public void setPiece(int index) {
        bitmap.set(index);
    }

    public boolean hasPiece(int index) {
        return bitmap.get(index);
    }

    // Getters
    public int getPeerID() {
        return peerID;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public int getNumPieces() {
        return numPieces;
    }

    public BitSet getBitmap() {
        return bitmap;
    }

    public boolean isChoked() {
        return choked;
    }

    // Setters
    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }

    public void setChoked(boolean choked) {
        this.choked = choked;
    }
}
