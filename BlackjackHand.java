/* 
  Class that handles the card in players hand
*/

import java.util.*;
public class BlackjackHand {
    LinkedList<Card> hand;
  public  BlackjackHand(){ //constructor for blackjack
    hand = new LinkedList<Card>();
  }

  public int getBlackjackValue() {
  // Returns the value of this hand 
  int val = 0; //total value of cards in hand
  int hasAce = false;

  for ( Card card : hand ) {
     if (card.isAce()){
         hasAce = true;     // player has atleast one ace
     }
     val += card.getValue(); 
  }
     /* Handle case for aces. Aces can be worth 1 or 11. 2 Aces at 11 is 22, which 
     is a bust. So, we can only have one Ace be worth 11. And, ace will only be worth 
     11 if the total value of the hand will be less than 21*/

  if ( hasAce  &&  val + 10 <= 21 )
    val +=10;

  return val;

  }  

  public void clear() {
      // Discard all the cards from the hand.
      hand.clear();
   }
   
   public void addCard(Card c) { //add card to hand
      hand.add(c);
   }
   
   public void removeCard(Card c) {// remove specified card from hand
      if(c!=null)
        hand.remove(c); //remove card if it exists
   }
   
   public void removeCard(int position) {
         // remove card at give position
      if (position >= 0 && position < hand.size())
         hand.remove(position);
      else
        System.out.println("ERROR:Cannot remove card at position"+position);
   }
   
   public int getCardCount() {
      // Return the how many card the player has in their hands.
      return hand.size();
   }
   
   public Card getCard(int position) {
          // Get the card using position
      if (position >= 0 && position < hand.size())
        return (Card)hand.get(position);
      else{
        System.out.println("ERROR:Cannot retrieve card at position"+position);
        return null;
      }
   }

} 
