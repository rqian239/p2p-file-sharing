package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.*;
import java.util.Iterator;



public class PeerProcess 
{
    public static void main(String arg[]) throws IOException 
    {
      
        File peerConfig = new File("src/PeerInfo.cfg");

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

        int PEERID =  Integer.parseInt(arg[0]);

        Set<Entry<Integer, Peer>> entrySet = Peers.entrySet();

        Iterator<Entry<Integer, Peer>> itr= entrySet.iterator();

        while (itr.hasNext()) {
            Entry<Integer, Peer> entry = itr.next();
            if(entry.getKey() < PEERID){
                //connect two; make a client
                //java test-peers/peer1 
                //jaVA 
                
            }

        }
       Peer newpeer = Peers.get(1001);
        Runtime.getRuntime().exec("javac test-peers/peer1" + newpeer.port);




    


   
 
    

    }
}
