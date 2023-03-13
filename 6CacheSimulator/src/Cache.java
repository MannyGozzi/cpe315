import java.util.LinkedList;

public class Cache {
    private final int numBits = 32;
    private final int wordSize = 4;
    private final int numBitsByte = 2; // byteOffset 2^2 = 4 bytes
    private int numBitsTag, sizeBytes, sizeBlock, associativity, indicies, hits, misses;
    private int[][] cache;
    private int numBitsBlock; // blockOffset 2^5 = 32 bytes
    private int numBitsIndex;
    private LinkedList<Integer>[] LRU; // least recently used

    public Cache(int sizeKB, int blockSize, int associativity) {
        this.sizeBlock = blockSize;
        this.associativity = associativity;
        this.sizeBytes = sizeKB * 1024;
        numBitsBlock = (int) (Math.log(blockSize)/Math.log(2));
        int lineSize = blockSize * wordSize * associativity;
        numBitsIndex = (int) (Math.log((double)sizeBytes / (double)lineSize) / Math.log(2));
        indicies = (int) Math.pow(2, numBitsIndex);
        numBitsTag = numBits - numBitsIndex - numBitsBlock - numBitsByte;
        cache = new int[indicies][associativity];
        LRU = new LinkedList[indicies];
        for (int i = 0; i < indicies; ++i) {
            LRU[i] = new LinkedList<>();
            for (int j = 0; j < associativity; ++j) {
                LRU[i].add(j);
            }
            for (int associate = 0; associate < associativity; ++ associate) {
                cache[i][associate] = -1;
            }
        }
    }

    /* Will read the address and update hit and miss values in the cache Object */
    public void read(int address) {

        int tag = getTag(address);
        int index = getIndex(address);
        for (int mru = 0; mru < associativity; ++mru) {
            if (cache[index][mru] == tag) {
                ++hits;
                updateLRU(index, mru);
                return;
            }
        }
        // otherwise we missed
        loadCacheBlock(tag, index);
        updateLRU(index, getLRU(index));
        ++misses;
    }

    private int getTag(int address) {
        return address >>> (numBits - numBitsTag);
    }

    private int getIndex(int address) {
        return address << numBitsTag >>> (numBits - numBitsIndex);
    }
    /* updates the LRU for the specified tag by sending the most recently used */
    private void updateLRU(int index, int mru) {
        if (LRU[index].contains(mru)) LRU[index].removeFirstOccurrence(mru);
        else LRU[index].removeFirst(); // prob
        LRU[index].addLast(mru);
    }

    /* returns the LRU location for the specified index */
    private int getLRU(int index) {
        return LRU[index].getFirst();
    }
    /* Loads the block of memory for the specified address utilizing the correct tag,
     LRU location with n address defined by the block size */
    private void loadCacheBlock(int tag, int index) {
        int lruIndex = getLRU(index);
        cache[index][lruIndex] = tag;
    }

    public int getCacheSizeBytes() {
        return this.sizeBytes;
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

    public int getSizeBlock() {
        return sizeBlock;
    }

    @Override
    public String toString() {
        return "Cache size: " + this.getCacheSizeBytes() + "B\t" + "Associativity: " + this.getAssociativity() + "\t" + "Block size: " + this.getSizeBlock()
            + "\nHits: " + this.getHits() + "\t" + String.format("Hit Rate: %.2f%%", (double) this.getHits()/(double) (this.getHits()+this.getMisses()) * 100);
    }
}
