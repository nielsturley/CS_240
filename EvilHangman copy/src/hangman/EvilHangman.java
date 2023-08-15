package hangman;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangman {

    public static void main(String[] args) {
        File file = new File(args[0]);
        int wordLength = Integer.parseInt(args[1]);
        int guesses = Integer.parseInt(args[2]);

        if (guesses <= 0) {
            System.out.println("No guesses! You'll never win that way.");
            System.exit(2);
        }

        EvilHangmanGame game = new EvilHangmanGame();

        try {
            game.startGame(file, wordLength);
        }
        catch (EmptyDictionaryException e) {
            System.out.println("No dictionary!");
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> evilWords = new HashSet<>();
        boolean stillHaveGuesses = true;
        while (stillHaveGuesses) {
            if (game.didYouWin()) { break; }
            System.out.println("You have " + guesses + " guesses left");
            System.out.print("Used letters: ");
            boolean first = true;
            for (Character c : game.getGuessedLetters()) {
                if (first) {
                    System.out.print(c);
                    first = false;
                }
                else {
                    System.out.print(" " + c);
                }
            }
            System.out.print("\n");
            System.out.println("Word: " + game.getCurWord());
            while (true) {
                System.out.print("Enter guess: ");
                Scanner guess = new Scanner(System.in);
                String s = guess.nextLine();
                if (s.length() != 1 || !Character.isLetter(s.charAt(0))) {
                    System.out.println("Invalid input!");
                    continue;
                }
                try {
                    char c = s.charAt(0);
                    evilWords = game.makeGuess(c);
                    if (game.getNumOfLastGuess() == 0) {
                        System.out.println("Sorry, there are no " + s + "'s\n");
                        --guesses;
                    }
                    else {
                        System.out.println("Yes, there is " + game.getNumOfLastGuess() + " " + s + "\n");
                        break;
                    }
                }
                catch (GuessAlreadyMadeException e) {
                    System.out.println("Guess already made!");
                    continue;
                }
                if (guesses == 0) {
                    stillHaveGuesses = false;
                }
                break;
            }

        }
        if (!game.didYouWin()) {
            System.out.println("You lose! The word was: " + evilWords.iterator().next());
        }
        else {
            System.out.println("You win! The word was: " + game.getCurWord());
        }


    }

}
