package ch.zhaw.pm2.multichat.server;

import ch.zhaw.pm2.multichat.protocol.DataFrame;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Application running on the server. It initiates all connectionHandlers
 */
public class Server {

    // Server connection
    private NetworkHandler.NetworkServer<DataFrame> networkServer;

    // Connection registry
    private Map<String,ServerConnectionHandler> connections = new HashMap<>();

    ExecutorService connectionService = Executors.newCachedThreadPool();

    /**
     * Main method used to start the server
     * @param args Server port
     */
    public static void main(String[] args) {
        // Parse arguments for server port.
        try {
            int port;
            switch (args.length) {
                case 0 -> port = NetworkHandler.DEFAULT_PORT;
                case 1 -> port = Integer.parseInt(args[0]);
                default -> {
                    System.out.println("Illegal number of arguments:  [<ServerPort>]");
                    return;
                }
            }
            // Initialize server
            final Server server = new Server(port);

            // This adds a shutdown hook running a cleanup task if the JVM is terminated (kill -HUP, Ctrl-C,...)
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                        System.out.println("Shutdown initiated...");
                        server.terminate();
                    } catch (InterruptedException e) {
                        System.out.println("Warning: Shutdown interrupted. " + e);
                    } finally {
                        System.out.println("Shutdown complete.");
                    }
                }
            });

            // Start server
            server.start();
        } catch (IOException e) {
            System.err.println("Error while starting server." + e.getMessage());
        }
    }

    /**
     * Creates a new server, that once started will listen on the given network port.
     *
     * @param serverPort to be used for the server
     * @throws IOException if an error occurred opening the port, e.g. the port number is already used.
     */
    public Server(int serverPort) throws IOException {
        // Open server connection
        System.out.println("Create server connection");
        networkServer = NetworkHandler.createServer(serverPort);
        System.out.println("Listening on " + networkServer.getHostAddress() + ":" + networkServer.getHostPort());
    }
    /**
     * Waits for clients to be connected and adds / starts for each client a new
     * Connection Handler.
     */
    private void start() {
        System.out.println("Server started.");
        try {
            while (true) {
                 NetworkHandler.NetworkConnection<DataFrame> connection = networkServer.waitForConnection();
                 ServerConnectionHandler connectionHandler = new ServerConnectionHandler(connection, connections);
                 connectionService.submit(connectionHandler);
                 System.out.println(String.format("Connected new Client %s with IP:Port <%s:%d>",
                     connectionHandler.getUserName(),
                     connection.getRemoteHost(),
                     connection.getRemotePort()
                 ));
            }
        } catch(SocketException e) {
            System.out.println("Server connection terminated");
        }
        catch (IOException e) {
            System.err.println("Communication error " + e);
        }
        // close server
        System.out.println("Server Stopped.");
    }
    /**
     * Informs the clients about the closed port and closes the network Server
     */
    public void terminate() {
        try {
            System.out.println("Close server port.");
            networkServer.close();
        } catch (IOException e) {
            System.err.println("Failed to close server connection: " + e);
        }
    }

}
