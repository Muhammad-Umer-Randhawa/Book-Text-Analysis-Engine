import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    // Shared state, so every method can use it
    static Scanner s = new Scanner(System.in);
    static ArrayList<String> words;
    static CompleteHashtable table;
    static CompleteHashtable nextTable;   // word -> (following word -> count)
    static CompleteHashtable prevTable;   // word -> (preceding word -> count)

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
            System.out.println("4. View the neighbors of a word");
            System.out.println("0: Exit");
            System.out.println("What do you wanna do? ");

            try {
                int choice = s.nextInt();
                switch (choice) {
                    case 1: showTotalWords();   break;
                    case 2: showUniqueWords();  break;
                    case 3: showTopNWords();    break;
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

    // Reads the file and builds the frequency table once. Returns false if the file failed.
    // O(n)
    public static boolean loadBook() {
        String filePath = "D:\\DSA in JAVA 3rd SEM\\Project\\Book Text Analytics Engine\\Great_Expectations.txt";
        words = readFile(filePath);
        if (words.isEmpty()) {
            return false;
        }
        table = new CompleteHashtable(words.size());
        for (String w : words) {
            Object a = table.get(w);
            if (a == null) {
                table.put(w, 1);
            } else {
                table.put(w, (Integer) a + 1);
            }
        }
        buildNeighbors();
        return true;
    }
    
    // O(1)
    public static void showTotalWords() {
        System.out.println("Total words in the book: " + words.size());
    }

    // O(1)
    public static void showUniqueWords() {
        System.out.println("Unique words in the book: " + table.size());
    }

    // O(n^2)
    public static void showTopNWords() {
        System.out.print("How many Top most used words in the book you want? ");
        int n = s.nextInt();
        if (n <= 0) {
            System.out.println("Enter a number greater than 0.");
            return;
        }
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
            System.out.println("Word: " + bestWord);
            System.out.println("It's count: " + bestCount);
            System.out.println();
        }
    }
    // ===================================== TASK 1 ===============================================
    public static boolean isWordChar(char c) {
        if(Character.isLetterOrDigit(c)){
            return true;
        }
        if(c == 39 || c == 45 || c == 8217){
            return true;
        }
        return false;
    }

    //O(n)
    public static ArrayList<String> tokenize(String line) {
        ArrayList<String> words = new ArrayList<>();   // permanent box
        StringBuilder temp = new StringBuilder();      // temporary box
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isWordChar(c)) {
                temp.append(c);
            }
            else {
                if(temp.length() > 0){
                    words.add(temp.toString().toLowerCase());
                    temp.setLength(0);
                }
            }
        }
        if(!temp.isEmpty()){
            words.add(temp.toString().toLowerCase());
        }
        return words;
    }

    // O(n)
    public static ArrayList<String> readFile(String filePath) {
        ArrayList<String> words_tokenized = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words_tokenized.addAll(tokenize(line));
            }
        }
        catch(FileNotFoundException e){
            System.out.println("File not found: " + filePath);
        }
        catch(IOException e){
            System.out.println("Something went wrong");
        }
        return words_tokenized;
    }
    // ============================== TASK 2 =========================================
    // Time:  O(n) 
    // Space: O(n) 
    public static void buildNeighbors() {
        nextTable = new CompleteHashtable(words.size());
        prevTable = new CompleteHashtable(words.size());
        for (int i = 0; i < words.size() - 1; i++) {
            String current = words.get(i);
            String next = words.get(i + 1);
            addPair(nextTable, current, next);   // next comes after current
            addPair(prevTable, next, current);   // current comes before next
        }
    }

    // Time:  O(d) average, d = number of neighbors of the word; O(m) worst case
    // Space: O(1) extra
    public static void getNeighbors(String word) {
        if (table.get(word) == null) {
            System.out.println("\"" + word + "\" does not appear in the book.");
            return;
        }
        printNeighborList("Words that come AFTER \"" + word + "\":", nextTable, word);
        printNeighborList("Words that come BEFORE \"" + word + "\":", prevTable, word);
    }

    // ===================== HELPER METHODS =====================

    // HELPER: adds 1 to the count of "neighbor" inside the inner table of "word"
    // Time:  O(1) 
    // Space: O(1)
    public static void addPair(CompleteHashtable t, String word, String neighbor) {
        CompleteHashtable neighbors;
        Object inner = t.get(word);
        if (inner == null) {
            neighbors = new CompleteHashtable(16);
            t.put(word, neighbors);
        } else {
            neighbors = (CompleteHashtable) inner;
        }
        Object c = neighbors.get(neighbor);
        if (c == null) {
            neighbors.put(neighbor, 1);
        } else {
            neighbors.put(neighbor, (Integer) c + 1);
        }
    }

    // HELPER: prints one neighbor list with counts
    // Time:  O(d) average (getKeys scans the inner table, whose size is proportional to d)
    // Space: O(d) for the keys list
    public static void printNeighborList(String title, CompleteHashtable t, String word) {
        System.out.println(title);
        Object inner = t.get(word);
        if (inner == null) {
            System.out.println("  (none)");
            return;
        }
        CompleteHashtable neighbors = (CompleteHashtable) inner;
        for (String n : neighbors.getKeys()) {
            System.out.println("  " + n + " -> " + neighbors.get(n));
        }
    }

    // HELPER: menu wrapper, only reads the word and calls getNeighbors
    // Time: O(1) plus the cost of getNeighbors
    public static void showNeighbors() {
        System.out.print("Enter a word: ");
        String w = s.next().toLowerCase();
        getNeighbors(w);
    }
}