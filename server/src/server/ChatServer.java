package server;

import network.TCPConnection;
import network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static network.TCPConnection.SERVERPORT;

public class ChatServer implements TCPConnectionListener {
    public static void main(String[] args) {
        new ChatServer();
    }
    private final ArrayList<TCPConnection> connections = new ArrayList<TCPConnection>();
    private ChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(SERVERPORT)) {
            while(true)
            {
                try{
                    Socket socket = new Socket();
                    socket = serverSocket.accept();

                    new TCPConnection(this,socket,);
                }catch (IOException e)
                {
                    System.out.println("TCPConnection exception: "+e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {

        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        if(value.contains("/members")) printMembersList(tcpConnection);
        else
        sendToAllConnections(value);
    }
    private synchronized void printMembersList(TCPConnection tcpConnection)
    {
        String temp = "";
        for (int i = 0; i <connections.size() ; i++) {
            temp+=connections.get(i);
            temp+="\n";
        }
        tcpConnection.sendString(temp);
    }
    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
    connections.remove(tcpConnection);

        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }
    private void sendToAllConnections(String value)
    {
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) connections.get(i).sendString(value);
    }
}
