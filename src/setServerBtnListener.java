import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class setServerBtnListener implements ActionListener {
    private final Client client;
    public setServerBtnListener(Client cLient){
        this.client = cLient;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = JOptionPane.showInternalInputDialog(client, "Please input a server name", "Server " +
                "Input", JOptionPane.QUESTION_MESSAGE);
        try {
            client.setServerAddress(InetAddress.getByName(input));
        } catch (UnknownHostException unknownHostException) {
            JOptionPane.showMessageDialog(client, "Unknown host!\nHost remains unchanged", "ERROR",
                     JOptionPane.ERROR_MESSAGE );
        }
    }
}