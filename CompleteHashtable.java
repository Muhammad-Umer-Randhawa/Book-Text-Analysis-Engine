// this one uses open addressing (linear probing) to resolve collisions.
import java.util.ArrayList;

public class CompleteHashtable {
    private class Entry{
      Object key;
      Object value;
      Entry(Object k, Object v){
        key = k;
        value = v;
      }
    }
    private int hash(Object key){
        if(key == null) throw new IllegalArgumentException();
        return (key.hashCode() & 0x7FFFFFFF) % entries.length;
    }
    private int nextProbe(int h, int i){
        return (h + i) % entries.length;
    }
    private void rehash(){
        Entry[] oldEntries = entries;
        entries = new Entry[2 * oldEntries.length + 1];
        for(int k=0; k<oldEntries.length; k++){
            Entry entry = oldEntries[k];
            if(entry == null || entry == NIL) continue;
            int h = hash(entry.key);
            for(int i=0; i<entries.length; i++){
                int j = nextProbe(h, i);
                if(entries[j] == null){
                    entries[j] = entry;
                    break;
                }
            }
        }
        used = size;
    }
    private Entry[] entries;
    private int size, used;
    private float loadFactor;
    private final Entry NIL = new Entry(null, null);

    public CompleteHashtable(int capacity, float loadFactor){
      entries = new Entry[capacity];
      this.loadFactor = loadFactor;
    }
    public CompleteHashtable(int capacity){
        this(capacity, 0.75f);
    }
    public CompleteHashtable(){
        this(101);
    }
    public Object get(Object key){
        int h = hash(key);
        for(int i=0; i<entries.length; i++){
            int j = nextProbe(h, i);
            Entry entry = entries[j];
            if(entry == null) return null;
            if(entry == NIL) continue;
            if(entry.key.equals(key)) return entry.value;
        }
        return null;
    }
    public Object put(Object key, Object value){
        if(used > loadFactor * entries.length) rehash();
        int h = hash(key);
        for(int i = 0; i< entries.length; i++){
            int j = nextProbe(h, i);
            Entry entry = entries[j];
            if(entry == null){
                entries[j] = new Entry(key, value);
                ++size;
                ++used;
                return null;
            }
            if(entry == NIL) continue;
            if(entry.key.equals(key)){
                Object oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
        }
        return null;
    }
    public Object remove(Object key){
        int h = hash(key);
        for(int i=0; i<entries.length; i++){
            int j = nextProbe(h, i);
            Entry entry = entries[j];
            if(entry == null) break;
            if(entry == NIL) continue;
            if(entry.key.equals(key)){
                Object oldValue = entry.value;
                entries[j] = NIL;
                --size;
                return oldValue;
            }
        }
        return null;
    }
    public int size(){
        return size;
    }
    public void printAll(){
        for(int i=0; i<entries.length; i++){
            if(entries[i] == null || entries[i] == NIL) continue;
            System.out.println(entries[i].key + " " + entries[i].value);
        }
    }
    // Returns a list of all the keys stored in the table.
    // Time: O(capacity) -> it looks at every slot of the array, including the empty ones
    public ArrayList<String> getKeys() {

        // An empty list that will collect the keys we find.
        ArrayList<String> keys = new ArrayList<>();

        // Visit every slot of the array, one by one. i is the slot number.
        for (int i = 0; i < entries.length; i++) {

            // Empty slot (never used) or removed slot (NIL): nothing to collect, go to the next slot.
            if (entries[i] == null || entries[i] == NIL) continue;

            // A real entry: take its key and add it to the list.
            // (The cast is needed because keys are stored as plain Objects.)
            keys.add((String) entries[i].key);
        }

        // All slots visited, so give back the list.
        return keys;
    }
}