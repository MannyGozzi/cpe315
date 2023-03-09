import java.util.LinkedList;

public class Cache {
    private int sizeTags, associativity, blockSize, hits, misses, cacheSizeKB;
    private int[][][] cache;
    private LinkedList<Integer>[] LRU; // least recently used
    public Cache(int sizeKB, int blockSize, int associativity) {
        sizeTags = sizeKB * 1024 / 32 / blockSize;
        this.blockSize = blockSize;
        this.associativity = associativity;
        hits = 0;
        misses = 0;
        cacheSizeKB = sizeKB;
        cache = new int[sizeTags][associativity][blockSize];
        LRU = new LinkedList[sizeTags];
        for (int i = 0; i < sizeTags; ++i) {
            LRU[i] = new LinkedList<>();
            LRU[i].add(0);
        }
    }
    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public void read(int address) {
        int tag = address/blockSize % sizeTags;
        int blockOffset = address % blockSize;
        System.out.print("tag: " + tag + "\tblockOffset: " + blockOffset + "\taddress: " + address);
        for (int mru = 0; mru < associativity; mru++) {
            if (cache[tag][mru][blockOffset] == address) {
                ++hits;
                System.out.println("\tHIT");
                accessCache(tag, mru);
            } else {
                System.out.println("\tMISS");
            }
        }
        // otherwise we missed
        loadCacheBlock(tag, address);
        ++misses;
    }

    /* updates the LRU for the specified tag by sending the most recently used */
    private void accessCache(int tag, int mru) {
        if (!LRU[tag].contains(mru)) {
            if (LRU[tag].size() == associativity) {
                LRU[tag].removeFirst();
            }
        } else {
            LRU[tag].remove(mru);
        }
        LRU[tag].addLast(mru);
    }

    /* returns the LRU location for the specified tag */
    private int getLRU(int tag) {
        return LRU[tag].getFirst();
    }
    /* Loads the block of memory for the specified address utilizing the correct tag, LRU location with n address defined by the block size */
    private void loadCacheBlock(int tag, int address) {
        int startAddress = address - address%blockSize;
        int LRU = getLRU(tag);
        for (int i = 0; i < blockSize; i++) {
            cache[tag][LRU][i] = startAddress + i;
        }
    }

    public int getCacheSizeKB() {
        return cacheSizeKB;
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public int getAssociativity() {
        return associativity;
    }

    public int getBlockSize() {
        return blockSize;
    }
}
