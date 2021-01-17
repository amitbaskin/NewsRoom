import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class Server extends JFrame implements Runnable{
    public static final String FRAME_TITLE = "Server";
    public static final int PORT_NUM = 7777;
    public static final int DEFAULT_MSG_SIZE = 100;
    public static final String SEND_TITLE = "Send";
    public static final String STOP_TITLE = "Stop\nEnrollments";
    public static final String CONTINUE_TITLE = "Continue\nEnrollments";
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String DEFAULT_TEXT = "";
    public static final int ROWS = 300;
    public static final int COLS = 400;
    private DatagramSocket socket;
    private final HashMap<InetAddress, Integer> clients;
    private boolean isAcceptNewClients;

    public void setAcceptNewClients(boolean acceptNewClients) {
        isAcceptNewClients = acceptNewClients;
    }

    public Server(){
        super(FRAME_TITLE);
        isAcceptNewClients = true;
        clients = new HashMap<>();
        final JTextArea newsArea = new JTextArea();
        newsArea.setEditable(true);
        add(new JScrollPane(newsArea), BorderLayout.CENTER);
        JButton sendBtn = new JButton(SEND_TITLE);
        add(sendBtn, BorderLayout.SOUTH);
        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Object, Object>(){
                    @Override
                    protected Object doInBackground(){
                        String news = newsArea.getText();
                        for (InetAddress address : clients.keySet()){
                            try {
                                socket.send(new DatagramPacket(news.getBytes(), news.length(), address,
                                        clients.get(address)));
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        } newsArea.setText(DEFAULT_TEXT);
                        return null;
                    }
                }.doInBackground();

            }
        });
        JPanel enrollmentPanel = new JPanel();
        JButton stopBtn = new JButton(STOP_TITLE);
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAcceptNewClients(false);
            }
        });
        JButton continueBtn = new JButton(CONTINUE_TITLE);
        continueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });
        enrollmentPanel.add(stopBtn, BorderLayout.WEST);
        enrollmentPanel.add(continueBtn, BorderLayout.EAST);
        add(enrollmentPanel, BorderLayout.NORTH);
        setSize(COLS, ROWS);
        setVisible(true);
        try{
            socket = new DatagramSocket(PORT_NUM);
        } catch (SocketException exception){
            exception.printStackTrace();
            System.exit(1);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setAcceptNewClients(false);
                int length = Server.DISCONNECT.length();
                for (InetAddress address : clients.keySet()) {
                    try {
                        socket.send(new DatagramPacket(Server.DISCONNECT.getBytes(), length, address,
                                clients.get(address)));
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } dispose();
            }
        });
    }

    public void run(){
        new SwingWorker<Object, Object>(){
            @Override
            protected Object doInBackground(){
                getClients();
                return null;
            }
        }.doInBackground();
    }

    public void getClients(){
        while(isAcceptNewClients){
            try{
                byte[] data = new byte[DEFAULT_MSG_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(data, data.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if (msg.equals(CONNECT)){
                    clients.put(receivePacket.getAddress(), receivePacket.getPort());
                } if (msg.equals(DISCONNECT)) clients.remove(receivePacket.getAddress());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}