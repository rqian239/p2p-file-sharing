package src;

import java.io.Serializable;

public class Message implements Serializable{
    
    private int messageLength;
    private byte messageType;

    private String bitfield = null;
    private int indexField;
    private byte[] payload;

    Message(byte messageType, int messageLength) {
        this.messageType = messageType;
        this.messageLength = messageLength;
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
