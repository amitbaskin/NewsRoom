import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new Server());
        executorService.execute(new Client());
    }
}
