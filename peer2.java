import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class peer2 {
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

                //Send message to server
                bw.write(outmsg);
                bw.newLine();
                bw.flush();

                //Check for key words to know whether to recieve file or not(Joke 1, Joke 2, Joke 3, bye, or error)
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

