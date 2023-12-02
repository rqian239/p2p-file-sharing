import com.sun.org.apache.bcel.internal.Const;
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

    Client client = null;

    boolean handshakeReceived = false;

    boolean sentBitfield = false;

    BitSet otherPeerBitfield = null;
    

    public ConnectionHandler(Socket socket, int thisPeerID) {
        this.socket = socket;
        this.thisPeerID = thisPeerID;
        this.thisPeer = RunPeer.allPeers.get(thisPeerID);
        this.pieceSize = RunPeer.pieceSize;
    }

    @Override
    public void run() {
        System.out.println("Socket handler is working...");

        // Expect to receive handshake
        try {

            // TODO: Add a infinite while loop?
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
                        returnHandshake();
                    }
//                sendBitfield(socket, thisPeer);

//                boolean interest = checkReceivedBitfield(socket, thisPeer);
//                if(!interest){
////                    for(int i = 0; i < thisPeer.getNumPieces(); i++){
////                        sendPiece(socket, i, "tree.jpg", pieceSize);
////                    }
//                    // TODO: REMOVE LATER, for now send the entire file
//                    System.out.println("Beginning to send entire image file...");
//                    sendEntireFile();
//                }
//                else{
////                    for(int i = 0; i < thisPeer.getNumPieces(); i++){
////                        receivePiece(socket, 1, "tree1.jpg", pieceSize);
////                    }
//                    readEntireFile();
//                }

                } else {

                    // TODO: Read other messages
                    System.out.println("Expecting a message...");
                    DataInputStream readMessage = readMessage();
                    // Read the message length (first four bytes)
                    int messageLength = readMessage.readInt();
                    // Read the message type
                    byte messageType = readMessage.readByte();

                    // TODO: Switch statement for message type
                    switch (messageType) {
                        case Constants.BITFIELD:

                            // Parse the bitfield
                            // Read the bitfield bytes
                            byte[] receivedBitfield = new byte[messageLength - 1]; // 1 byte for message type
                            readMessage.readFully(receivedBitfield);

                            otherPeerBitfield = BitSet.valueOf(receivedBitfield);

                            System.out.println("Received a bitfield from peer [" + connectedPeerID + "] ----- Other Bitmap:" + otherPeerBitfield.get(1)); //TODO: what is getBitmap().get(1)?

                            // Return a bitfield if we haven't one yet
                            if (!sentBitfield) {
                                sendBitfield(socket, thisPeer);
                                sentBitfield = true;
                            }

                            // TODO: Send interested/uninterested message
                            sendInterested(checkInterested());  // send interested/uninterested message based on checkInterested();
                            break;

                        case Constants.INTERESTED:
                            System.out.println(Logger.logReceiveInterested(thisPeerID, connectedPeerID));
                            //TODO: receive file - should go to piece (interested returns index interested in)
                            for(int i = 0; i < thisPeer.getNumPieces(); i++){
                                receivePiece(socket, i, "tree1.jpg", pieceSize);
                            }
                            break;

                        case Constants.NOT_INTERESTED:
                            System.out.println(Logger.logReceiveNotInterested(thisPeerID, connectedPeerID));
                            //TODO: send file - should go to request (only sends piece requested)
                            for(int i = 0; i < thisPeer.getNumPieces(); i++){
                                sendPiece(socket, i, "tree.jpg", pieceSize);
                            }
                            break;
                        case Constants.HAVE:
                            //
                            break;
                        case Constants.REQUEST:
                            //
                            break;

                        case Constants.PIECE:
                            //
                            break;
                    }

                }
            }

        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ConnectionHandler exiting...");

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
        client.sendHandshake(socket, thisPeerID);
    }

    public void createClient() {
        client = new Client(thisPeerID);
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
            System.out.println("[" + RunPeer.getCurrentTime() + "]: Peer [" + thisPeerID + "] received a handshake from Peer [" + connectedPeerID + "]. ");
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

    private boolean checkReceivedBitfield(Socket socket, Peer peer) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // Read the message length (first four bytes)
            int messageLength = in.readInt();
            // Read the message type
            byte messageType = in.readByte();
            
            if (messageType == Constants.BITFIELD) {
                System.out.println("Received a bitfield from peer [" + connectedPeerID + "] ----- My Bitmap:" + peer.getBitmap().get(1)); //TODO: what is getBitmap().get(1)?
                // Read the bitfield bytes
                byte[] receivedBitfield = new byte[messageLength - 1]; // 1 byte for message type
                in.readFully(receivedBitfield);

                otherPeerBitfield = BitSet.valueOf(receivedBitfield);


//                // Prepare and send the appropriate message
//                OutputStream outputStream = socket.getOutputStream();
//                if (interested) {
//                    System.out.println("Sending interested message from peer " + peer.getPeerID());
////                    byte messageBytes = (byte)2; // TODO: Replace this with your message creation logic
////                    //  Write the message to the output stream
////                    outputStream.write(messageBytes);
////                    // Flush the output stream to ensure all data is sent
////                    outputStream.flush();
////                    // Close the output stream
//                    return true;
//                }
//                else {
//                    System.out.println("Sending not interested message from peer [" + thisPeerID + "] to [" + connectedPeerID + "].");
//                    // TODO: Send a not interested message
//                    return false;
//                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkInterested() {
        for (int i = 0; i < otherPeerBitfield.length(); i++) {
            if (otherPeerBitfield.get(i) && !thisPeer.getBitmap().get(i)) {
                return true;
            }
        }

        return false;
    }

    public int sendPiece(Socket socket, int index, String fName, int pieceSize){
        try{
            //System.out.println("Sending piece to peer " + thisPeer.getPeerID()+" ---- //////");

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
            messages.Message pieceMessage = new messages.Message((byte) 7, payloadBytes);
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
    
    public int receivePiece(Socket socket, int index, String fName, int pieceSize){
        try{
            //System.out.println("Receiving piece to peer " + thisPeer.getPeerID()+" ---- //////");
            
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int messageLength = in.readInt();
            // Read the message type
            byte messageType = in.readByte();
            int indexT = in.readInt();

            // File newfile = new File(fName);
            FileOutputStream fos = new FileOutputStream(fName, true);
            
            // byte[] indexBytes = new byte[4];
            // in.read(indexBytes);
            // int indexT = ByteBuffer.wrap(indexBytes).getInt();

            //System.out.println("Piece Index Received: " + indexT);

            byte[] fileBytes = new byte[pieceSize];
            //System.out.println("In bytes " + in.available()+" ---- Message Length:"+messageLength +"  ----  Message Type: "+ messageType);
            int bytesRead = in.read(fileBytes);
            //System.out.println("Received bytes: " + bytesRead);
            fos.write(fileBytes, 0, bytesRead);

            RunPeer.allPeers.get(connectedPeerID).getBitmap().set(index);
            System.out.println(Logger.logPieceDownloadedFrom(connectedPeerID, thisPeerID, index, RunPeer.allPeers.get(connectedPeerID).getBitmap().cardinality()));
            fos.flush();
            fos.close();
            if(RunPeer.allPeers.get(thisPeerID).getNumPieces() == (index+1)){
                System.out.println(Logger.logFileDownloaded(thisPeerID));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return 0;
    }
}
