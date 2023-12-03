import messages.Message;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class ConnectionHandler implements Runnable {

    Socket socket;
    int connectedPeerID;
    int thisPeerID;
    Peer thisPeer;
    int pieceSize;
    int index;

    Client client = null;

    boolean handshakeReceived = false;

    boolean sentBitfield = false;

    BitSet otherPeerBitfield = null;

    BitSet interestedPieces = null;
    

    public ConnectionHandler(Socket socket, int thisPeerID) {
        this.socket = socket;
        this.thisPeerID = thisPeerID;
        this.thisPeer = RunPeer.allPeers.get(thisPeerID);
        this.pieceSize = RunPeer.pieceSize;
        interestedPieces = new BitSet(thisPeer.getNumPieces());
    }

    @Override
    public void run() {
        System.out.println("Socket handler is working...");

        // Expect to receive handshake
        try {

            while(true) {
                if (!handshakeReceived) {
                    receiveHandshake();
                    if (client == null) {
                        createClient();
                    }

                    // If we receive a handshake from a peer with a lower ID
                    if (connectedPeerID < thisPeerID) {
                        sendBitfield(socket, thisPeer);
                        sentBitfield = true;
                    } else {
                        // HERE THIS PEER IS THE "SERVER"
                        RunPeer.allConnections.put(connectedPeerID, this);
                        returnHandshake();
                    }

                } else {

                    System.out.println("Expecting a message...");
                    DataInputStream readMessage = readMessage();
                    // Read the message length (first four bytes)
                    int messageLength = readMessage.readInt();
                    // Read the message type
                    byte messageType = readMessage.readByte();
                    // Read in the payload
                    // Read the bitfield bytes
                    byte[] receivedPayload = new byte[messageLength - 1]; // 1 byte for message type
                    readMessage.readFully(receivedPayload);


                    // TODO: Switch statement for message type
                    switch (messageType) {
                        case Constants.BITFIELD:

                            // Parse the bitfield
                            otherPeerBitfield = BitSet.valueOf(receivedPayload);

                            //System.out.println("Received a bitfield from peer [" + connectedPeerID + "] ----- Other Bitmap:" + otherPeerBitfield.get(1)); //TODO: what is getBitmap().get(1)?

                            // Return a bitfield if we haven't one yet
                            if (!sentBitfield) {
                                sendBitfield(socket, thisPeer);
                                sentBitfield = true;
                            }

                            // TODO: Send interested/uninterested message

                            boolean areWeInterested = checkIfWeAreInterested();

                            sendInterested(areWeInterested);  // send interested/uninterested message based on checkInterested();

                            if(areWeInterested) {
                                // TODO: send a request message

                                // TODO: calculate the index we are requesting
                                int requestThisPiece = RunPeer.getRandomPieceIndex();

                                if(requestThisPiece == -1) {
                                    // We do not have any pieces we're missing
                                    System.out.println("WE ALREADY HAVE THE FILE?");
                                } else {
                                    sendRequestMessage(requestThisPiece);
                                }
                            }


                            break;

                        case Constants.INTERESTED:
                            System.out.println(Logger.logReceiveInterested(thisPeerID, connectedPeerID));
                            break;

                        case Constants.NOT_INTERESTED:
                            System.out.println(Logger.logReceiveNotInterested(thisPeerID, connectedPeerID));
                            break;
                        case Constants.HAVE:
                            index = getIndexByte(receivedPayload);

                            otherPeerBitfield.set(index, true);

                            break;
                        case Constants.REQUEST:
                            // We send the piece back
                            //TODO: send the file to interested peer
                            index = getIndexByte(receivedPayload);

                            sendPiece(socket, index, "tree.jpg", pieceSize);

                            break;

                        case Constants.PIECE:
                            index = getIndexByte(receivedPayload);
                            // TODO: receive the piece here
                            processPiece(receivedPayload, "tree" + thisPeerID + ".jpg");

                            sendHaveMessage(index);

                            // Decide if we should continue
                            int requestThisPiece = RunPeer.getRandomPieceIndex();
                            if(requestThisPiece != -1){
                                sendRequestMessage(requestThisPiece);
                            }
                            break;
                    }
                    //TODO:else stop thread

                }
            }

        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ConnectionHandler exiting...");

    }

    private void sendRequestMessage(int requestedPieceIndex) throws IOException {
        // Create message

        byte messageType = Constants.REQUEST;

        byte[] payload = ByteBuffer.allocate(4).putInt(requestedPieceIndex).array();
        Message message = new Message(messageType, payload);
        byte[] messageBytes = message.createMessageBytes();
        System.out.println(Logger.logPieceRequestedFrom(thisPeerID, connectedPeerID, requestedPieceIndex));

        client.sendMessage(socket, messageBytes);
    }
    private void sendHaveMessage(int pieceIndex) throws IOException {
        // Create message

        byte messageType = Constants.HAVE;

        byte[] payload = ByteBuffer.allocate(4).putInt(pieceIndex).array();
        Message message = new Message(messageType, payload);
        byte[] messageBytes = message.createMessageBytes();

        client.sendMessage(socket, messageBytes);
    }
    private int getIndexByte(byte[] receivedPayload){
        byte[] intBuffer = new byte[4];
        System.arraycopy(receivedPayload, 0, intBuffer, 0, 4);
        ByteBuffer wrapped = ByteBuffer.wrap(intBuffer); // big-endian by default
        int index = wrapped.getInt();
        return index;
    }

    private DataInputStream readMessage() throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    private void readEntireFile() throws IOException {
        InputStream inputStream = socket.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream("tree_read.jpg");

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        System.out.println("File received");
        //fileOutputStream.close();
    }

    private void sendEntireFile() throws IOException {
        OutputStream out = socket.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(RunPeer.dataFilename);
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }

        System.out.println("Image sent");
        //fileInputStream.close();
        //out.close();
    }

    public void returnHandshake() {
        System.out.println(Logger.logConnection(thisPeerID, connectedPeerID, false));
        client.sendHandshake(socket, thisPeerID);
    }

    public void createClient() {
        client = new Client(thisPeerID);
        client.setConnectionHandler(this);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void receiveHandshake() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        // Expecting a 32-byte handshake message
        byte[] receivedHandshake = new byte[32];
        in.readFully(receivedHandshake);

        // Validate the handshake message
        if (isValidHandshake(receivedHandshake)) {
            handshakeReceived = true;
            System.out.println(Logger.logConnection(thisPeerID, connectedPeerID, true));
        } else {
            System.err.println("INVALID HANDSHAKE RECEIVED FROM PEER " + connectedPeerID);
        }

    }

    private boolean isValidHandshake(byte[] handshake) {
        String header = new String(handshake, 0, 18);

        // Check if the header is correct and peer ID matches expected value
        connectedPeerID = ByteBuffer.wrap(handshake, 28, 4).getInt();
        return header.equals("P2PFILESHARINGPROJ");
    }

    private void sendBitfield(Socket socket, Peer peer) {
        try {
            // Convert the BitSet to a byte array
            System.out.println("Sending bitfield to peer [" + connectedPeerID + "] ----- My Bitmap:" + peer.getBitmap().get(1));   //TODO: what is getBitmap().get(1)?
            byte[] bitfieldBytes = peer.getBitmap().toByteArray();

            // Prepare the message
            messages.Message bitfieldMessage = new messages.Message((byte) 5, bitfieldBytes);
            byte[] messageBytes = bitfieldMessage.createMessageBytes();

            // Send the message
            client.sendMessage(socket, messageBytes);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInterested(boolean isInterested) throws IOException {
        // Create message

        byte messageType = isInterested ? Constants.INTERESTED : Constants.NOT_INTERESTED;

        Message message = new Message(messageType);
        byte[] messageBytes = message.createMessageBytes();

        client.sendMessage(socket, messageBytes);
    }

    // Checks if we are interested in the other peer
    private boolean checkIfWeAreInterested() {
        boolean interested = false;
        interestedPieces.set(0, thisPeer.getNumPieces(), false);
        for (int i = 0; i < otherPeerBitfield.length(); i++) {
            if (otherPeerBitfield.get(i) && !thisPeer.getBitmap().get(i)) {
                interestedPieces.set(i,true);
                interested = true;
            }
        }

        return interested;
    }

    public int sendPiece(Socket socket, int index, String fName, int pieceSize){
        try{
            System.out.println("Sending piece " + index + " to Peer " + connectedPeerID+" from Peer "+thisPeerID);

            FileInputStream fis = new FileInputStream(fName);
            //BufferedInputStream bis = new BufferedInputStream(fis);
            fis.skip(index * pieceSize);
            byte[] fileBytes = new byte[pieceSize];
            //System.out.println("Available in buff input stream " + fis.available()+" ----"+ index+"_______________"+index*pieceSize+" -------- - - -");
            int bytesRead = fis.read(fileBytes, 0,pieceSize);

            //System.out.println("Read bytes: " + bytesRead);

            byte[] indexBytes = ByteBuffer.allocate(4).putInt(index).array();

            // Combine indexBytes and fileBytes into a single byte array
            byte[] payloadBytes = new byte[4 + bytesRead];
            System.arraycopy(indexBytes, 0, payloadBytes, 0, 4);
            System.arraycopy(fileBytes, 0, payloadBytes, 4, bytesRead);

            // Prepare the message
            Message pieceMessage = new Message(Constants.PIECE, payloadBytes);
            byte[] messageBytes = pieceMessage.createMessageBytes();

            // Send the message
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageBytes);
            outputStream.flush();

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return 0;
    }
    
//    public int processPiece(byte[] payload, String fName){
//        try{
//            index = getIndexByte(payload);
//
//            // File newfile = new File(fName);
//            FileOutputStream fos = new FileOutputStream(fName, true);
//
//            // byte[] indexBytes = new byte[4];
//            // in.read(indexBytes);
//            // int indexT = ByteBuffer.wrap(indexBytes).getInt();
//
//            //System.out.println("Piece Index Received: " + indexT);
//
//            byte[] fileBytes = new byte[pieceSize];
//            //System.out.println("In bytes " + in.available()+" ---- Message Length:"+messageLength +"  ----  Message Type: "+ messageType);
//            int bytesRead = payload.length - 4;
//
//            System.arraycopy(payload, 4, fileBytes, 0, bytesRead);
//
//
//            //System.out.println("Received bytes: " + bytesRead);
//            fos.write(fileBytes, 0, bytesRead);
//
//            // Update this bitmap
//            thisPeer.getBitmap().set(index);
//            System.out.println(Logger.logPieceDownloadedFrom(thisPeerID, connectedPeerID, index, thisPeer.getBitmap().cardinality()));
//            fos.flush();
//            fos.close();
//            if(thisPeer.getNumPieces() == (index+1)){
//                System.out.println(Logger.logFileDownloaded(thisPeerID));
//            }
//        }
//        catch(IOException e){
//            e.printStackTrace();
//        }
//        return 0;
//    }
    public int processPiece(byte[] payload, String fName) {
        try {
            int index = getIndexByte(payload);

            // Calculate the starting position in the file
            long startPosition = (long) index * pieceSize;

            // File newfile = new File(fName);
            RandomAccessFile raf = new RandomAccessFile(fName, "rw");

            // Seek to the specified position in the file
            raf.seek(startPosition);

            byte[] fileBytes = new byte[pieceSize];
            int bytesRead = payload.length - 4;

            // Copy the payload bytes to the fileBytes array
            System.arraycopy(payload, 4, fileBytes, 0, bytesRead);

            // Write the fileBytes array to the file
            raf.write(fileBytes, 0, bytesRead);

            // Update this bitmap
            thisPeer.getBitmap().set(index);
            System.out.println(Logger.logPieceDownloadedFrom(thisPeerID, connectedPeerID, index, thisPeer.getBitmap().cardinality()));

            // If all pieces are downloaded, log the completion
            if (thisPeer.getNumPieces() == (index + 1)) {
                System.out.println(Logger.logFileDownloaded(thisPeerID));
            }

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
