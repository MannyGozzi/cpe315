import java.util.LinkedList;

public class Cache {
    private final int numBits = 32;
    private int numTags, numBitsTag, sizeBytes, sizeBlock, sizeLine, associativity, hits, misses;
    private int[][][] cache;
    private final int wordSize = 4;
    private final int numBitsByte = (int) Math.floor(Math.log(wordSize)/Math.log(2)); // 2^2 = 4 bytes
    private int numBitsBlock; // 2^5 = 32 bytes
    private int numBitsIndex;
    private LinkedList<Integer>[] LRU; // least recently used

    public Cache(int sizeKB, int blockSize, int associativity) {
        this.sizeBlock = blockSize;
        this.associativity = associativity;
        this.sizeBytes = sizeKB * 1024;
        numBitsBlock = (int) (Math.log(blockSize)/Math.log(2.0));
        numBitsIndex = (int) (Math.log((double) sizeBytes / (double) wordSize / (double) blockSize) / Math.log(2.0));
        numBitsTag = numBits - numBitsIndex - numBitsBlock - numBitsByte;
        numTags = (int) Math.pow(2, numBitsTag);
        cache = new int[numTags][associativity][blockSize*wordSize];
        LRU = new LinkedList[numTags];
        sizeLine = blockSize*wordSize;
        for (int i = 0; i < numTags; ++i) {
            LRU[i] = new LinkedList<>();
            LRU[i].add(0);
        }
        System.out.println("offsetBlock:\t" + numBitsBlock + "\toffsetByte: " + numBitsByte + "\toffsetTag: " + numBitsTag + "\tsizeTags " + numTags + "\tlineSize: " + sizeLine);
    }

    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public void read(int address) {
        int byteOffset = getByteOffset(address);
        int blockOffset = getBlockOffset(address);
        int tag = getTag(address);

        for (int mru = 0; mru < associativity; mru++) {
            try {

                if (cache[tag][mru][blockOffset * wordSize + byteOffset] == address) {
                    ++hits;
                    accessCache(tag, mru);
                    return;
                }
            } catch (Exception e) {
                System.out.println(tag + " " + mru + " " + blockOffset*wordSize + byteOffset);
                e.printStackTrace();
            }
        }
        // otherwise we missed
        loadCacheBlock(tag, address);
        accessCache(tag, getLRU(tag));
        ++misses;
    }

    private int getByteOffset(int address) {
        return address << (numBits - numBitsByte) >>> (numBits - numBitsByte);
    }

    private int getBlockOffset(int address) {
        return address << (numBits - numBitsBlock - numBitsByte) >>> (numBits - numBitsByte);
    }

    private int getTag(int address) {
        return address >>> (numBits - numBitsTag);
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
        int startAddress = address - address% sizeLine;
        int LRU = getLRU(tag);
        for (int i = 0; i < sizeLine; i++) {
            cache[tag][LRU][i] = startAddress + i;
        }
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
}
