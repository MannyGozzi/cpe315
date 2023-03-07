public class CacheDirect {
    private int sizeTags;
    private int sizeWordBlock;
    private int[] cache;
    public CacheDirect(int sizeKB, int wordBlock) {
        sizeTags = sizeKB * 1024 / wordBlock;
        sizeWordBlock = wordBlock;
        cache = new int[sizeTags];
    }
    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public boolean read(int address) {
        int tag = address / sizeWordBlock;
        if (cache[tag] == 0) {
            cache[tag] = 1;
            return false;
        }
        return true;
    }

    public void printCache() {
        for (int i = 0; i < sizeTags; i++) {
            System.out.println("Tag " + i + ": " + cache[i]);
        }
    }


}
