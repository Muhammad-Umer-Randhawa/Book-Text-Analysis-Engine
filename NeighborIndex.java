import java.util.ArrayList;

// Task 2: neighboring words (bi-grams).
// Two tables, each a hash table inside a hash table:
//   nextTable: word -> (word that comes right AFTER it  -> count)
//   prevTable: word -> (word that comes right BEFORE it -> count)
// n = number of words in the book, d = number of different neighbors of one word
public class NeighborIndex {
    private CompleteHashtable nextTable;
    private CompleteHashtable prevTable;

    // MAIN 1: builds both tables from the word list
    // Time: O(n) -> goes through the book once, each step is quick
    // Space: O(n) -> in the worst case every pair of words is different
    public NeighborIndex(ArrayList<String> words) {
        nextTable = new CompleteHashtable(words.size());
        prevTable = new CompleteHashtable(words.size());
        for (int i = 0; i < words.size() - 1; i++) {
            String current = words.get(i);
            String next = words.get(i + 1);
            addPair(nextTable, current, next); // next comes after current
            addPair(prevTable, next, current); // current comes before next
        }
    }

    // MAIN 2: the lookup function the task asks for
    // Time: O(d) -> finds the word instantly, then prints only its d neighbors
    public void getNeighbors(String word) {
        if (nextTable.get(word) == null && prevTable.get(word) == null) {
            System.out.println("\"" + word + "\" does not appear in the book.");
            return;
        }
        printNeighborList("Words that come AFTER \"" + word + "\":", nextTable, word);
        printNeighborList("Words that come BEFORE \"" + word + "\":", prevTable, word);
    }

    // HELPER: remembers that "neighbor" appeared right next to "word" one more
    // time.
    // Example: addPair(nextTable, "dark", "night") -> "night" came right after
    // "dark".
    // Time: O(1) -> a few quick lookups, no loops
    private void addPair(CompleteHashtable t, String word, String neighbor) {

        // An empty variable. Later it will point to the inner table that belongs to
        // "word".
        CompleteHashtable neighbors;

        // Ask t: "is anything stored under this word?"
        // inner = null if not found, or the existing inner table if found.
        Object inner = t.get(word);

        if (inner == null) {
            // NOT FOUND: this word has no inner table yet (first time we see it).

            // Make a brand new empty table. neighbors points to it.
            neighbors = new CompleteHashtable(16);

            // Store that new table inside t, with the word as the key.
            t.put(word, neighbors);
        } else {
            // FOUND: this word already has an inner table from an earlier call.

            // Point neighbors at that same table. Nothing new is created.
            // (The cast is needed because t stores plain Objects.)
            neighbors = (CompleteHashtable) inner;
        }

        // From here on, neighbors is the right inner table, in both cases.

        // Ask the inner table: "how many times have we counted this neighbor?"
        // c = null if never counted, or the count if found.
        Object c = neighbors.get(neighbor);

        if (c == null) {
            // NOT FOUND: first time this neighbor appears next to the word, so start at 1.
            neighbors.put(neighbor, 1);
        } else {
            // FOUND: counted before, so take the old count (c), add 1, and store it again.
            neighbors.put(neighbor, (Integer) c + 1);
        }
    }

    // HELPER: prints one list of neighbors with their counts.
    // title = the heading to print, t = nextTable or prevTable, word = the word we
    // look up.
    // Time: O(d) -> prints each of the d neighbors once
    private void printNeighborList(String title, CompleteHashtable t, String word) {

        // Print the heading, for example: Words that come AFTER "dark":
        System.out.println(title);

        // Ask t: "is anything stored under this word?"
        Object inner = t.get(word);

        if (inner == null) {
            // NOT FOUND: the word has no neighbors on this side (it is at the edge of the
            // book).
            System.out.println("  (none)");
            return; // stop the method here, nothing below runs
        }

        // FOUND: neighbors points to the word's inner table.
        CompleteHashtable neighbors = (CompleteHashtable) inner;

        // getKeys() gives the list of all neighbor words. The loop runs once for each
        // one.
        for (String n : neighbors.getKeys()) {

            // Look up this neighbor's count and print one line, for example: night -> 2
            System.out.println("  " + n + " -> " + neighbors.get(n));
        }
    }
}