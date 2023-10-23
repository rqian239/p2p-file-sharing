package src;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;



public class PeerProcess 
{
    public static void main(String arg[]) throws FileNotFoundException 
    {
      
        File peerConfig = new File("PeerInfo.cfg");

        Hashtable<Integer, Peer> Peers = new Hashtable<Integer, Peer>();

        Scanner scnr = new Scanner(peerConfig);

        while(scnr.hasNextLine())
        {
            String line = scnr.nextLine();
            String [] variables = line.split(" ");
            Peer peer = new Peer(Integer.parseInt(variables[0]), variables[1], Integer.parseInt(variables[2]), Integer.parseInt(variables[3]));
            // System.out.println("Peer stuff" + peer.peerId + " " + peer.hostname + " " + peer.port + " " + peer.bitfield);
            Peers.put(peer.peerId, peer);
        }
        scnr.close();
    }


    public static void sendFile(String name, Socket sockT) throws IOException{
        try{
            //Initalize files and streams
            File file = new File(name+".mp3");
            FileInputStream fstream = new FileInputStream(file);

            BufferedOutputStream bos = new BufferedOutputStream(sockT.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            byte[] buf = new byte[(int) file.length()];
            fstream.read(buf);

            oos.writeObject(buf);
            oos.flush();

            fstream.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void recieveFile(String name, Socket sockT) throws IOException{
        try{
            //Initalize files and streams
            BufferedInputStream bis = new BufferedInputStream(sockT.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(bis);

            FileOutputStream fos = new FileOutputStream(name+".mp3");

            byte[] buf = byte[] ois.readObject();

            fos.write(buf);
            fos.flush();
            fos.close();

            System.out.println(name + "file recieved");

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
