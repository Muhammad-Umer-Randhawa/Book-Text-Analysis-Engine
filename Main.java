import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// Main only shows the menu, reads the input, and calls the class that does the work.
//   BookLoader    -> reads and cleans the book
//   WordStats     -> Task 1 (word counts, top N)
//   NeighborIndex -> Task 2 (neighboring words)
//   Trie          -> Task 3 (prefix search)
//   InvertedIndex -> Task 4 (line/page range retrieval)
public class Main {
    static final String FILE_PATH = "D:\\DSA in JAVA 3rd SEM\\Project\\Book Text Analytics Engine\\Great_Expectations.txt";
    static Scanner s = new Scanner(System.in);
    static ArrayList<String> words;
    static WordStats stats;
    static NeighborIndex neighbors;
    static Trie trie;
    static InvertedIndex invertedIndex;

    public static void main(String[] args) throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("win")) { // This is the safety net for any other
                                                                           // non-English character that console has
                                                                           // trouble rendering
            new ProcessBuilder("cmd", "/c", "chcp 65001 > nul").inheritIO().start().waitFor();
        }
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        if (!loadBook())
            return;
        printBanner();
        boolean do_again = true;
        while (do_again) {
            System.out.println("1. View total word count");
            System.out.println("2: View the # of unique words(non duplicate words)");
            System.out.println("3: View the count of N specific most occuring words");
            System.out.println("4: View the neighbors of a word");
            System.out.println("5. Search similar words");
            System.out.println("6: Search word by page range (Inverted Index)");
            System.out.println("0: Exit");
            System.out.print("What do you wanna do? ");
            System.out.println();

            try {
                int choice = s.nextInt();
                switch (choice) {
                    case 1:
                        showTotalWords();
                        break;
                    case 2:
                        showUniqueWords();
                        break;
                    case 3:
                        showTopWords();
                        break;
                    case 4:
                        showNeighbors();
                        break;
                    case 5:
                        searchSimilar();
                        break;
                    case 6:
                        searchByPageRange();
                        break;
                    case 0:
                        do_again = false;
                        break;
                    default:
                        System.out.println("Invalid option, choose 0-6.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number.");
                s.nextLine(); // clear the bad input so the loop doesn't repeat forever
            }
        }
        s.close();
    }

    public static void printBanner() {
        System.out.println("========================================================================");
        System.out.println("                      Book Text Analytics Engine                        ");
        System.out.println("========================================================================");
    }

    // Loads the book and builds the structures once. Returns false if the file
    // failed.
    public static boolean loadBook() {
        words = BookLoader.readFile(FILE_PATH);
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

    public static void showTotalWords() {
        System.out.println("Total words in the book: " + stats.getTotalWords());
    }

    public static void showUniqueWords() {
        System.out.println("Unique words in the book: " + stats.getUniqueWords());
    }

    public static void showTopWords() {
        System.out.print("How many Top most used words in the book you want? ");
        int n = s.nextInt();
        if (n <= 0) {
            System.out.println("Enter a number greater than 0.");
            return;
        }
        stats.printTopWords(n);
    }

    public static void showNeighbors() {
        System.out.print("Enter a word: ");
        String w = s.next().toLowerCase();
        ArrayList<String> cleaned = BookLoader.tokenize(w);
        if (cleaned.isEmpty()) {
            System.out.println("Wrong input");
            return;
        }
        neighbors.getNeighbors(cleaned.get(0));
    }

    public static void searchSimilar() {
        System.out.print("Enter a prefix: ");
        String p = s.next().toLowerCase();
        ArrayList<String> found = trie.getWordsWithPrefix(p);
        System.out.println(found.size() + " words start with \"" + p + "\":");
        System.out.println(found);
    }

    // Task 4: Inverted Index search.
    // Sub-menu lets the user either search in a page range or see all occurrences.
    public static void searchByPageRange() {
        System.out.println("  a) Search word in a specific page range");
        System.out.println("  b) Show ALL occurrences of a word");
        System.out.print("Choice (a/b): ");
        String choice = s.next().toLowerCase();
        System.out.print("Enter word: ");
        String word = s.next().toLowerCase();
        ArrayList<String> cleaned = BookLoader.tokenize(word);
        if (cleaned.isEmpty()) {
            System.out.println("Invalid input.");
            return;
        }
        String cleanWord = cleaned.get(0);
        if (choice.equals("a")) {
            int totalPages = (int) Math.ceil(21_000.0 / InvertedIndex.LINES_PER_PAGE);
            System.out.print("Start page (1-" + totalPages + "): ");
            int start = s.nextInt();
            System.out.print("End page   (1-" + totalPages + "): ");
            int end = s.nextInt();
            if (start < 1 || end < start) {
                System.out.println("Invalid page range.");
                return;
            }
            invertedIndex.searchInPageRange(cleanWord, start, end);
        } else if (choice.equals("b")) {
            invertedIndex.showAllOccurrences(cleanWord);
        } else {
            System.out.println("Unknown choice.");
        }
    }
}