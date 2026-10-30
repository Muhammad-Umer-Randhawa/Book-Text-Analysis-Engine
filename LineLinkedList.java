import java.util.ArrayList;

// Task 4: Singly linked list that stores the line numbers where a word appears.
public class LineLinkedList {

    private static class Node {
        int val; // line number
        Node next;

        Node(int val) {
            this.val = val;
        }
    }

    Node head;
    Node tail;
    int size;

    // Appends a line number — mirrors addAtTail, but skips the line
    // if it is the same as the previous one (a word is counted once per line).
    // Time: O(1)
    public void addLine(int line) {
        if (tail != null && tail.val == line)
            return; // skip duplicate
        Node temp = new Node(line);
        if (tail == null) { // list is empty
            head = tail = temp;
        } else {
            tail.next = temp;
            tail = temp;
        }
        size++;
    }

    // Returns every line number in [startLine, endLine].
    // Stops early once we pass endLine because the list is sorted.
    // Time: O(k) k = nodes up to and including endLine
    public ArrayList<Integer> getLinesInRange(int startLine, int endLine) {
        ArrayList<Integer> result = new ArrayList<>();
        Node temp = head;
        while (temp != null) {
            if (temp.val > endLine)
                break; // sorted -> nothing further matches
            if (temp.val >= startLine)
                result.add(temp.val);
            temp = temp.next;
        }
        return result;
    }

    // Returns true the moment it finds one line inside [startLine, endLine].
    // Time: O(k)
    public boolean hasLineInRange(int startLine, int endLine) {
        Node temp = head;
        while (temp != null) {
            if (temp.val > endLine)
                return false;
            if (temp.val >= startLine)
                return true;
            temp = temp.next;
        }
        return false;
    }

    // Prints every line number with its estimated page number, 4 entries per row.
    // Time: O(n)
    public void printAll(int linesPerPage) {
        Node temp = head;
        int count = 0;
        while (temp != null) {
            int page = (temp.val - 1) / linesPerPage + 1;
            System.out.printf("  Line %-6d(Page %3d)", temp.val, page);
            count++;
            if (count % 4 == 0)
                System.out.println();
            else
                System.out.print("  |");
            temp = temp.next;
        }
        if (count % 4 != 0)
            System.out.println();
    }

    public int size() {
        return size;
    }
}
