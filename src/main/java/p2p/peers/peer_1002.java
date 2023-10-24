package main.java.p2p.peers;

import java.io.*;
import java.net.Socket;

import main.java.p2p.Constants;
import main.java.p2p.HandshakeMessage;
import main.java.p2p.Message;

public class peer_1002 {
    public static void main(String[] args) {
        Socket socket = null;
        InputStreamReader rdr = null;
        OutputStreamWriter wr = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            //Initalize socket, streams, and scanner
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            rdr = new InputStreamReader(socket.getInputStream());
            wr = new OutputStreamWriter(socket.getOutputStream());

            br = new BufferedReader(rdr);
            bw = new BufferedWriter(wr);

	        System.out.println("Receieved: " + br.readLine());

            bw.write("Hello From Client 2!");
            bw.newLine();
            bw.flush();

            Object recieved = recieveFile(socket);

            HandshakeMessage handshake = (HandshakeMessage) recieved;
            System.out.println("Handshake received: PeerID = "+ handshake.getPeerID());

            HandshakeMessage pid = new HandshakeMessage(1002);
            sendFile(socket, pid);

            recieved = recieveFile(socket);
            Message messageR = (Message) recieved;
            System.out.println("RECEIVED MESSAGE - Message Type = "+ messageR.getMessageType());

            Message message = new Message(Constants.getINTERESTED());
            sendFile(socket, message);
	    
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            //close all streams and sockets if not already closed
            try {
                if(br != null) br.close();
                if(bw != null) bw.close();
                if(rdr != null) rdr.close();
                if(wr != null) wr.close();
                if(socket != null) socket.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void sendFile(Socket sockT, Object obj) throws IOException{
        try{
            //Initalize files and streams
            

            BufferedOutputStream bos = new BufferedOutputStream(sockT.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(obj);
            oos.flush();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Object recieveFile(Socket sockT) throws IOException{
        System.out.println("peer1001: recieveFile called");
        try{
            //Initalize files and streams
            try {
            BufferedInputStream bis = new BufferedInputStream(sockT.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return ois.readObject();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return sockT;
    }
}

