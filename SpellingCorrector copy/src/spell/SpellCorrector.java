package spell;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpellCorrector implements ISpellCorrector {
    private Trie dictionary;
    private HashSet<String> similarWords;

    public SpellCorrector() {
        dictionary = new Trie();
        similarWords = new HashSet<String>();
    }

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        File file = new File(dictionaryFileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String str = scanner.next();
            dictionary.add(str);
        }
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        similarWords.clear();
        inputWord = inputWord.toLowerCase();
        if (dictionary.find(inputWord) != null) {
            return inputWord;
        }
        generateSimilarWords(inputWord);
        String suggestedWord = bestMatchWord();
        if (suggestedWord != null) {
            return suggestedWord;
        }
        else {
            List<String> distance1Words = new ArrayList<>();
            distance1Words.addAll(similarWords);
            similarWords.clear();
            for (String str : distance1Words) {
                generateSimilarWords(str);
            }
            suggestedWord = bestMatchWord();
            if (suggestedWord != null) {
                return suggestedWord;
            }
        }
        return null;
    }

    private void generateSimilarWords(String inputWord) {
        StringBuilder inWord = new StringBuilder(inputWord);


        //Use the wrong character (generates 25n words)
        for (int i = 0; i < inputWord.length(); ++i) {
            for (int j = 'a'; j <= 'z'; ++j) {
                if (inputWord.charAt(i) != (char) j) {
                    StringBuilder simWord = new StringBuilder(inWord);
                    simWord.setCharAt(i, (char) j);
                    similarWords.add(simWord.toString());

                }
            }
        }

        //Omit a character (generates n words)
        for (int i = 0; i < inputWord.length(); ++i) {
            StringBuilder simWord = new StringBuilder(inWord);
            simWord.deleteCharAt(i);
            similarWords.add(simWord.toString());
        }

        //Insert an extra character (generates 26(n+1) words)
        for (int i = 0; i < inputWord.length() + 1; ++i) {
            for (int j = 'a'; j <= 'z'; ++j) {
                StringBuilder simWord = new StringBuilder(inWord);
                simWord.insert(i, (char) j);
                similarWords.add(simWord.toString());

            }
        }

        //Transpose two adjacent characters (generates n-1 words)
        for (int i = 1; i < inputWord.length(); ++i) {
            StringBuilder simWord = new StringBuilder(inWord);
            char c1 = simWord.charAt(i - 1), c2 = simWord.charAt(i);
            simWord.setCharAt(i, c1);
            simWord.setCharAt(i - 1, c2);
            similarWords.add(simWord.toString());
            //System.out.println(simWord);
        }
    }

    private String bestMatchWord() {
        int maxCount = 0;
        List<String> mostSimilar = new ArrayList<>();
        for (String str : similarWords) {
            if (dictionary.find(str) != null) {
                if (dictionary.find(str).getValue() == maxCount) {
                    mostSimilar.add(str);
                }
                if (dictionary.find(str).getValue() > maxCount) {
                    mostSimilar.clear();
                    mostSimilar.add(str);
                    maxCount = dictionary.find(str).getValue();
                }
            }
        }
        if (mostSimilar.size() == 0) { return null; }
        Collections.sort(mostSimilar);
        return mostSimilar.get(0);
    }
}
