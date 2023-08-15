package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    private Set<String> allWords;
    private SortedSet<Character> guessedChars;
    private String curWord;
    private int numOfLastGuess;
    private boolean winnerWinnerChickenDinner;

    public EvilHangmanGame() {
        allWords = new HashSet<>();
        guessedChars = new TreeSet<>();
        curWord = "";
        numOfLastGuess = 0;
        winnerWinnerChickenDinner = false;
    }

    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        allWords.clear();
        guessedChars.clear();
        curWord = "";
        numOfLastGuess = 0;
        winnerWinnerChickenDinner = false;
        try (Scanner scanner = new Scanner(dictionary)) {
            while (scanner.hasNext()) {
                String str = scanner.next();
                if (str.length() == wordLength) {
                    allWords.add(str);
                }
            }

            if (allWords.isEmpty()) {
                throw new EmptyDictionaryException();
            }

            StringBuilder s = new StringBuilder();
            for (int i = 0; i < wordLength; ++i) {
                s.append('-');
            }
            curWord = String.valueOf(s);
        }

    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);
        if (guessedChars.contains(guess)) { throw new GuessAlreadyMadeException(); }
        guessedChars.add(guess);

        Map<String, Set<String>> wordGroups = new HashMap<>();
        for (String s : allWords) {
            StringBuilder key = new StringBuilder();
            for (int i = 0; i < s.length(); ++i) {
                if (s.charAt(i) == guess) {
                    key.append(guess);
                }
                else {
                    key.append('-');
                }
            }
            if (wordGroups.containsKey(key.toString())) {
                wordGroups.get(key.toString()).add(s);
            }
            else {
                Set<String> strings = new HashSet<>();
                strings.add(s);
                wordGroups.put(key.toString(), strings);
            }
        }

        Set<String> evilSet = new HashSet<>();
        int mostWords = 0;
        for (Map.Entry<String, Set<String>> entry : wordGroups.entrySet()) {
            if (entry.getValue().size() == mostWords) {
                evilSet.add(entry.getKey());
                continue;
            }
            if (entry.getValue().size() > mostWords) {
                evilSet.clear();
                evilSet.add(entry.getKey());
                mostWords = entry.getValue().size();
            }
        }

        allWords.clear();

        if (evilSet.size() == 1) {
            allWords.addAll(wordGroups.get(evilSet.iterator().next()));
            setCurWord(evilSet.iterator().next());
            return allWords;
        }


        Map<String, Integer> counter = new HashMap<>();

        for (String s : evilSet) {
            int numOfLetters = 0;
            char[] chars = s.toCharArray();
            for (char c : chars) {
                if (c == guess) {
                    ++numOfLetters;
                }
            }
            if (numOfLetters == 0) {
                allWords.addAll(wordGroups.get(s));
                return allWords;
            }
            else {
                counter.put(s, numOfLetters);
            }
        }

        int numOfLetters = 0;
        int stringLength = 0;
        Set<String> moreKeyComparison = new HashSet<>();
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            if (numOfLetters == 0) {
                numOfLetters = entry.getValue();
                moreKeyComparison.add(entry.getKey());
                stringLength = entry.getKey().length();
                continue;
            }
            if (entry.getValue() == numOfLetters) {
                moreKeyComparison.add(entry.getKey());
                continue;
            }
            if (entry.getValue() < numOfLetters) {
                moreKeyComparison.clear();
                moreKeyComparison.add(entry.getKey());
                numOfLetters = entry.getValue();
                continue;
            }
        }

        if (moreKeyComparison.size() == 1) {
            allWords.addAll(wordGroups.get(moreKeyComparison.iterator().next()));
            setCurWord(moreKeyComparison.iterator().next());
            return allWords;
        }

        boolean found = false;
        int charAtCheck = stringLength - 1;
        Set<String> rightMostWords = new HashSet<>();
        while (!found) {
            for (String s : moreKeyComparison) {
                if (s.charAt(charAtCheck) == guess) {
                    rightMostWords.add(s);
                }
            }
            if (rightMostWords.size() == 1) {
                allWords.addAll(wordGroups.get(rightMostWords.iterator().next()));
                setCurWord(rightMostWords.iterator().next());
                return allWords;
            }
            if (rightMostWords.size() != 0) {
                moreKeyComparison.clear();
                moreKeyComparison.addAll(rightMostWords);
                rightMostWords.clear();
            }
            --charAtCheck;
        }

        return null;



    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return guessedChars;
    }

    public String getCurWord() {
        return curWord;
    }

    public int getNumOfLastGuess() {
        return numOfLastGuess;
    }

    private void setCurWord(String key) {
        char[] chars = key.toCharArray();
        StringBuilder word = new StringBuilder(curWord);
        int numChars = 0;
        for (int i = 0; i < chars.length; ++i) {
            if (!(chars[i] == '-')) {
                word.setCharAt(i, chars[i]);
                ++numChars;
            }
        }
        curWord = word.toString();
        if (!curWord.contains("-")) {
            winnerWinnerChickenDinner = true;
        }
        numOfLastGuess = numChars;
    }

    public boolean didYouWin() {
        return winnerWinnerChickenDinner;
    }
}
