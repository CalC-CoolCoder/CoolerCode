/**
A game of Go Fish between a user and the computer.
@author Calvin C.
@version 20241031
*/
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class GoFish {
    /**
    Removes a card from the draw pile and adds it to a player's hand.
    Cards are stored as arrays containing the rank and suit.
    @param hand the player's hand
    @param draw the draw pile
    @return the card drawn
    */
    public static String[] drawCard(ArrayList<String[]> hand, ArrayList<String[]> draw) {
        Random rand = new Random();
        int cardIndex = rand.nextInt(draw.size());
        String[] card = draw.get(cardIndex);
        hand.add(card);
        draw.remove(card);
        return card;
    }
    /**
    Converts a player's hand from an ArrayList to a string.
    @param hand the player's hand as an ArrayList
    @return the player's hand as a string
    */
    public static String printHand(ArrayList<String[]> hand) {
        String cards = "";
        for (String[] card : hand) {
            cards += card[0] + card[1] + " ";
        }
        return cards;
    }
    /**
    "Books" are sets of four cards of matching rank.
    Searches a player's hand for books and adds the number to the player's score.
    @param hand the player's hand
    @param name the first part of the output, "You make" or "The computer makes"
    @param ranks list of all possible card ranks
    @return the number of points earned
    */
    public static int checkBooks(ArrayList<String[]> hand, String name, String[] ranks) {
        int score = 0;
        for (String rank : ranks) {
            int matches = 0;
            ArrayList<String[]> matchingCards = new ArrayList<>();
            for (String[] card : hand) {
                if (card[0].equals(rank)) {
                    matches++;
                    matchingCards.add(card);
                }
            }
            if (matches == 4) {
                System.out.println(name + " a book of " + rank + "s.");
                score++;
                for (String[] card : matchingCards) {
                    hand.remove(card);
                }
            }
        }
        return score;
    }
    /**
    Asks the user to input a card rank.
    @param hand the user's hand
    @param scanner a Scanner object
    @return the rank chosen
    */
    public static String askCardUser(ArrayList<String[]> hand, Scanner scanner) {
        boolean valid = false;
        String rank = "";
        while (!valid) {
            System.out.print("What cards do you want? ");
            rank = scanner.nextLine();
            for (String[] card : hand) {
                if (card[0].equals(rank)) {
                    valid = true;
                }
            }
            if (!valid) {
                System.out.println("You do not have any cards of that rank.");
            }
        }
        return rank;
    }
    /**
    Picks a random card from the computer's hand and returns its rank.
    @param hand the computer's hand
    @return the rank chosen
    */
    public static String askCardCPU(ArrayList<String[]> hand) {
        Random rand = new Random();
        int cardIndex = rand.nextInt(hand.size());
        String[] card = hand.get(cardIndex);
        return card[0];
    }
    /**
    Removes cards of a certain rank from one player's hand and adds it to another's.
    @param giver the player giving cards
    @param taker the player taking cards
    @param rank the rank of the cards being given
    @return the number of cards given
    */
    public static int giveCards(ArrayList<String[]> giver, ArrayList<String[]> taker, String rank) {
        int matches = 0;
        ArrayList<String[]> discard = new ArrayList<>();
        for (String[] card : giver) {
            if (card[0].equals(rank)) {
                taker.add(card);
                discard.add(card);
                matches++;
            }
        }
        for (String[] card : discard) {
            giver.remove(card);
        }
        return matches;
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        final String[] SUITS = {"\u2660", "\u2661", "\u2662", "\u2663"};
        // create a draw pile with 52 cards
        ArrayList<String[]> draw = new ArrayList<>();
        for (String rank : RANKS) {
            for (String suit : SUITS) {
                String[] card = {rank, suit};
                draw.add(card);
            }
        }
        System.out.println("For card ranks, type 2-10, J for jacks, Q for queens, K for kings, or A for aces.");
        // deal 7 cards to each player
        ArrayList<String[]> userHand = new ArrayList<>();
        ArrayList<String[]> cpuHand = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            drawCard(userHand, draw);
            drawCard(cpuHand, draw);
        }
        // check for any books at the start
        int userScore = 0;
        int cpuScore = 0;
        userScore += checkBooks(userHand, "You make", RANKS);
        cpuScore += checkBooks(cpuHand, "The computer makes", RANKS);
        boolean userTurn = true;
        // game ends when all cards are in books
        while (userHand.size() > 0 || cpuHand.size() > 0 || draw.size() > 0) {
            // user's turn
            if (userTurn) {
                // if hand is empty, draw a card
                if (userHand.size() == 0 && draw.size() > 0) {
                    drawCard(userHand, draw);
                    System.out.println("Your hand is empty, so you draw a card.");
                }
                if (userHand.size() > 0) {
                    System.out.println("Your hand: " + printHand(userHand));
                    String rank = askCardUser(userHand, in);
                    int matches = giveCards(cpuHand, userHand, rank);
                    // if given no cards, draw a card
                    if (matches == 0) {
                        System.out.println("The computer says, \"Go fish.\"");
                        String[] card = drawCard(userHand, draw);
                        System.out.println("You draw the card " + card[0] + card[1] + ".");
                        // if drawn card is of the rank asked for, take another turn
                        if (card[0].equals(rank)) {
                            System.out.println("You get another turn.");
                        } else {
                            userTurn = false;
                        }
                    } else {
                        // if given cards, take another turn
                        System.out.print("The computer gives you " + matches);
                        if (matches == 1) {
                            System.out.println(" card.");
                        } else {
                            System.out.println(" cards.");
                        }
                    }
                    userScore += checkBooks(userHand, "You make", RANKS);
                }
            // computer's turn
            } else {
                if (cpuHand.size() == 0 && draw.size() > 0) {
                    drawCard(cpuHand, draw);
                }
                if (cpuHand.size() > 0) {
                    System.out.println("It is the computer's turn.");
                    String rank = askCardCPU(cpuHand);
                    System.out.println("The computer asks you for " + rank + "s.");
                    int matches = giveCards(userHand, cpuHand, rank);
                    if (matches == 0) {
                        System.out.println("You say, \"Go fish.\"");
                        String[] card = drawCard(cpuHand, draw);
                        if (card[0].equals(rank)) {
                            System.out.println("The computer draws the card " + card[0] + card[1] + " and gets another turn.");
                        } else {
                            userTurn = true;
                        }
                    } else {
                        System.out.print("You give the computer " + matches);
                        if (matches == 1) {
                            System.out.println(" card.");
                        } else {
                            System.out.println(" cards.");
                        }
                    }
                    cpuScore += checkBooks(cpuHand, "The computer makes", RANKS);
                }
            }
        }
        System.out.println("Your score: " + userScore);
        System.out.println("The computer's score: " + cpuScore);
        if (userScore > cpuScore) {
            System.out.println("You win!");
        } else {
            System.out.println("The computer wins!");
        }
        in.close();
    }
}