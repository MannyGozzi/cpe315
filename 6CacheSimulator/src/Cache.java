import java.util.LinkedList;

public class Cache {
    private final int numBits = 32;
    private int numBitsTag, sizeBytes, sizeBlock, sizeLine, associativity, indicies, hits, misses;
    private int[][] cache;
    private final int wordSize = 4;
    private final int numBitsByte = (int) Math.floor(Math.log(wordSize)/Math.log(2)); // byteOffset 2^2 = 4 bytes
    private int numBitsBlock; // blockOffset 2^5 = 32 bytes
    private int numBitsIndex;
    private LinkedList<Integer>[] LRU; // least recently used

    public Cache(int sizeKB, int blockSize, int associativity) {
        this.sizeBlock = blockSize;
        this.associativity = associativity;
        this.sizeBytes = sizeKB * 1024;
        numBitsBlock = (int) (Math.log(blockSize)/Math.log(2.0));
        numBitsIndex = (int) (Math.log((double) sizeBytes / (double) wordSize / (double) blockSize) / Math.log(2.0));
        indicies = (int) Math.pow(2, numBitsIndex);
        numBitsTag = numBits - numBitsIndex - numBitsBlock - numBitsByte;
        sizeLine = blockSize*wordSize;
        cache = new int[indicies][associativity];
        LRU = new LinkedList[indicies];
        for (int i = 0; i < indicies; ++i) {
            LRU[i] = new LinkedList<>();
            LRU[i].add(0);
            for (int associate = 0; associate < associativity; ++ associate) {
                cache[i][associate] = -1;
            }
        }
        // System.out.println("offsetBlock:\t" + numBitsBlock + "\toffsetByte: " + numBitsByte + "\tTagbits: " + numBitsTag + "\tindicies: " + indicies + "\tTag Bits " + numBitsIndex +  "\tlineSize: " + sizeLine);
    }

    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public void read(int address) {
        int tag = getTag(address);
        int index = getIndex(address);
        for (int mru = 0; mru < associativity; mru++) {
            try {
                // System.out.println("looking for address: " + address + " in tag: " + tag + " mru: " + mru + " blockOffset: " + blockOffset + " byteOffset: " + byteOffset);
                if (cache[index][mru] == tag) {
                    ++hits;
                    accessCache(index, mru);
                    return;
                }
            } catch (Exception e) {
                System.out.println("Tag:" + tag + "\tMRU: " + mru);
                e.printStackTrace();
            }
        }
        // otherwise we missed
        loadCacheBlock(tag, index);
        accessCache(index, getLRU(index));
        ++misses;
    }

//    private int getByteOffset(int address) {
//        return address << (numBits - numBitsByte) >>> (numBits - numBitsByte);
//    }
//
//    private int getBlockOffset(int address) {
//        return address << (numBits - numBitsBlock - numBitsByte) >>> (numBits - numBitsByte);
//    }

    private int getTag(int address) {
        return address >>> (numBits - numBitsTag);
    }

    private int getIndex(int address) {
        return address << numBitsTag >>> (numBits - numBitsIndex);
    }
    /* updates the LRU for the specified tag by sending the most recently used */
    private void accessCache(int index, int mru) {
        if (!LRU[index].contains(mru)) {
            if (LRU[index].size() == associativity) {
                LRU[index].removeFirst();
            }
        } else {
            LRU[index].removeFirstOccurrence(mru);
        }
        LRU[index].addLast(mru);
    }

    /* returns the LRU location for the specified tag */
    private int getLRU(int index) {
        return LRU[index].getFirst();
    }
    /* Loads the block of memory for the specified address utilizing the correct tag, LRU location with n address defined by the block size */
    private void loadCacheBlock(int tag, int index) {
        int LRU = getLRU(index);
        cache[index][LRU] = tag;
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
        return "Cache size: " + this.getCacheSizeBytes() + "B\t" + "Associativity: " + this.getAssociativity() + "\t" + "Block Size: " + this.getSizeBlock()
            + "\nHits: " + this.getHits() + "\t" + String.format("Hit Rate: %.2f%%", (double) this.getHits()/(this.getHits()+this.getMisses()) * 100);
    }
}
