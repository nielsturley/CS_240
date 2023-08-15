package spell;

import java.util.Locale;

public class Trie implements ITrie {
    private Node root;
    private int wordCount;
    private int nodeCount;

    public Trie() {
        root = new Node();
        wordCount = 0;
        nodeCount = 1;
    }

    @Override
    public void add(String word) {
        INode node = root;
        word = word.toLowerCase();
        for (int i = 0; i < word.length(); ++i) {
            if (node.getChildren()[word.charAt(i) - 'a'] != null) { node = node.getChildren()[word.charAt(i) - 'a']; }
            else {
                Node newNode = new Node();
                node.getChildren()[word.charAt(i) - 'a'] = newNode;
                node = newNode;
                ++nodeCount;
            }
        }
        if (node.getValue() == 0) {
            ++wordCount;
        }
        node.incrementValue();
    }

    @Override
    public INode find(String word) {
        INode node = root;
        word = word.toLowerCase();
        for (int i = 0; i < word.length(); ++i) {
            if (node.getChildren()[word.charAt(i) - 'a'] != null) { node = node.getChildren()[word.charAt(i) - 'a']; }
            else { return null; }
        }
        if (node.getValue() > 0) { return node; }
        else { return null; }
    }

    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public String toString() {

        StringBuilder curWord = new StringBuilder();
        StringBuilder output = new StringBuilder();

        toString_Helper(root, curWord, output);

        return output.toString();
    }

    private void toString_Helper(Node n, StringBuilder curWord, StringBuilder output) {
        if (n.getValue() > 0) {
            output.append(curWord.toString());
            output.append("\n");
        }

        for (int i = 0; i < n.getChildren().length; ++i) {
            Node child = (Node) n.getChildren()[i];
            if (child != null) {
                char childLetter = (char)('a' + i);
                curWord.append(childLetter);
                toString_Helper(child, curWord, output);
                curWord.deleteCharAt(curWord.length() - 1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) { return false; }
        if (o == this) { return true; }
        if (o.getClass() != this.getClass()) { return false; }

        Trie d = (Trie)o;

        if (d.getNodeCount() != this.getNodeCount() || d.getWordCount() != this.getWordCount()) { return false; }

        return equals_Helper(this.root, d.root);
    }

    private boolean equals_Helper(Node n1, Node n2) {
        if (n1.getValue() != n2.getValue()) { return false; }
        for (int i = 0; i < n1.getChildren().length; ++i) {
            if (n1.getChildren()[i] != null && n2.getChildren()[i] != null) {
                boolean equal = equals_Helper((Node) n1.getChildren()[i], (Node) n2.getChildren()[i]);
                if (!equal) { return false; }
            }
            else if (n1.getChildren()[i] == null && n2.getChildren()[i] == null) {
                //do nothing
            }
            else { return false; }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int indexCombined = 0;
        for (int i = 0; i < root.getChildren().length; ++i) {
            if (root.getChildren()[i] != null) { indexCombined += i; }
        }
        return (wordCount * nodeCount * indexCombined);
    }
}