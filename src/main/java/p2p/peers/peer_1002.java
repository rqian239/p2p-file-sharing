package main.java.p2p.peers;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import peers.HandshakeMessage;

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

            Scanner sc = new Scanner(System.in);

	        System.out.println("Receieved: " + br.readLine());

            bw.write("Hello From Client 2!");
            bw.newLine();
            bw.flush();
	    
            while (true) {
                String outmsg = sc.nextLine();

                //send message to peer
                bw.write(outmsg);
                bw.newLine();
                bw.flush();

                if(outmsg.equals("exit")){
                    System.out.println("Receieved: " + br.readLine());
                    System.out.println("Exitting");
                    break;
                }
                else{
                    System.out.println("Receieved: " + br.readLine());
                }
            }
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

    public static void sendFile(int pidT, Socket sockT) throws IOException{
        try{
            //Initalize files and streams
            HandshakeMessage pid = HandshakeMessage(pidT);

            BufferedOutputStream bos = new BufferedOutputStream(sockT.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            byte[] buf = new byte[(int) file.length()];
            fstream.read(buf);

            oos.writeObject(buf);
            oos.flush();

            fstream.close();

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

            System.out.print(ois.readObject());

            System.out.println("Handshake received");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

