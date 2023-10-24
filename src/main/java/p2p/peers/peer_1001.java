package main.java.p2p.peers;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import main.java.p2p.Constants;
import main.java.p2p.HandshakeMessage;
import main.java.p2p.Message;


public class peer_1001 {
    public static void main(String[] args) throws IOException{
        Socket socket = null;
        InputStreamReader rdr = null;
        OutputStreamWriter wr = null;
        BufferedReader br = null;
        BufferedWriter bw = null;

        ServerSocket serverSock = new ServerSocket(Integer.parseInt(args[0]));

        while(true){
            try {
                //Initalize socket, streams, and scanner
                socket = serverSock.accept();
                rdr = new InputStreamReader(socket.getInputStream());
                wr = new OutputStreamWriter(socket.getOutputStream());

                br = new BufferedReader(rdr);
                bw = new BufferedWriter(wr); 

                 
		        bw.write("Hello From Client 1!");
                bw.newLine();
                bw.flush();

                System.out.println("Receieved: " + br.readLine());
                
                HandshakeMessage pid = new HandshakeMessage(1001);
                sendFile(socket, pid);
                Object recieved = recieveFile(socket);

                HandshakeMessage handshake = (HandshakeMessage) recieved;
                System.out.println("Handshake received: PeerID = "+ handshake.getPeerID());


                Message message = new Message(Constants.getHAVE());
                sendFile(socket, message);
                recieved = recieveFile(socket);

                Message messageR = (Message) recieved;
                System.out.println("RECEIVED MESSAGE - Message Type = "+ messageR.getMessageType());

                //close all streams and sockets
                socket.close();
                rdr.close();
                wr.close();
                br.close();
                bw.close();

                return;

            } catch (IOException e) {
                e.printStackTrace();
            } finally{
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

