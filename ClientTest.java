import javax.swing.JFrame;
public class ClientTest {

	public static void main(String[] args) throws ClassNotFoundException {
		Client testClient = new Client("127.0.0.1");
		testClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testClient.beginProgram();
	}

}