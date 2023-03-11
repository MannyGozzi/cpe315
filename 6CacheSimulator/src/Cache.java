import java.util.LinkedList;

public class Cache {
    private final int numBits = 32;
    private int sizeTags, associativity, blockSize, hits, misses, sizeBytes, numBitsTag, lineSize;
    private int[][][] cache;
    private final int wordSize = 4;
    private final int offsetByte = (int) Math.floor(Math.log(wordSize)/Math.log(2)); // 2^2 = 4 bytes
    private int offsetBlock; // 2^5 = 32 bytes
    private int numBitsIndex;
    private LinkedList<Integer>[] LRU; // least recently used

    public Cache(int sizeKB, int blockSize, int associativity) {
        this.blockSize = blockSize;
        this.associativity = associativity;
        this.sizeBytes = sizeKB * 1024;
        offsetBlock = (int) (Math.log(blockSize)/Math.log(2.0));
        numBitsIndex = (int) (Math.log((double) sizeBytes / (double) wordSize / (double) blockSize) / Math.log(2.0));
        numBitsTag = numBits - numBitsIndex - offsetBlock - offsetByte;
        sizeTags = (int) Math.pow(2, numBitsTag);
        cache = new int[sizeTags][associativity][blockSize*wordSize];
        LRU = new LinkedList[sizeTags];
        lineSize = blockSize*wordSize;
        for (int i = 0; i < sizeTags; ++i) {
            LRU[i] = new LinkedList<>();
            LRU[i].add(0);
        }
        System.out.println("offsetBlock:\t" + offsetBlock + "\toffsetByte: " + offsetByte + "\toffsetTag: " + numBitsTag + "\tsizeTags " + sizeTags);
    }

    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public void read(int address) {
        int byteOffset = getByteOffset(address);
        int blockOffset = getBlockOffset(address);
        int tag = getTag(address);

        //System.out.print("tag: " + tag + "\tblockOffset: " + blockOffset + "\taddress: " + address);
        for (int mru = 0; mru < associativity; mru++) {
            try {

                if (cache[tag][mru][blockOffset * wordSize + byteOffset] == address) {
                    ++hits;
                    loadCacheBlock(tag, address);
                    accessCache(tag, mru);
                } else {
                    // otherwise we missed
                    loadCacheBlock(tag, address);
                    accessCache(tag, getLRU(tag));
                    ++misses;
                }
            } catch (Exception e) {
                System.out.println(tag + " " + mru + " " + blockOffset*wordSize + byteOffset);
                e.printStackTrace();
            }
        }
    }

    private int getByteOffset(int address) {
        return address << (numBits - offsetByte) >>> (numBits - offsetByte);
    }

    private int getBlockOffset(int address) {
        return address << (numBits - offsetBlock - offsetByte) >>> (numBits - offsetByte);
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
        int startAddress = address - address%lineSize;
        int LRU = getLRU(tag);
        for (int i = 0; i < lineSize; i++) {
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

    public int getBlockSize() {
        return blockSize;
    }
}
