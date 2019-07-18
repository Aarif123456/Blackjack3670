public class Card {

	// Face value cards
	public final static int ACE = 1, JACK = 11, QUEEN = 12, KING = 13;

	// Suits
	public final static int SPADES = 0, HEARTS = 1, DIAMONDS = 2, CLUBS = 3;
	
	private final int value; // The numerical value of a card
	private final int suit;  // The suit of a card
	
	public Card(int theValue, int theSuit) {
		// Construct a card with the specified value and suit.
		// Value must be between 1 and 13. Suit must be between
		// 0 and 3. If the parameters are outside these ranges,
		// the constructed card object will be invalid.
		value = theValue;
		suit = theSuit;
	}

	public int getSuit() {
		// Return the int that codes for this card's suit.
		return suit;
	}

	public int getValue() {
		// Return the int that codes for this card's value.
		return value;
	}

	public String getSuitAsString() {
		// Return a String representing the card's suit.
		// (If the card's suit is invalid, "??" is returned.)
		switch (suit) {
		case SPADES:
			return "Spades";
		case HEARTS:
			return "Hearts";
		case DIAMONDS:
			return "Diamonds";
		case CLUBS:
			return "Clubs";
		default:
			return "??";
		}
	}

	public String getValueAsString() {
		// Return a String representing the card's value.
		// If the card's value is invalid, "??" is returned.
		switch (value) {
		case 1:
			return "Ace";
		case 2:
			return "2";
		case 3:
			return "3";
		case 4:
			return "4";
		case 5:
			return "5";
		case 6:
			return "6";
		case 7:
			return "7";
		case 8:
			return "8";
		case 9:
			return "9";
		case 10:
			return "10";
		case 11:
			return "Jack";
		case 12:
			return "Queen";
		case 13:
			return "King";
		default:
			return "??";
		}
	}

	public String toString() {
		// Return a String representation of this card, such as
		// "10 of Hearts" or "Queen of Spades".
		return getValueAsString() + " of " + getSuitAsString();
	}

} // end class Card