package me.matoosh.undernet.p2p.router.server;

import java.net.Socket;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;

/**
 * Represents a single connection with the server.
 * Created by Mateusz Rębacz on 18.02.2017.
 */

public class Connection {
    /**
     * Server making this connection.
     */
    public Server server;
    /**
     * The Thread used for this connection.
     */
    public Thread thread;
    /**
     * Node that the server is connected to.
     */
    public Node node;

    //Creates a new connection on a specific thread.
    public Connection(Server server, Thread thread) throws Exception {
        //Setting the variables.
        this.server = server;
        this.thread = thread;

        //Starting the connection session.
        session();
    }

    /**
     * A single connection session of the server.
     */
    private void session() throws Exception {
        //Listen and accept the connection.
        UnderNet.logger.info("Listening for connections on: " + server.port);
        Socket clientSocket = server.serverSocket.accept();

        //TODO: Connection establishment logic.
        //The node sends its current id.
        //If the id is empty, create an id based on our node id and send it back.
        //If the id is not empty, proceed.
        //Send hand-shake message.
        //Receive the node info and cache it.

        //The connection has been established, calling the event on the server.
        server.onConnectionEstablished(this);

        //Session logic.
        while(!thread.isInterrupted()) {
            UnderNet.logger.info("Connection logic running.");
            //TODO: Logic
        }
    }

    /**
     * Drops the connection.
     */
    public void drop() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
