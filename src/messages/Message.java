package messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Message implements Serializable {
    private int messageLength;
    private byte messageType;
    private byte[] payload;

    // Constructor with payload
    public Message(byte messageType, byte[] payload) {
        this.messageType = messageType;
        this.payload = payload;

        if(payload != null) {
            this.messageLength = payload.length + 1;
        } else {
            this.messageLength = 1;
        }
    }

    // Constructor without payload
    public Message(byte messageType) {
        this.messageType = messageType;
        this.payload = null;
        this.messageLength = 1;
    }

    public byte[] createMessageBytes() {

        // Create the byte array for the message
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + this.messageLength);
        buffer.putInt(messageLength);
        buffer.put(messageType);

        // Add in payload if nonempty
        if(payload != null) {
            buffer.put(payload);
        }

        return buffer.array();
    }


    // Getters

    public int getMessageLength() {
        return messageLength;
    }

    public byte getMessageType() {
        return messageType;
    }

    public byte[] getPayload() {
        return payload;
    }
}
