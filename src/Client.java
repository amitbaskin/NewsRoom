import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends JFrame implements Runnable {
    private static final String FRAME_TITLE = "Client";
    private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String NEWS_PADDING = "\n\n\n";
    private static final int ROWS = 300;
    private static final int COLS = 400;
    private final DatagramSocket socket;
    private final JTextArea displayArea;
    private final SimpleDateFormat formatter;
    private boolean isGetNews;
    private InetAddress serverAddress;

    public void setServerAddress(InetAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Client() throws SocketException, UnknownHostException {
        super(FRAME_TITLE);
        serverAddress = InetAddress.getLocalHost();
        isGetNews = true;
        socket = new DatagramSocket();
        formatter = new SimpleDateFormat(TIME_PATTERN);
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setIsGetNews(false);
                int length = Server.DISCONNECT.length();
                try {
                    getSocket().send(new DatagramPacket(Server.CONNECT.getBytes(), length,
                            getServerAddress(), Server.PORT_NUM));
                } catch (IOException exception) {
                    exception.printStackTrace();
                } dispose();
            }
        });
    }

    @Override
    public void run() {
        JButton setServerBtn = new JButton("Set Server");
        setServerBtn.addActionListener(new setServerBtnListener(this));
        add(setServerBtn, BorderLayout.SOUTH);
        setVisible(true);
        setSize(COLS, ROWS);
        try {
            new SwingWorker<Object, Object>(){
                @Override
                protected Object doInBackground() throws Exception {
                    getNews();
                    return null;
                }
            }.doInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setIsGetNews(boolean getNews) {
        isGetNews = getNews;
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public void getNews() throws IOException {
        int length = Server.CONNECT.length();
        socket.send(new DatagramPacket(Server.CONNECT.getBytes(), length, getServerAddress(),
                 Server.PORT_NUM));
        byte[] data = new byte[Server.DEFAULT_MSG_SIZE];
        final DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        while(isGetNews) {
            socket.receive(receivePacket);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    displayArea.append(formatter.format(new Date(System.currentTimeMillis())));
                    displayArea.append(new String(receivePacket.getData(), 0, receivePacket.getLength()));
                    displayArea.append(NEWS_PADDING);
                }
            });
        }
    }
}