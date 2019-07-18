import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

	private JTextField playerInput;
	private JTextArea gameScreen;
	private ObjectOutputStream output;
	private static ObjectInputStream input;
	private String serverIP;
	private Socket connection;

	/*
	 * Summary: Client constructor
	 */
	public Client(String host) {
		super("Blackjack player");
		serverIP = host;
		playerInput = new JTextField();
		playerInput.setEditable(false);

		// Send response to server
		playerInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				serverMessage(e.getActionCommand());
				playerInput.setText("");
			}
		});
		add(playerInput, BorderLayout.NORTH);
		gameScreen = new JTextArea();
		add(new JScrollPane(gameScreen), BorderLayout.CENTER);
		setSize(800, 800);
		setVisible(true);
	}

	/*
	 * Summary: Find hosts
	 */
	public void beginProgram() throws ClassNotFoundException {
		try {
			findDealer();
			createStream();
			duringGame();
		} catch (EOFException eofException) {
			clientMessage("\n...The player has terminated the connection...\n");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			closeConn();
		}
	}

	/*
	 * Summary: Find a dealer to connect, then display the dealer's IP
	 */
	private void findDealer() throws IOException {
		clientMessage("\n...Looking for game hosts...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		clientMessage("\nYou have connected to a dealer. IP: " + connection.getInetAddress().getHostName());
	}

	/*
	 * Summary: Create a stream to send and receive data
	 */
	private void createStream() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		clientMessage("\n...The streams for sending and receiving data is now setup...\n");
	}

	/*
	 * Summary: Handles client server interactions during the game
	 */
	private void duringGame() throws IOException, ClassNotFoundException {
		String message = "\n...You have connected to a dealer...\n";
		clientMessage(message);
		allowPlayerInput(true);

		do {
			try {
				message = (String) input.readObject();
				clientMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				clientMessage("\n...Invalid information sent...\n");
			}

		} while (!message.equals("END"));
	}

	/*
	 * Summary: Sends String information to the client/player's window Parameters:
	 * String data, which holds the text value of the information sent to the
	 * client/player's window
	 */
	private void clientMessage(final String data) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gameScreen.append(data);
			}
		});
	}

	/*
	 * Summary: Sends String information to the server/dealers's window Parameters:
	 * String data, which holds the text value of the information sent to the
	 * client/player's window
	 */
	private void serverMessage(String data) {
		try {
			output.writeObject(data);
			output.flush();
		} catch (IOException ioException) {
			gameScreen.append("\n...An error has occured. The information could not be sent...\n");
		}
	}

	/*
	 * Summary: Allows the JFrame text field to be editable Parameters: boolean
	 * textFieldStatus, which is true when the text field should be editable, and
	 * false otherwise
	 */
	private void allowPlayerInput(final boolean textFieldStatus) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				playerInput.setEditable(textFieldStatus);
			}
		});
	}

	/*
	 * Summary: Closes the connection stream and sockets
	 */
	private void closeConn() {
		clientMessage("\n...The connection is now being closed...\n");
		allowPlayerInput(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}
