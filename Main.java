import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        try(Scanner sc = new Scanner(System.in)){
            System.out.print("How many Top most used words in the book you want? ");
            int n = sc.nextInt();
            if (n <= 0) {
                System.out.println("Enter a number greater than 0.");
                return;
            }
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            String filePath = "D:\\DSA in JAVA 3rd SEM\\Project\\Book Text Analytics Engine\\Great_Expectations.txt";
            ArrayList<String> words = readFile(filePath);
            analyze(words, n);
        }
        catch(InputMismatchException e){
            System.out.println("Enter a valid input (The max unique words are 11491)");
        }
    }
    private static boolean isWordChar(char c) {
        if(Character.isLetterOrDigit(c)){
            return true;
        }
        if(c == 39 || c == 45 || c == 8217){
            return true;
        }
        return false;
    }
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
    public static void analyze(ArrayList<String> words, int n){
        System.out.println("Total words in the book: " + words.size());
        CompleteHashtable table = new CompleteHashtable(words.size());
        for (String w : words) {
            Object a = table.get(w);
            if(a == null){
                table.put(w, 1);
            }
            else{
                table.put(w, (Integer)a + 1);
            }
        }
        System.out.println("Unique words in the book: " + table.size());
        ArrayList<String> picked = new ArrayList<>();
        ArrayList<String> keys = table.getKeys();
        if (n > keys.size()) n = keys.size();
        for(int i=0; i<n; i++){
            String bestWord = null;
            int bestCount = 0;
            for (String w : keys) {
                if(picked.contains(w)) continue;
                Object a = table.get(w);
                int count = (Integer)a;
                if(count > bestCount){
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
}