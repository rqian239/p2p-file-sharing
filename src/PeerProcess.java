package src;

import java.io.File;
import java.io.FileNotFoundException;
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
}
