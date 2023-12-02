import messages.*;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Scanner;
import java.util.AbstractMap;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


public class peerProcess {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("PeerID not specified!");
        }
//        Hashtable<Integer, Peer> Peers = new Hashtable<Integer, Peer>();

        int peerID = Integer.parseInt(args[0]);
        RunPeer runProcess = new RunPeer(peerID);
        runProcess.run();

//        // Create a Peer object
//        Peer peer = Peers.get(peerID);
//        Map<Integer, AbstractMap.SimpleEntry<Peer, Socket>> peerMap = new HashMap<>();

        // Start the peer process
//        try {
//            startPeerProcess(peer);
//            checkInterested(peer, peerMap);
//        } catch (IOException e) {
//            System.err.println("Error starting the peer process: " + e.getMessage());
//            e.printStackTrace();
//        }
    }
    // private static Peer createPeer(int peerID) {
        
    //     // String hostname = "localhost";  // Update with actual hostname
    //     // int port = 6008;  // Update with actual port
    //     // boolean hasFile = false;  // Update based on configuration file
    //     // int numPieces = 306;  // Update based on configuration file

    //     // return new Peer(peerID, hostname, port, hasFile, numPieces);
    // }

    private static void sendHandshake(Socket socket, Peer peer) {
        messages.Handshake handshake = new messages.Handshake(peer.getPeerID());
        byte[] handshakeBytes = handshake.createHandshakeMessage();
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(handshakeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveHandshake(Socket socket, Peer peer) {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] receivedHandshake = new byte[32];
            inputStream.read(receivedHandshake);
    
            String receivedHeader = new String(receivedHandshake, 0, 18);
            byte[] zeroBits = new byte[10];
            System.arraycopy(receivedHandshake, 18, zeroBits, 0, 10);
            int receivedPeerID = ByteBuffer.wrap(receivedHandshake, 28, 4).getInt();
    

            if (receivedHeader.equals("P2PFILESHARINGPROJ") && receivedPeerID == peer.getPeerID()) {
                System.out.println("Received handshake from Peer " + receivedPeerID);
              
            } else {
                System.out.println("Invalid handshake received from Peer " + receivedPeerID);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // private static void sendBitfield(Socket socket, Peer peer) {
    //     try {
    //         // Convert the BitSet to a byte array
    //         byte[] bitfieldBytes = peer.getBitmap().toByteArray();

    //         // Prepare the message
    //         messages.Message bitfieldMessage = new messages.Message((byte) 5, bitfieldBytes);
    //         byte[] messageBytes = bitfieldMessage.createMessageBytes();

    //         // Send the message
    //         OutputStream outputStream = socket.getOutputStream();
    //         outputStream.write(messageBytes);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    // private static void checkInterested(Peer peer, Map<Integer, AbstractMap.SimpleEntry<Peer, Socket>> peerMap){
    //     for(int i = 0; i < peer.getNumPieces(); i++){
    //         for(Map.Entry<Integer, AbstractMap.SimpleEntry<Peer, Socket>> entry : peerMap.entrySet()){
    //             if(!peer.hasPiece(i) && entry.getValue().getKey().hasPiece(i)){
    //                 System.out.println("Peer " + peer.getPeerID() + " is interested in " + entry.getValue().getKey().getPeerID());
    //                 //send interested message
    //                 try {
    //                     messages.Message interested = new messages.Message((byte) 2);
    //                     OutputStream outputStream;
    //                     outputStream = entry.getValue().getValue().getOutputStream();
    //                     outputStream.write(interested.createMessageBytes());
    //                 } catch (IOException e) {
    //                     // TODO Auto-generated catch block
    //                     e.printStackTrace();
    //                 }
    //             }
    //             else{
    //                 System.out.println("Peer " + peer.getPeerID() + " is not interested in " + entry.getValue().getKey().getPeerID());
    //             }
    //         }

    //     }
    // }

//     private static void checkReceivedBitfield(Socket socket, BitSet peerBitSet) {
//     try {
//         DataInputStream in = new DataInputStream(socket.getInputStream());
//         // Read the message length
//         int messageLength = in.readInt();
//         // Read the message type
//         byte messageType = in.readByte();
        
//         if (messageType == Constants.BITFIELD) {
//             // Read the bitfield bytes
//             byte[] receivedBitfield = new byte[messageLength - 1]; // 1 byte for message type
//             in.readFully(receivedBitfield);

//             BitSet receivedBitSet = BitSet.valueOf(receivedBitfield);

//             boolean interested = false;
//             for (int i = 0; i < receivedBitSet.length(); i++) {
//                 if (receivedBitSet.get(i) && !peerBitSet.get(i)) {
//                     interested = true;
//                     break;
//                 }
//             }

//             // Prepare and send the appropriate message
//             OutputStream outputStream = socket.getOutputStream();
//             if (interested) {
//                 byte messageBytes = (byte)2; // Replace this with your message creation logic
//                 //  Write the message to the output stream
//                 outputStream.write(messageBytes);
//                 // Flush the output stream to ensure all data is sent
//                 outputStream.flush();
//                 // Close the output stream
//                 outputStream.close();
//             } 
//             else {
//                 byte messageBytes = (byte)3; // Replace this with your message creation logic
//                 //  Write the message to the output stream
//                 outputStream.write(messageBytes);
//                 // Flush the output stream to ensure all data is sent
//                 outputStream.flush();
//                 // Close the output stream
//                 outputStream.close();
//             }
//         }
//     } 
//     catch (IOException e) {
//         e.printStackTrace();
//     }
// }

}