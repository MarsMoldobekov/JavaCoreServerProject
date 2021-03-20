package ru.geekbrains.chat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.android.network.TCPConnection;
import ru.android.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    private static final Logger LOGGER = LogManager.getLogger(ChatServer.class);
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
        new ChatServer();
    }

    private ChatServer() {
        LOGGER.info("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException exception) {
                    System.out.println("TCPConnection exception: " + exception);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection.toString());
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection.toString());
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("TCPConnection exception: " + exception);
    }

    private void sendToAllConnections(String value) {
        LOGGER.info(value);

        for (TCPConnection connection : connections) {
            connection.sendString(value);
        }
    }
}
