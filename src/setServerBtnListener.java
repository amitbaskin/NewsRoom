import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class setServerBtnListener implements ActionListener {
    private final static String HOST_NAME_REQUEST_MSG = "Please enter a host name:";
    private final static String DEFAULT_HOST = "127.0.0.1";

    private final Client client;
    public setServerBtnListener(Client cLient){
        this.client = cLient;
    }

    private InetAddress getInputHost() throws UnknownHostException {
        String hostName = JOptionPane.showInputDialog(client, HOST_NAME_REQUEST_MSG, DEFAULT_HOST);
        return InetAddress.getByName(hostName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            new SwingWorker<Object, Object>(){
                @Override
                protected Object doInBackground() throws UnknownHostException {
                    client.setServerAddress(getInputHost());
                    return null;
                }
            }.doInBackground();
        } catch (UnknownHostException unknownHostException) {
            JOptionPane.showMessageDialog(client, "Unknown host!\nHost remains unchanged", "ERROR",
                    JOptionPane.ERROR_MESSAGE );
        }
    }
}