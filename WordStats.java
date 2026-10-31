import java.util.ArrayList;

// Task 1: word count statistics.
// Holds the word list and a hash table of word -> how many times it appears.
public class WordStats {
    private ArrayList<String> words;
    private CompleteHashtable table;

    // Builds the frequency table once.
    // Time: O(n) -> n = number of words in the book
    public WordStats(ArrayList<String> words) {
        this.words = words;
        table = new CompleteHashtable(words.size());
        for (String w : words) {
            Object a = table.get(w);
            if (a == null) {
                table.put(w, 1);
            } else {
                table.put(w, (Integer) a + 1);
            }
        }
    }

    // Time: O(1)
    public int getTotalWords() {
        return words.size();
    }

    // Time: O(1)
    public int getUniqueWords() {
        return table.size();
    }

    // Prints the n most used words with their counts.
    // Uses sorting instead of repeated full scans to keep the task faster.
    public void printTopWords(int n) {
        ArrayList<String> keys = table.getKeys();
        if (n > keys.size())
            n = keys.size();

        java.util.ArrayList<java.util.Map.Entry<String, Integer>> entries = new java.util.ArrayList<>();
        for (String key : keys) {
            entries.add(new java.util.AbstractMap.SimpleEntry<>(key, (Integer) table.get(key)));
        }
        entries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (int i = 0; i < n; i++) {
            java.util.Map.Entry<String, Integer> entry = entries.get(i);
            System.out.println("Most appeared word: " + entry.getKey());
            System.out.println("It's count: " + entry.getValue());
        }
    }

    public ArrayList<String> getUniqueWordList() {
        return table.getKeys();
    }
}