public class Card {

	// Face value cards
	//public final static int ACE = 1, JACK = 11, QUEEN = 12, KING = 13;

	// Suits
	private final static String suits={"Spades", "Hearts", "Diamonds", "Clubs"};
	private final static String faceCard={"Jack", "Queen", "King"};
	private final int rank,suit,value; // rank, suite and value of card
	private final String val;
	public Card(int r, int s) {
		// Construct a card with the rank and suit.
		
		this.rank = r;
		this.suit = s%4; //force correct value
		if(rank==1){ //for ace
			value=1;
			val="ace";
		}
		else if(rank<=10){ //for numerical card
			value=rank;
			val=Integer.toString(rank);
		}
		else{  //face cards
			value=10;
			val=faceCard[(r+1)%3];
		}
	}

	public int getSuit() {
		// Return the int that codes for this card's suit.
		return suit;
	}

	public int getRank() {
		// Return the int that codes for this card's rank.
		return rank;
	}
	public int getValue() {
		// Return the int that codes for this card's rank.
		return value;
	}
	public boolean isAce(){ //check is card is Ace because Aces act weird
		return (rank==1)?true:false;
	}

	@Override
	public String toString() {
		// Return a String representation of this card
		return val + " of " + suits[suit];
	}
	/*
	@Override
	public boolean equals(Object o){
		if (this == o) 
            return true;
            
        if (o == null || getClass() != o.getClass()) 
            return false;
        Card c= (Card) o;
        return rank == o.value;     
	}*/

} 