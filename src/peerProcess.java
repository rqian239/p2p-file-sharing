public class peerProcess {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException("PeerID not specified!");
        }

        int peerID = Integer.parseInt(args[0]);
        RunPeer runProcess = new RunPeer(peerID);
        runProcess.run();

    }

}