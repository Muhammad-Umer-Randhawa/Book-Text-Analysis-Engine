import java.io.PrintStream;
import java.util.*;

// Main only shows the menu, reads the input, and calls the class that does the work.
//   BookLoader    -> reads and cleans the book
//   WordStats     -> Task 1 (word counts, top N)
//   NeighborIndex -> Task 2 (neighboring words)
//   Trie          -> Task 3 (prefix search)
//   InvertedIndex -> Task 4 (line/page range retrieval)
public class Main {
    static final String FILE_PATH = "D:\\DSA in JAVA 3rd SEM\\Project\\Book Text Analytics Engine\\Great_Expectations.txt";
    static Scanner s = new Scanner(System.in);
    static WordStats stats;
    static NeighborIndex neighbors;
    static Trie trie;
    static InvertedIndex invertedIndex;

    public static void main(String[] args) throws Exception {
        setupConsole();
        if (!loadBook()) return;
        printBanner();

        boolean do_again = true;
        while (do_again) {
            printMenu();
            try {
                switch (s.nextInt()) {
                    case 1 -> showTotalWords();
                    case 2 -> showUniqueWords();
                    case 3 -> showTopWords();
                    case 4 -> showNeighbors();
                    case 5 -> searchSimilar();
                    case 6 -> searchByPageRange();
                    case 0 -> do_again = false;
                    default -> System.out.println("Invalid option, choose 0-6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number.");
                s.nextLine(); // clear the bad input so the loop doesn't repeat forever
            }
        }
        s.close();
    }

    // Makes the console print UTF-8 correctly (safety net for non-English characters)
    public static void setupConsole() throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            new ProcessBuilder("cmd", "/c", "chcp 65001 > nul").inheritIO().start().waitFor();
        }
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
    }

    public static void printBanner() {
        System.out.println("========================================================================");
        System.out.println("                      Book Text Analytics Engine                        ");
        System.out.println("========================================================================");
    }

    public static void printMenu() {
        System.out.println("1. View total word count");
        System.out.println("2: View the # of non-duplicate unique words");
        System.out.println("3: View the count of (N) most occuring words");
        System.out.println("4: View the neighbors of a word");
        System.out.println("5. Search similar words");
        System.out.println("6: Search word by page range");
        System.out.println("0: Exit");
        System.out.println("What do you wanna do? ");
    }

    // Loads the book and builds the structures once. Returns false if the file failed.
    public static boolean loadBook() {
        ArrayList<String> words = BookLoader.readFile(FILE_PATH);
        if (words.isEmpty()) {
            return false;
        }
        stats = new WordStats(words);
        neighbors = new NeighborIndex(words);
        trie = new Trie();
        for (String w : stats.getUniqueWordList()) {
            trie.insert(w);
        }
        invertedIndex = new InvertedIndex(FILE_PATH);
        return true;
    }

    // Asks for a word and cleans it the same way the book was cleaned.
    // Returns null if nothing usable was typed.
    public static String readWord() {
        System.out.print("Enter a word: ");
        ArrayList<String> cleaned = BookLoader.tokenize(s.next());
        if (cleaned.isEmpty()) {
            System.out.println("Invalid input.");
            return null;
        }
        return cleaned.get(0);
    }

    public static void showTotalWords() {
        System.out.println("Total words in the book: " + stats.getTotalWords());
    }

    public static void showUniqueWords() {
        System.out.println("Unique words in the book: " + stats.getUniqueWords());
    }

    public static void showTopWords() {
        System.out.print("How many Top most words do you want? ");
        int n = s.nextInt();
        if (n <= 0) {
            System.out.println("Enter a number greater than 0.");
            return;
        }
        stats.printTopWords(n);
    }

    public static void showNeighbors() {
        String w = readWord();
        if (w != null) neighbors.getNeighbors(w);
    }

    public static void searchSimilar() {
        System.out.print("Enter a prefix: ");
        String p = s.next().toLowerCase();
        ArrayList<String> found = trie.getWordsWithPrefix(p);
        System.out.println(found.size() + " words start with \"" + p + "\":");
        System.out.println(found);
    }

    // Task 4: sub-menu to search a word in a page range, or show all its occurrences.
    public static void searchByPageRange() {
        System.out.println("  a) Search word in a specific page range");
        System.out.println("  b) Show ALL occurrences of a word");
        System.out.print("Choice (a/b): ");
        String choice = s.next().toLowerCase();
        if (!choice.equals("a") && !choice.equals("b")) {
            System.out.println("Unknown choice.");
            return;
        }

        String word = readWord();
        if (word == null) return;

        if (choice.equals("b")) {
            invertedIndex.showAllOccurrences(word);
            return;
        }

        int totalPages = invertedIndex.getTotalPages();
        System.out.print("Start page (1-" + totalPages + "): ");
        int start = s.nextInt();
        System.out.print("End page   (1-" + totalPages + "): ");
        int end = s.nextInt();
        if (start < 1 || end < start) {
            System.out.println("Invalid page range.");
            return;
        }
        invertedIndex.searchInPageRange(word, start, end);
    }
}