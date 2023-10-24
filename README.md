# p2p-file-sharing

Nivedha Natarajan, Richard Qian, Sonu Venu, Revati Vijayn

This project is a peer-to-peer file sharing program similar to BitTorrent. This project is a current work in progress and IS NOT in a final state.

## Run Project

To run, navigate to the project root and enter the following into a terminal or command prompt

```
cd src
chmod +x run_peers.sh
./run_peers.sh
java -cp . main.java.p2p.peers.peer_1001 5230

```

Open a new terminal instance. Run the following

```
cd src
java -cp . main.java.p2p.peers.peer_1002 localhost 5230

```

The peers should have exchanged messages and printed logs into the terminal.


If you run into any errors with the .sh script above, you can run the commands directly. Run this inplace of the first code block

```
cd src
cd main/java/p2p
javac *.java
cd ../../..
javac -cp . main/java/p2p/peers/peer_1001.java
javac -cp . main/java/p2p/peers/peer_1002.java
java -cp . main.java.p2p.peers.peer_1001 5230

```
And then proceed with opening a new terminal instance, running the subsequent commands...