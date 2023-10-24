import java.io.Serializable;


// This class stores a Message (packet) defined in the project spec
public class Message implements Serializable{
    
    private int messageLength;
    private byte messageType;

    // Needed if there is a payload (data)
    private String bitfield = null;
    private int indexField;
    private byte[] payload;

    // Constructor given messageType and messageLength
    Message(byte messageType, int messageLength) {
        this.messageType = messageType;
        this.messageLength = messageLength;
    }

    Message(byte messageType) {
        this.messageType = messageType;
        messageLength = 0;
    }

    Message() {
        throw new RuntimeException("You must provide a messageType to create a Message object.");
    }

    // Getters

    public int getMessageLength() {
        return messageLength;
    }

    public byte getMessageType() {
        return messageType;
    }

    public String getBitfield() {
        return bitfield;
    }

    public int getIndexField() {
        return indexField;
    }

    
    // Setters

    public void setBitfield(String bitfield) {
        this.bitfield = bitfield;
    }

    public void setIndexField(int indexField) {
        this.indexField = indexField;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
