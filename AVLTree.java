// Task 4

public class AVLTree {
    private String key;
    private Object value; // the data mapped to this key (a LineLinkedList)
    private int height;
    private AVLTree left, right;

    // Sentinel empty tree. Every "null pointer" is replaced with NIL.
    // NIL.height = -1 --> leaf height = 1 + max(-1,-1) = 0
    public static final AVLTree NIL = new AVLTree();

    // Public constructor: creates a new leaf node.
    public AVLTree(String key, Object value) {
        this.key = key;
        this.value = value;
        left = right = NIL;
    }

    // Inserts or updates a key-value pair.
    // Returns the root of the subtree (may differ after rebalancing).
    // Time: O(log n)
    public AVLTree grow(String k, Object v) {
        if (this == NIL)
            return new AVLTree(k, v);
        int cmp = k.compareTo(this.key); // String comparison instead of < / >
        if (cmp == 0) {
            this.value = v;
            return this;
        } // key exists -> update value
        if (cmp < 0)
            left = left.grow(k, v);
        else
            right = right.grow(k, v);
        rebalance();
        height = 1 + Math.max(left.height, right.height);
        return this;
    }

    // Returns the value for the given key, or null if not found.
    // Time: O(log n)
    public Object get(String k) {
        if (this == NIL)
            return null;
        int cmp = k.compareTo(this.key);
        if (cmp == 0)
            return value;
        if (cmp < 0)
            return left.get(k);
        return right.get(k);
    }

    // ---- private helpers ----

    private AVLTree() { // constructs the NIL sentinel
        left = right = this;
        height = -1;
    }

    // Used by rotations to build the displaced child node.
    // Carries both key AND value down so the map stays correct.
    private AVLTree(String key, Object value, AVLTree left, AVLTree right) {
        this.key = key;
        this.value = value;
        this.left = left;
        this.right = right;
        height = 1 + Math.max(left.height, right.height);
    }

    private void rebalance() {
        if (right.height > left.height + 1) {
            if (right.left.height > right.right.height)
                right.rotateRight(); // Right-Left
            rotateLeft(); // Right-Right
        } else if (left.height > right.height + 1) {
            if (left.right.height > left.left.height)
                left.rotateLeft(); // Left-Right
            rotateRight(); // Left-Left
        }
    }

    // In-place left rotation.
    // Pulls right child's key+value up into this node, pushes this node's key+value
    // down-left.
    private void rotateLeft() {
        left = new AVLTree(key, value, left, right.left);
        key = right.key;
        value = right.value; // <-- must swap value too, not just key
        right = right.right;
    }

    // In-place right rotation.
    // Pulls left child's key+value up into this node, pushes this node's key+value
    // down-right.
    private void rotateRight() {
        right = new AVLTree(key, value, left.right, right);
        key = left.key;
        value = left.value; // <-- must swap value too, not just key
        left = left.left;
    }
}
