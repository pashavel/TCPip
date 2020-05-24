package client;

import network.TCPConnection;
import network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static String IP_ADDR = "";
    private static int PORT = 8188;
    private static String username = "";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField();
    private final JTextField fieldInput = new JTextField();
    private final JScrollPane scrollPane = new JScrollPane(log);
    private TCPConnection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                IP_ADDR = args[0];
                if (!args[1].equals("")) PORT = Integer.parseInt(args[1]);
                username = args[2];
                new ClientWindow();
            }
        });
    }

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        log.setEditable(false);
        log.setLineWrap(true);
        add(scrollPane, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);
        fieldNickname.setText(username);
        fieldNickname.setEditable(false);
        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if (message.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + message);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }


    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection lost");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getText().length());
                log.getCaret().setVisible(false);
            }
        });
    }
}
