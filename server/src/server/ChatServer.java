package server;

import network.TCPConnection;
import network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.util.ArrayList;

import static network.TCPConnection.SERVERPORT;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }

    private class Connection {
        TCPConnection tcpConnection;
        String username;

        public Connection(TCPConnection tcpConnection, String username) {
            this.tcpConnection = tcpConnection;
            this.username = username;
        }

        @Override
        public String toString() {
            String temp = "(" + (tcpConnection.toString()).substring(1) + ")";
            temp = username + " " + temp;
            return temp;
        }
    }

    private final ArrayList<Connection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(SERVERPORT)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());

                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(new Connection(tcpConnection, ""));
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        Connection currentConnection = null;
        for (Connection connection : connections) {
            if (tcpConnection == connection.tcpConnection) currentConnection = connection;
        }
        if (!value.contains(":")) {
            currentConnection.username = value;
            if (currentConnection.tcpConnection.toString().contains("127.0.0.1"))
                currentConnection.username += "(host)";
            return;
        }
        if (value.contains(": /")) {
            onReceiveCommand(currentConnection, value);
            return;
        }
        value = currentConnection.username + value;
        sendToAllConnections(value);
    }

    private synchronized void sendToCurrentConnection(Connection currentConnection, String value) {
        currentConnection.tcpConnection.sendString(value);
    }

    private synchronized void printMembersList(Connection currentConnection) {
        String temp = "";
        for (Connection connection : connections) temp += connection + "\n";
        sendToCurrentConnection(currentConnection, temp);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        for (Connection connection : connections) {
            if (connection.tcpConnection == tcpConnection) {
                connections.remove(connection);
                sendToAllConnections("Client disconnected: " + connection);
            }

        }
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    public void onReceiveCommand(Connection connection, String command) {
        System.out.println(connection.username + command);
        sendToCurrentConnection(connection, command.substring(1));
        if (command.contains("/members")) {
            printMembersList(connection);
        }

    }

    private void sendToAllConnections(String value) {
        System.out.println(value);
        for (Connection connection : connections) connection.tcpConnection.sendString(value);
    }
}
