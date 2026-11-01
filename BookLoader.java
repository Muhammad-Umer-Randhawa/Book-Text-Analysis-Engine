import java.io.*;
import java.util.ArrayList;

// Loads the book from a file and cleans it into a list of lowercase words.
public class BookLoader {
    private static boolean isWordChar(char c) {
        if (Character.isLetterOrDigit(c)) {
            return true;
        }
        if (c == 39 || c == 45 || c == 8217) {
            return true;
        }
        return false;
    }

    public static ArrayList<String> tokenize(String line) {
        ArrayList<String> words = new ArrayList<>(); // permanent box
        StringBuilder temp = new StringBuilder(); // temporary box
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isWordChar(c)) {
                if (c == 8217)
                    c = '\''; // curly apostrophe -> straight apostrophe
                temp.append(c);
            } else {
                if (temp.length() > 0) {
                    words.add(temp.toString().toLowerCase());
                    temp.setLength(0);
                }
            }
        }
        if (!temp.isEmpty()) {
            words.add(temp.toString().toLowerCase());
        }
        return words;
    }

    public static ArrayList<String> readFile(String filePath) {
        ArrayList<String> words_tokenized = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words_tokenized.addAll(tokenize(line));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
        return words_tokenized;
    }
}