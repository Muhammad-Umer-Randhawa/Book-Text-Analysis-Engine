import java.io.*;
import java.util.ArrayList;

// Task 4: Structural Indexing & Line/Page Range Retrieval.
//
// Data structures used:
//   AVL Tree  : maps each unique word (String) to its LineLinkedList.
//               Lookup and insert are both O(log u), u = unique words.
//   LineLinkedList : sorted singly linked list of every line the word appears on.
//               Range query stops early because the list is sorted.
//
// Building the index: one sequential pass through the file   O(n log u)
// Searching by page range:                                   O(log u + k)
//   k = number of lines up to the end of the requested range
public class InvertedIndex {

    // 45 lines per page is a common estimate for a standard novel.
    // Great Expectations has ~21 000 lines -> ~467 pages.
    public static final int LINES_PER_PAGE = 45;

    private AVLTree root; // root of the AVL tree (updated after every insert)
    private int totalLines;

    // Reads the .txt file once and builds the full index.
    public InvertedIndex(String filePath) {
        root = AVLTree.NIL;
        buildIndex(filePath);
    }

    // ---- public interface ----

    // Checks whether a word appears anywhere in pages [startPage, endPage].
    // Converts page numbers to line numbers, then queries the linked list.
    // This is the method required by the task spec.
    public void searchInPageRange(String word, int startPage, int endPage) {
        int startLine = (startPage - 1) * LINES_PER_PAGE + 1;
        int endLine = endPage * LINES_PER_PAGE;

        Object found = root.get(word); // O(log u)
        if (found == null) {
            System.out.println("\"" + word + "\" does not appear in the book at all.");
            return;
        }

        LineLinkedList list = (LineLinkedList) found;
        ArrayList<Integer> lines = list.getLinesInRange(startLine, endLine); // O(k)

        if (lines.isEmpty()) {
            System.out.println("\"" + word + "\" does NOT appear in pages "
                    + startPage + " to " + endPage + ".");
        } else {
            System.out.println("\"" + word + "\" appears " + lines.size()
                    + " time(s) in pages " + startPage + " to " + endPage + ":");
            for (int ln : lines) {
                int page = (ln - 1) / LINES_PER_PAGE + 1;
                System.out.println("  Line " + ln + "  (Page ~" + page + ")");
            }
        }
    }

    // Bonus: shows every line (and page number) where the word appears in the full
    // book.
    public void showAllOccurrences(String word) {
        Object found = root.get(word);
        if (found == null) {
            System.out.println("\"" + word + "\" does not appear in the book.");
            return;
        }
        LineLinkedList list = (LineLinkedList) found;
        System.out.println("\"" + word + "\" appears on " + list.size() + " unique line(s):");
        list.printAll(LINES_PER_PAGE);
    }

    public int getTotalPages() {
        return (totalLines + LINES_PER_PAGE - 1) / LINES_PER_PAGE;
    }

    // Silent boolean version of searchInPageRange — used by Task 5 benchmarking
    // so the timed loop does not print 1000 lines to the console.
    // Time: average O(1) for the map lookup, plus a short scan over the matching lines.
    public boolean hasWordInPageRange(String word, int startPage, int endPage) {
        if (startPage < 1 || endPage < startPage) {
            return false;
        }
        Object result = root.get(word);
        if (result == null) {
            return false;
        }
        int startLine = (startPage - 1) * LINES_PER_PAGE + 1;
        int endLine = endPage * LINES_PER_PAGE;
        return ((LineLinkedList) result).hasLineInRange(startLine, endLine);
    }

    // ---- private helpers ----

    private void buildIndex(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                ArrayList<String> tokens = BookLoader.tokenize(line);
                for (String word : tokens) {
                    Object existing = root.get(word);
                    LineLinkedList list;
                    if (existing == null) {
                        list = new LineLinkedList();
                        root = root.grow(word, list);
                    } else {
                        list = (LineLinkedList) existing;
                    }
                    list.addLine(lineNumber);
                }
            }
            totalLines = lineNumber;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file for index.");
        }
    }
}
