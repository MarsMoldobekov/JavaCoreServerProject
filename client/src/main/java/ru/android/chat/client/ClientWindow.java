package ru.android.chat.client;

import ru.android.network.TCPConnection;
import ru.android.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Mars");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection tcpConnection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);
    }

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickname, BorderLayout.NORTH);

        setVisible(true);

        try {
            tcpConnection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException exception) {
            printMessage("Connection exception: " + exception);
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String msg = fieldInput.getText();

        if (msg.isEmpty()) {
            return;
        }

        fieldInput.setText(null);
        tcpConnection.sendString(fieldNickname.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection is ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection is closed.");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMessage("Connection exception: " + exception);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\r\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
