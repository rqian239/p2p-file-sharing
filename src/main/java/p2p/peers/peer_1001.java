package main.java.p2p.peers;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import main.java.p2p.HandshakeMessage;


public class peer1001 {
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
                
                sendFile(1001, socket);
                recieveFile(socket);


                System.out.println("Receieved: " + br.readLine());
		
                Scanner sc = new Scanner(System.in);

                while(true){
                    String inmsg = br.readLine();
                    //Print message from peer in Terminal
                    System.out.println("Receieved: " + inmsg);
                    
                    if(inmsg.equals("exit")){
                        bw.write("Disconnected");
                        bw.newLine();
                        bw.flush();
                        break;
                    }
                    else{
                        String outmsg = sc.nextLine();
                        bw.write(outmsg);
                        bw.newLine();
                        bw.flush();
                    }
                }
                //close all streams and sockets
                socket.close();
                rdr.close();
                wr.close();
                br.close();
                bw.close();

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
}

public static void sendFile(int pidT, Socket sockT) throws IOException{
        try{
            //Initalize files and streams
            HandshakeMessage pid = new HandshakeMessage(pidT);

            BufferedOutputStream bos = new BufferedOutputStream(sockT.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(pid);
            oos.flush();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void recieveFile(Socket sockT) throws IOException{
        try{
            //Initalize files and streams
            try {
            BufferedInputStream bis = new BufferedInputStream(sockT.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(bis);

            HandshakeMessage message = (HandshakeMessage) ois.readObject();

            System.out.println("Handshake received: PeerID = "+ message.getPeerID());

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

