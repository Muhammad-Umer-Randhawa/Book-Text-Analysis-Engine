import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

// Task 5: Benchmarks build time and memory for each task, then
// compares fast (Trie / InvertedIndex) vs slow (linear scan / raw scan).
// Run: java Task5 [prefix] [word] [startPage] [endPage]
public class Benchmarker {

    static ArrayList<String> words;
    static ArrayList<String> lines;
    static WordStats stats;
    static NeighborIndex neighbors;
    static Trie trie;
    static InvertedIndex invertedIndex;
    static Set<String> exactWordSet;
    static Map<String, Set<Integer>> pageIndex;

    static long memory() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory() - r.freeMemory();
    }

    // Call BEFORE the work: saves start time and memory.
    // Call report() AFTER the work to print the result.
    static long[] startMeasure() {
        return new long[] { System.nanoTime(), memory() };
    }

    static void report(String name, long[] before) {
        double ms = (System.nanoTime() - before[0]) / 1_000_000.0;
        double kib = (memory() - before[1]) / 1024.0;
        System.out.printf("%-32s %.2f ms,  heap change %.2f KiB%n", name + ":", ms, kib);
    }

    // Brute-force prefix search: scan every unique word for startsWith.
    static String linearPrefix(String prefix) {
        StringBuilder sb = new StringBuilder();
        for (String w : stats.getUniqueWordList())
            if (w.startsWith(prefix))
                sb.append(w).append(' ');
        return sb.toString();
    }

    // Faster page-range scan using a page map instead of walking the full token list.
    static boolean rawScan(String word, int startPage, int endPage) {
       if (startPage < 1 || endPage < startPage) {
           return false;
       }
       int startLine = (startPage - 1) * InvertedIndex.LINES_PER_PAGE + 1;
       int endLine = Math.min(endPage * InvertedIndex.LINES_PER_PAGE, lines.size());
       for (int ln = startLine; ln <= endLine; ln++) {
           for (String t : BookLoader.tokenize(lines.get(ln - 1))) {
               if (t.equals(word)) {
                   return true;
               }
           }
       }
       return false;
   }

    public static void main(String[] args) throws IOException {
        String prefix = args.length > 0 ? args[0] : "mis";
        String word = args.length > 1 ? args[1] : "miss";
        int start = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        int end = args.length > 3 ? Integer.parseInt(args[3]) : 50;

        System.out.println("=== Task 5: Benchmarks ===");
        System.out.println("Prefix: " + prefix + "  |  Word: " + word + "  |  Pages: " + start + "-" + end);
        System.out.println();

        // ---- BUILD ----
        long[] m;

        lines = new ArrayList<>(java.nio.file.Files.readAllLines(java.nio.file.Path.of(Main.FILE_PATH)));
        m = startMeasure();
        words = BookLoader.readFile(Main.FILE_PATH);
        stats = new WordStats(words);
        report("Task 1 load + word stats", m);

        m = startMeasure();
        neighbors = new NeighborIndex(words);
        report("Task 2 neighbor build", m);

        m = startMeasure();
        trie = new Trie();
        for (String w : stats.getUniqueWordList())
            trie.insert(w);
        report("Task 3 Trie build", m);

        m = startMeasure();
        invertedIndex = new InvertedIndex(Main.FILE_PATH);
        report("Task 4 index build", m);

        System.out.println();

        // ---- QUERIES (1000 each) ----
        for (int i = 0; i < 100; i++) {
            trie.getWordsWithPrefix(prefix);
            linearPrefix(prefix);
            invertedIndex.hasWordInPageRange(word, start, end);
            rawScan(word, start, end);
        }

        m = startMeasure();
        for (int i = 0; i < 1000; i++)
            trie.getWordsWithPrefix(prefix);
        report("Trie prefix search   x1000", m);

        m = startMeasure();
        for (int i = 0; i < 1000; i++)
            linearPrefix(prefix);
        report("Linear prefix scan   x1000", m);

        m = startMeasure();
        for (int i = 0; i < 1000; i++)
            invertedIndex.hasWordInPageRange(word, start, end);
        report("Index range lookup   x1000", m);

        m = startMeasure();
        for (int i = 0; i < 1000; i++)
            rawScan(word, start, end);
        report("Raw token scan       x1000", m);
    }
}