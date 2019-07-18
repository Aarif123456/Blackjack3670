import javax.swing.JFrame;
public class ServerTest {

	public static void main(String[] args) throws ClassNotFoundException {
		Server testServer = new Server();
		testServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testServer.beginProgram();
	}

}