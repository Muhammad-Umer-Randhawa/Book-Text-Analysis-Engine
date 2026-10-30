import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

// Main only shows the menu, reads the input, and calls the class that does the work.
//   BookLoader    -> reads and cleans the book
//   WordStats     -> Task 1 (word counts, top N)
//   NeighborIndex -> Task 2 (neighboring words)
public class Main {
    static Scanner s = new Scanner(System.in);
    static ArrayList<String> words;
    static WordStats stats;
    static NeighborIndex neighbors;

    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        if (!loadBook()) return;

        boolean do_again = true;
        while (do_again) {
            System.out.println("================================================");
            System.out.println("         Book Text Analytics Engine");
            System.out.println("================================================");
            System.out.println("1. View total word count");
            System.out.println("2: View the # of unique words(non duplicate words)");
            System.out.println("3: View the count of N specific most occuring words");
            System.out.println("4: View the neighbors of a word");
            System.out.println("0: Exit");
            System.out.println("What do you wanna do? ");

            try {
                int choice = s.nextInt();
                switch (choice) {
                    case 1: showTotalWords();   break;
                    case 2: showUniqueWords();  break;
                    case 3: showTopWords();     break;
                    case 4: showNeighbors();    break;
                    case 0: do_again = false;   break;
                    default: System.out.println("Invalid option, choose 0-4."); break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter a valid number.");
                s.nextLine(); // clear the bad input so the loop doesn't repeat forever
            }
        }
        s.close();
    }

    // Loads the book and builds the structures once. Returns false if the file failed.
    public static boolean loadBook() {
        String filePath = "D:\\DSA in JAVA 3rd SEM\\Project\\Book Text Analytics Engine\\Great_Expectations.txt";
        words = BookLoader.readFile(filePath);
        if (words.isEmpty()) {
            return false;
        }
        stats = new WordStats(words);
        neighbors = new NeighborIndex(words);
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
        if(cleaned.isEmpty()){
            System.out.println("Wrong input");
            return;
        }
        neighbors.getNeighbors(cleaned.get(0)); 
    }
}