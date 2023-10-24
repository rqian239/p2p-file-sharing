import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommonConfig {

    private String numPrefNeighbors;
    private int unchokingInterval;
    private int optUnchokingInterval;
    private String filename;
    private long filesize;
    private long piecesize;
    
    CommonConfig() {
        try {
            parseCommonConfig();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseCommonConfig() throws FileNotFoundException {

        File commonConfig = new File("Common.cfg");
        Scanner scnr = new Scanner(commonConfig);

        List<String> configValues = new ArrayList<String>();
        while(scnr.hasNextLine())
        {
            String line = scnr.nextLine();
            String [] commonvariables = line.split(" ");
            configValues.add(commonvariables[1]);
        }

        scnr.close();
        
        this.numPrefNeighbors = configValues.get(0);
        this.unchokingInterval = Integer.parseInt(configValues.get(1));
        this.optUnchokingInterval = Integer.parseInt(configValues.get(2));
        this.filename = configValues.get(3);
        this.filesize = Integer.parseInt(configValues.get(4));
        this.piecesize = Integer.parseInt(configValues.get(5));

    }

    // Getters

    public String getNumPrefNeighbors() {
        return numPrefNeighbors;
    }

    public int getUnchokingInterval() {
        return unchokingInterval;
    }

    public int getOptUnchokingInterval() {
        return optUnchokingInterval;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return filesize;
    }

    public long getPieceSize() {
        return piecesize;
    }

}
