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
    // Time: O(n^2)
    public void printTopWords(int n) {
        ArrayList<String> picked = new ArrayList<>();
        ArrayList<String> keys = table.getKeys();
        if (n > keys.size()) n = keys.size();
        for (int i = 0; i < n; i++) {
            String bestWord = null;
            int bestCount = 0;
            for (String w : keys) {
                if (picked.contains(w)) continue;
                Object a = table.get(w);
                int count = (Integer) a;
                if (count > bestCount) {
                    bestWord = w;
                    bestCount = count;
                }
            }
            if (bestWord == null) break;
            picked.add(bestWord);
            System.out.println("Most appeared word: " + bestWord);
            System.out.println("It's count: " + bestCount);
        }
    }
    public ArrayList<String> getUniqueWordList() {
        return table.getKeys();
    }
}