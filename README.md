# Book Text Analytics Engine

A Java-based command-line text analytics project for analyzing a book text file using core data structures and indexing techniques. The program loads a text file, tokenizes words, and exposes several search and statistics features through a menu-driven interface.

## Features

- Total word count and unique word count
- Most frequent words (top N)
- Neighbor analysis for words that appear before or after a target word
- Prefix-based search using a trie
- Page/line range lookup for a word using an inverted index
- Benchmarking for comparison between slower and optimized approaches

## Project Structure

- `Main.java` — main menu and user interaction
- `BookLoader.java` — text loading and tokenization
- `WordStats.java` — frequency statistics and top-word reporting
- `NeighborIndex.java` — adjacent word relationships
- `Trie.java` — prefix-search structure
- `InvertedIndex.java` — page/line range search index
- `AVLTree.java` — AVL tree used by the inverted index
- `LineLinkedList.java` — sorted line list for word occurrences
- `CompleteHashtable.java` — custom hash table implementation
- `Benchmarker.java` — performance benchmark runner
- `Great_Expectations.txt` — sample book dataset

## How to Run

1. Open a terminal in the project folder.
2. Compile the Java files:

```bash
javac *.java
```

3. Run the program:

```bash
java Main
```

## Menu Options

The program provides these choices:

1. View total word count
2. View the number of unique words
3. View the N most common words
4. View neighbors of a word
5. Search similar words by prefix
6. Search word occurrences by page range
0. Exit

## Notes

- The project currently uses a hardcoded file path in `Main.java`:

```java
static final String FILE_PATH = "D:\\DSA in JAVA 3rd SEM\\Project\\Book Text Analytics Engine\\Great_Expectations.txt";
```

- If your project folder is stored somewhere else, update that path to match your local machine.
- The `Benchmarker` class can be used to test performance and compare indexed vs non-indexed solutions:

```bash
java Benchmarker [prefix] [word] [startPage] [endPage]
```

Example:

```bash
java Benchmarker mis miss 1 50
```

## Purpose

This project demonstrates practical applications of data structures in text analysis, including hashing, tries, AVL trees, and inverted indexes for efficient word lookup and retrieval.
