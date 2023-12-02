import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.BitSet;

public class ConnectionHandler implements Runnable {

    Socket socket;
    int connectedPeerID;
    int thisPeerID;
    Peer peer;
    int pieceSize;

    Client client = null;

    boolean handshakeReceived = false;

    private int currentConnectionState;
    

    public ConnectionHandler(Socket socket, int thisPeerID, Peer peer, int pieceSize) {
        this.socket = socket;
        this.thisPeerID = thisPeerID;
        this.peer = peer;
        this.pieceSize = pieceSize;
    }

    @Override
    public void run() {
        System.out.println("Socket handler is working...");

        // Expect to receive handshake
        try {
//            if(!handshakeReceived) {
//                receiveHandshake();
//                if(client == null) {
//                    createClient();
//                }
//                returnHandshake();
//            }
//            switch(currentConnectionState) {
//
//                // This case occurs when this peer initiates a connection and waits for a handshake in return
//                case Constants.SENT_HANDSHAKE_AWAITING_HANDSHAKE:
//                    receiveHandshake();
//                    // TODO: send bitfield here
//                    break;
//
//                // This case occurs when this peer did not initiate the connection and receives a handshake from a different peer
//                case Constants.HAVE_NOT_SENT_HANDSHAKE_AWAITING_HANDSHAKE:
//                    receiveHandshake();
//                    if(client == null) {
//                        createClient();
//                    }
//                    returnHandshake();
//                    break;
//
//                case Constants.SENT_BITFIELD_AWAITING_BITFIELD:
//
//                    break;
//
//                case Constants.HAVE_NOT_SENT_BITFIELD_AWAITING_BITFIELD:
//
//
//            }

            if(!handshakeReceived) {

                receiveHandshake();
                if(client == null) {
                    createClient();
                }

                // If we receive a handshake from a peer with a lower ID
                if(connectedPeerID < thisPeerID) {
                    // TODO: return a bitfield
                } else {
                    returnHandshake();
                }
                sendBitfield(socket, peer);
                boolean interest = checkReceivedBitfield(socket, peer);
                if(!interest){
                    for(int i = 0; i < peer.getNumPieces(); i++){
                        sendPiece(socket, i, "tree.jpg", pieceSize);
                    }
                }
                else{
                    for(int i = 0; i < peer.getNumPieces(); i++){
                        receivePiece(socket, 1, "tree1.jpg", pieceSize);
                    }
                }

            } else {

                // TODO: Read other messages

            }

        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ConnectionHandler exiting...");

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

    public void setConnectionState(int connectionState) {
        this.currentConnectionState = connectionState;
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

    private static void sendBitfield(Socket socket, Peer peer) {
        try {
            // Convert the BitSet to a byte array
            System.out.println("Sending bitfield to peer " + peer.getPeerID() + " ----- Bitmap:"+peer.getBitmap().get(1));
            byte[] bitfieldBytes = peer.getBitmap().toByteArray();

            // Prepare the message
            messages.Message bitfieldMessage = new messages.Message((byte) 5, bitfieldBytes);
            byte[] messageBytes = bitfieldMessage.createMessageBytes();

            // Send the message
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageBytes);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkReceivedBitfield(Socket socket, Peer peer) {
        try {
            System.out.println("Recieving bitfield to peer " + peer.getPeerID()+" ----- Bitmap:"+peer.getBitmap().get(1));
            DataInputStream in = new DataInputStream(socket.getInputStream());
            // Read the message length
            int messageLength = in.readInt();
            // Read the message type
            byte messageType = in.readByte();
            
            if (messageType == Constants.BITFIELD) {
                // Read the bitfield bytes
                byte[] receivedBitfield = new byte[messageLength - 1]; // 1 byte for message type
                in.readFully(receivedBitfield);

                BitSet receivedBitSet = BitSet.valueOf(receivedBitfield);

                boolean interested = false;
                for (int i = 0; i < receivedBitSet.length(); i++) {
                    if (receivedBitSet.get(i) && !peer.getBitmap().get(i)) {
                        interested = true;
                        break;
                    }
                }
                // Prepare and send the appropriate message
                OutputStream outputStream = socket.getOutputStream();
                if (interested) {
                    System.out.println("Sending interested message from peer " + peer.getPeerID());
                    byte messageBytes = (byte)2; // Replace this with your message creation logic
                    //  Write the message to the output stream
                    outputStream.write(messageBytes);
                    // Flush the output stream to ensure all data is sent
                    outputStream.flush();
                    // Close the output stream
                    return true;
                } 
                else {
                    System.out.println("Sending not interested message from peer " + peer.getPeerID());
                    byte messageBytes = (byte)3; // Replace this with your message creation logic
                    //  Write the message to the output stream
                    outputStream.write(messageBytes);
                    // Flush the output stream to ensure all data is sent
                    outputStream.flush();
                    // Close the output stream
                    return false;
                }
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int sendPiece(Socket socket, int index, String fName, int pieceSize){
        try{
            System.out.println("Sending piece to peer " + peer.getPeerID()+" ---- //////");
            
            FileInputStream fis = new FileInputStream(fName);
            BufferedInputStream bis = new BufferedInputStream(fis);

            byte[] fileBytes = new byte[pieceSize];
            System.out.println("Available in buff input stream " + bis.available()+" ----"+ pieceSize+"_______________"+index*pieceSize+" -------- - - -");
            if(pieceSize >= (bis.available()-(index*pieceSize))){
                System.out.println("Sent bytes: " + bis.read(fileBytes, 0, (bis.available()-(index*pieceSize))));
            }
            else{
                bis.read(fileBytes, 0, pieceSize);
            }

            // Prepare the message
            messages.Message pieceMessage = new messages.Message((byte) 7, fileBytes);
            byte[] messageBytes = pieceMessage.createMessageBytes();

            // Send the message
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageBytes);

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return 0;
    }
    
    public int receivePiece(Socket socket, int index, String fName, int pieceSize){
        try{
            System.out.println("Receiving piece to peer " + peer.getPeerID()+" ---- //////");
            
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // File newfile = new File(fName);
            FileOutputStream fos = new FileOutputStream(fName, true);
            
            byte[] fileBytes = new byte[pieceSize];
            in.read(fileBytes);
            System.out.println("Available in buff input stream " + in.available()+" ----"+ pieceSize+"_______________"+index*pieceSize+" -------- - - -");
            if(fileBytes.length >= in.available()){
                System.out.println("Received bytes: " + in.available());
                fos.write(fileBytes, 0, in.available());
            }
            else{
                System.out.println("Received bytes: " + fileBytes.length);
                fos.write(fileBytes, 0, pieceSize);
            }
            fos.close();



        //     System.out.println("Available in buff input stream " + bis.available()+" ----"+ pieceSize+"_______________"+index*pieceSize+" -------- - - -");
        //     if(pieceSize >= bis.available()){
        //         bis.read(fileBytes, 0, bis.available());
        //     }
        //     else{

        //         bis.read(fileBytes, 0, pieceSize);
        //     }

        //     // Prepare the message
        //     messages.Message pieceMessage = new messages.Message((byte) 7, fileBytes);
        //     byte[] messageBytes = pieceMessage.createMessageBytes();

        //     // Send the message
        //     OutputStream outputStream = socket.getOutputStream();
        //     outputStream.write(messageBytes);

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return 0;
    }
    // FileOutputStream(File file, boolean true)
}
