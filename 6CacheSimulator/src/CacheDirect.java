public class CacheDirect {
    private int sizeTags;
    private int sizeWordBlock;
    private int[] cache;
    private int[] tags;
    public CacheDirect(int sizeKB, int wordBlock) {
        sizeTags = sizeKB * 1024 / 32 / wordBlock;
        sizeWordBlock = wordBlock;
        cache = new int[sizeTags];
        tags = new int[sizeTags];

        for (int i = 0; i < sizeTags; i++) {
            cache[i] = -1;
            tags[i] = -1;
        }
    }
    /* returns true if the address was a hit, false otherwise
    * Will automatically add the corresponding values to the cache */
    public boolean read(int address) {
        int tag = address % sizeTags;
        int targetTag = address / sizeTags;
        if (tags[tag] != targetTag || cache[tag] == 0) {
            cache[tag] = 1;
            tags[tag] = targetTag;
            return false;
        }
        return true;
    }
}
