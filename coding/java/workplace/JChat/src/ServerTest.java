import javax.swing.JFrame;

public class ServerTest {
	public static void main(String[] args){
		Server CServer = new Server();
		CServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		CServer.startRunning();
	}
}
