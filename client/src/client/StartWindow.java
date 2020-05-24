package client;

import server.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartWindow extends JFrame implements ActionListener {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StartWindow();
            }
        });


        }
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("guest");
    private final JTextField fieldInputIp = new JTextField();
    JButton buttonCreate = new JButton("Create");
    class joinListenerAction extends Thread implements ActionListener {
        public synchronized void actionPerformed(ActionEvent e) {
            ClientWindow.main(new String[]{"127.0.0.1","8188",fieldNickname.getText()});
            buttonCreate.setEnabled(false);
            StartWindow.this.setVisible(false);
        }
    }
    class createListenerAction implements ActionListener {
        public synchronized void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Thread createServerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ChatServer.main(new String[]{""});
                        }
                    });
                    Thread createClientThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            ClientWindow.main(new String[]{"127.0.0.1","8188",fieldNickname.getText()});
                        }
                    });
                    createClientThread.start();
                    createServerThread.start();
                    buttonCreate.setEnabled(false);
                    StartWindow.this.setVisible(false);
                }
            });
        }
    }

    JButton buttonJoin = new JButton("Join");
    public StartWindow(){
        buttonJoin.addActionListener(new joinListenerAction());
        buttonCreate.addActionListener(new createListenerAction());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        log.setEditable(false);
        log.setLineWrap(true);
        log.setText("Hello");
        add(log, BorderLayout.NORTH);
        add(fieldNickname,BorderLayout.NORTH);
        add(buttonJoin,BorderLayout.EAST);
        add(buttonCreate,BorderLayout.WEST);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
