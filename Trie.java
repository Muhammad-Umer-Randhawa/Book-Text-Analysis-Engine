import java.util.ArrayList;

// ---- Task 3 --------
public class Trie {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[39];
        boolean isEndOfWord;
    }

    private TrieNode root = new TrieNode();

    private int indexOf(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        if (c >= '0' && c <= '9') {
            return c - '0' + 26;
        }
        if (c == '\'') {
            return 36;
        }
        if (c == '-') {
            return 37;
        }
        if (c == 8217) {
            return 38;
        }
        return -1;
    }

    private void collectWords(TrieNode node, StringBuilder prefix, ArrayList<String> result) {
        if (node == null) {
            return;
        }
        if (node.isEndOfWord) {
            result.add(prefix.toString());
        }
        for (int i = 0; i < node.children.length; i++) {
            TrieNode child = node.children[i];
            if (child != null) {
                char c = getCharacterForIndex(i);
                prefix.append(c); // Append character
                collectWords(child, prefix, result);
                prefix.deleteCharAt(prefix.length() - 1); // Backtrack (remove character)
            }
        }
    }

    private char getCharacterForIndex(int index) {
        if (index >= 0 && index < 26) {
            return (char) ('a' + index);
        }
        if (index >= 26 && index < 36) {
            return (char) ('0' + (index - 26));
        }
        if (index == 36) {
            return '\'';
        }
        if (index == 37) {
            return '-';
        }
        if (index == 38) {
            return (char) 8217;
        }
        return 0;
    }

    public void insert(String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            int index = indexOf(word.charAt(i));
            if (index == -1)
                return; // letter is outside our 39 boxes, skip this word
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    public ArrayList<String> getWordsWithPrefix(String prefix) {
        ArrayList<String> result = new ArrayList<>();
        TrieNode node = root;
        for (int i = 0; i < prefix.length(); i++) {
            int index = indexOf(prefix.charAt(i));
            if (index == -1 || node.children[index] == null) {
                return result;
            }
            node = node.children[index];
        }
        StringBuilder prefixBuilder = new StringBuilder(prefix);
        collectWords(node, prefixBuilder, result);
        return result;
    }

}