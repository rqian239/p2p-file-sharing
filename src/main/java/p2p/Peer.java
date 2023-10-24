package main.java.p2p;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.*;

// Class to hold information about a Peer

public class Peer 
{
    int peerId;
    String hostname;
    int port;
    int bitfield;
    String numPrefNeighbors;
    int unchokingInterval;
    int optUnchokingInterval;
    String filename;
    long filesize;
    long piecesize;

    public Peer(){

    }
    public Peer(int peerId, String hostname, int port, int bitfield)
    {
        this.peerId = peerId;
        this.hostname = hostname;
        this.port = port;
        this.bitfield = bitfield;
    } 

    public void setCommonConfig() throws FileNotFoundException
    {
        File commonConfig = new File("src/Common.cfg");
        Scanner scnr = new Scanner(commonConfig);

        List<String> tempVariables = new ArrayList<String>();
        while(scnr.hasNextLine())
        {
            String line = scnr.nextLine();
            String [] commonvariables = line.split(" ");
            tempVariables.add(commonvariables[1]);
        }

        scnr.close();
        
        this.numPrefNeighbors = tempVariables.get(0);
        this.unchokingInterval = Integer.parseInt(tempVariables.get(1));
        this.optUnchokingInterval = Integer.parseInt(tempVariables.get(2));
        this.filename = tempVariables.get(3);
        this.filesize = Integer.parseInt(tempVariables.get(4));
        this.piecesize = Integer.parseInt(tempVariables.get(5));
    }

    
}
