import java.util.LinkedList;

public class Cache {
    private int sizeTags, associativity, blockSize;
    private int[][][] cache;
    private int[] LRU; // least recently used
    public Cache(int sizeKB, int blockSize, int associativity) {
        sizeTags = sizeKB * 1024 / blockSize;
        this.blockSize = blockSize;
        this.associativity = associativity;
        cache = new int[sizeTags][blockSize][associativity];
        LRU = new int[sizeTags];
        for (int i = 0; i < sizeTags; ++i) {
            LRU[i] = new LinkedList<>();
        }
    }
    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public boolean read(int address) {
        int tag = address/blockSize % sizeTags;
        int blockOffset = address % blockSize;
        for (int i = 0; i < associativity; i++) {
            if (cache[tag][blockOffset][i] == address) {
                return true;
            }
        } else {
            loadCacheBlock(address);
            return false
        }
    }

    private int getLRU() {

    }
    private loadCacheBlock(int address) {

    }
}
