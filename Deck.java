public class Deck {

	private Card[] deck; 	//Used to hold the deck array
	private int usedCards;	//Used to store the used cards

	public Deck() {
		//Initialize a new deck of 52 cards
		deck = new Card[52];
		
		//Set the number of cards and used cards created to zero
		int createdCards = 0;
		usedCards = 0;
		
		// Loop through the suits of the cards, and then the numbers of the cards to add them to the deck
		for (int suit = 0; suit <= 3; suit++) {
			for (int num = 1; num <= 13; num++) {
				deck[createdCards] = new Card(num, suit);
				createdCards++;
			}
		}		
	}

	public Card dealDeck() {
		// If all of the cards are used, call the method to shuffle the deck
		if (usedCards == 52) {
			shuffleDeck();
		}
		
		// Return the deck of cards
		usedCards++;
		return deck[usedCards - 1];
	}

	public void shuffleDeck() {
		// Put the cards back in the deck and shuffle them 
		for (int i = 51; i > 0; i--) {
			int randomNum = (int) (Math.random() * (i + 1));
			Card temp = deck[i];
			deck[i] = deck[randomNum];
			deck[randomNum] = temp;
		}
		usedCards = 0;
	}

	// Return number of cards still in the deck
	public int leftoverCards() {
		return 52 - usedCards;
	}
}