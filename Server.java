import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

	private JTextField dealerInput;
	private JTextArea gameScreen;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	public String bet;
	public int totalMoney;

	/*
	 * Summary: Server Constructor
	 */
	public Server() {
		super("Blackjack Dealer");
		dealerInput = new JTextField();
		dealerInput.setEditable(false);

		// Send response to client
		dealerInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientMessage(e.getActionCommand());
				dealerInput.setText("");
			}
		});

		add(dealerInput, BorderLayout.NORTH);
		gameScreen = new JTextArea();
		add(new JScrollPane(gameScreen));
		setSize(800, 800);
		setVisible(true);
	}

	/*
	 * Summary: Set up the socket and look for connections
	 */
	public void beginProgram() throws ClassNotFoundException {
		try {
			server = new ServerSocket(6789, 100);
			while (true) {
				try {
					findConn();
					createStream();
					duringGame();
				} catch (EOFException eofException) {
					serverMessage("\n...The dealer has terminated the connection...\n");
				} finally {
					closeConn();
				}
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/*
	 * Summary: Wait to find a player to connect, then display the player's IP
	 */
	private void findConn() throws IOException {
		serverMessage("\n...Finding players for the game...\n");
		connection = server.accept();
		serverMessage("\nPlayer connected. IP: " + connection.getInetAddress().getHostName());
	}

	/*
	 * Summary: Create a stream to send and receive data
	 */
	private void createStream() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		serverMessage("\n...The streams for sending and receiving data is now setup...\n");
	}

	/*
	 * Summary: Handles client server interactions during the game
	 */
	private void duringGame() throws IOException, ClassNotFoundException {
		String message = "\n...You have connected to a player...\n";
		serverMessage(message);
		setUp();
		allowDealerInput(true);

		do {
			try {
				message = (String) input.readObject();
			} catch (ClassNotFoundException classNotFoundException) {
				serverMessage("\n...Invalid information sent...\n");
			}

		} while (!message.equals("END"));
	}

	/*
	 * Summary: Initializes the Blackjack game by taking initial bets from the
	 * player
	 */
	private void setUp() throws ClassNotFoundException, IOException {
		// Start the player off with 200 dollars
		totalMoney = 200;

		// Holds the response to whether or not the player wants to continue
		String answer;
		serverMessage(
				"Welcome to blackjack! You are the dealer. The player is starting with $" + totalMoney + "." + "\n");
		clientMessage("Welcome to blackjack! You are starting with $" + totalMoney
				+ ". how much totalMoney would you like to bet on each game?");

		// Get the players bet
		bet = (String) input.readObject();

		serverMessage("The player bets $" + bet);
		clientMessage("You have bet $" + bet + "\n Are you ready to begin the game? Yes(Y) or No(N)\n");
		answer = (String) input.readObject();

		// Call to set up the game based on the players response
		resetGame(answer);
	}

	/*
	 * Summary: Resets/restarts a Blackjack game based on the client's response
	 * *****************NOTE: INVALID RESPONSE NOT WORKIG NEEDS FIXING
	 * *************************
	 */
	private void resetGame(String answer) throws ClassNotFoundException, IOException {
		do {
			if (answer.equalsIgnoreCase("Y")) { // If the player wants to play, begin the game
				clientMessage("The game has begun");
				serverMessage("\nThe game has begun\n");
				beginBlackjack();
			} else if (answer.equalsIgnoreCase("N")) { // If the player wants to quit, terminate the connection
				clientMessage("Thanks for playing! Your total totalMoney is at $" + totalMoney);
				serverMessage("\nThe player has left\n");
				//closeConn();
			} else { // If the player enters an invalid response, prompt for a valid response
				clientMessage("Invalid response. Please enter 'Y' for Yes or 'N' for No");
				answer = (String) input.readObject();
			}
		} while (!(answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("N")));
	}

	/*
	 * Summary: Begins a game of Blackjack by handing out cards and giving the
	 * client play time
	 */
	private void beginBlackjack() throws ClassNotFoundException, IOException {
		// Create a new deck and blackjack hand for client and server
		Deck deck; // Create a new deck of cards
		deck = new Deck();
		BlackjackHand playerHand; // Create the player's hand
		playerHand = new BlackjackHand();
		BlackjackHand dealerHand; // Create the dealer's hand
		dealerHand = new BlackjackHand();

		// The deck is shuffleDeckd
		deck.shuffleDeck();

		// Two cards are added to the hand of the player and the dealer
		playerHand.addCard(deck.dealDeck());
		playerHand.addCard(deck.dealDeck());
		dealerHand.addCard(deck.dealDeck());
		dealerHand.addCard(deck.dealDeck());

		// Determine if the player has been automatically given a value of 21
		if (playerHand.getBlackjackValue() == 21) {
			clientMessage("The dealer has the cards: \n" + dealerHand.getCard(0) + "\n" + dealerHand.getCard(1));
			clientMessage("You have the cards: \n" + playerHand.getCard(0) + "\n" + playerHand.getCard(1));
			clientMessage("\nYou cards have a value of 21.\n\nYou have Blackjack. You win!");
			calculateBet(true);

			serverMessage("The player has the cards:\n " + playerHand.getCard(0) + "\n" + playerHand.getCard(1));
			serverMessage("You have the cards: \n" + dealerHand.getCard(0) + "\n" + dealerHand.getCard(1));
			serverMessage("\nThe player's cards have a value of 21.\n\nThey get Blackjack.\n\nYou lose.\n");

			clientMessage("\nWould you like to play another game? Yes(Y) or No(N)");
			String answer = (String) input.readObject();
			resetGame(answer);
		}

		// Determine if the dealer has been automatically given a value of 21
		if (dealerHand.getBlackjackValue() == 21) {
			clientMessage("The dealer has the cards: \n" + dealerHand.getCard(0) + "\n" + dealerHand.getCard(1));
			clientMessage("You have the cards: \n" + playerHand.getCard(0) + "\n" + playerHand.getCard(1));
			clientMessage("\nThe dealer's cards have a value of 21.\n\nThey get Blackjack.\n\nYou lose.\n");
			calculateBet(false);

			serverMessage("The player has the cards:\n " + playerHand.getCard(0) + "\n" + playerHand.getCard(1));
			serverMessage("You have the cards: \n" + dealerHand.getCard(0) + "\n" + dealerHand.getCard(1));
			serverMessage("\n\nYou cards have a value of 21.\n\nYou have Blackjack. You win!");

			clientMessage("\nWould you like to play another game? Yes(Y) or No(N)");
			String answer = (String) input.readObject();
			resetGame(answer);
		}

		// If no one has begun with a value of 21, we allow the player to chose to hit
		// of stand
		do {
			clientMessage("The cards in your hand are:");

			// Get all the cards in the player's hand
			for (int i = 0; i < playerHand.getCardCount(); i++) {
				clientMessage("\n" + playerHand.getCard(i));
			}

			clientMessage("The value of your cards is: " + playerHand.getBlackjackValue());
			clientMessage("\nThe dealer's top card is: " + dealerHand.getCard(0));
			clientMessage("\nWould you like to Hit(H) or Stand(S)? ");

			// Display host's/dealer's cards
			serverMessage("The cards in your hand are:");

			for (int i = 0; i < dealerHand.getCardCount(); i++) {
				serverMessage("\n" + dealerHand.getCard(i));
			}

			serverMessage("\nThe value of your cards is: " + dealerHand.getBlackjackValue());
			serverMessage("\nThe player's top card is: " + playerHand.getCard(0));

			// Holds whether the player chose to hit(H) or stand(s)
			String playerResponse;

			do {
				// Get the player's (client's) response
				playerResponse = (String) input.readObject();

				if (!(playerResponse.equalsIgnoreCase("H")) && !(playerResponse.equalsIgnoreCase("S"))) {
					clientMessage("Invalid response, please enter 'H' for Hit, or 'S' for Stand.  \n");
				}
			} while (!(playerResponse.equalsIgnoreCase("H")) && !(playerResponse.equalsIgnoreCase("S")));

			// If the player stands, the dealer can now draw cards, if they hit, a card is
			// assigned
			if (playerResponse.equalsIgnoreCase("S")) {
				break;
			} else {
				// Give player new card
				Card newCard = deck.dealDeck();
				playerHand.addCard(newCard);

				clientMessage("\nYou have decided to hit.\n");
				serverMessage("\nThe player has decided to hit.\n");
				clientMessage("The card you receive is " + newCard);
				clientMessage("\nThe total value of your cards is now: " + playerHand.getBlackjackValue());

				// If the Player goes over 21
				if (playerHand.getBlackjackValue() > 21) {
					clientMessage("\nYou went over 21. You lose.");

					// Recalculate the bet totalMoney based on the results
					calculateBet(false);

					// Update the dealer
					serverMessage("\nThe player went over 21 and lost. You win!");

					// Prompt player
					clientMessage("\nThe other card of the dealer was " + dealerHand.getCard(1));
					clientMessage("\nWould you like to play another game? Yes(Y) or No(N)");
					String answer = (String) input.readObject();
					resetGame(answer);
					break;
				}
			}

		} while (true);

		// Inform the dealer and player that the player has opted to stand
		clientMessage("You have opted to stand");
		serverMessage("\nThe player has opted to stand.");
		clientMessage("The dealer has the cards: ");
		clientMessage("\n" + dealerHand.getCard(0));
		clientMessage("\n" + dealerHand.getCard(1));

		// The player and dealer both have a value of less than 21 in their hand, so we
		// compare to find the winner
		if (playerHand.getBlackjackValue() == dealerHand.getBlackjackValue()) { // Player wins on a tie

			// Deliver game results to both the player and dealer
			clientMessage("It's a tie. You win!");
			calculateBet(false);

			serverMessage("\nIt's a tie. The player has won.\n");

			serverMessage("\nThe player will decide whether to play again or not.\n");

			// Prompt the player and reset the game
			clientMessage("\nWould you like to play another game? Yes(Y) or No(N)");
			String answer = (String) input.readObject();
			resetGame(answer);

		} else if (playerHand.getBlackjackValue() < dealerHand.getBlackjackValue()) {

			// Deliver game results to both the player and dealer
			clientMessage("The dealer has more points, so you lose.\nDealer's value: " + dealerHand.getBlackjackValue()
					+ "\nYour value: " + playerHand.getBlackjackValue());
			calculateBet(false);

			serverMessage("\nYou have more points than the player, so you win!\nPlayer's value: "
					+ playerHand.getBlackjackValue() + "\nYour value: " + dealerHand.getBlackjackValue());

			serverMessage("\nThe player will decide whether to play again or not.\n");

			// Prompt the player and reset the game
			clientMessage("\nWould you like to play another game? Yes(Y) or No(N)");
			String answer = (String) input.readObject();
			resetGame(answer);
		} else {

			// Deliver game results to both the player and dealer
			clientMessage("You have more points than the dealer, so you win!\nDealer's value: "
					+ dealerHand.getBlackjackValue() + "\nYour value: " + playerHand.getBlackjackValue());
			calculateBet(true);
			serverMessage("\nYou have less points than the player, so you lose.\nPlayer's value: "
					+ playerHand.getBlackjackValue() + "\nYour value: " + dealerHand.getBlackjackValue());

			serverMessage("\nThe player will decide whether to play again or not.\n");

			// Prompt the player and reset the game
			clientMessage("\nWould you like to play another game? Yes(Y) or No(N)");
			String answer = (String) input.readObject();
			resetGame(answer);
		}
	}

	/*
	 * Summary: Calculates the player's totalMoney after the completion of a game.
	 * If the player wins, the bet is added onto their total money, but if the
	 * player loses, the bet is taken away Parameters: boolean status, true if the
	 * player has won the game, false otherwise
	 */
	private void calculateBet(boolean status) {
		if (status) { // If the player has won
			totalMoney += Integer.parseInt(bet);
			clientMessage("Your total money is now at $" + totalMoney);
		} else { // If the player has lost
			totalMoney -= Integer.parseInt(bet);
			clientMessage("Your total money is now at $" + totalMoney);
		}
	}

	/*
	 * Summary: Sends String information to the server/dealers's window Parameters:
	 * String data, which holds the text value of the information sent to the
	 * client/player's window
	 */
	private void serverMessage(final String data) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gameScreen.append(data);
			}
		});
	}

	/*
	 * Summary: Sends String information to the client/player's window Parameters:
	 * String data, which holds the text value of the information sent to the
	 * client/player's window
	 */
	private void clientMessage(String data) {
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
	private void allowDealerInput(final boolean textFieldStatus) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dealerInput.setEditable(textFieldStatus);
			}
		});
	}

	/*
	 * Summary: Closes the connection stream and sockets
	 */
	private void closeConn() {
		serverMessage("\n...The connection is now being closed...\n");
		allowDealerInput(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

}
