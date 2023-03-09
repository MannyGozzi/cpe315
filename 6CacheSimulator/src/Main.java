import java.util.Scanner;
import java.io.File;
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <input file>");
            System.exit(1);
        }
        try {
            String inputFilename = args[0];
            File file = new File(inputFilename);
            Scanner input = new Scanner(file);
            Cache cache = new Cache(2, 1);
            int hits = 0; int misses = 0;
            while (input.hasNext()) {
                String line = input.nextLine();
                int address = Integer.parseInt(line, 16);
                if (cache.read(address)) {
                    ++hits;
                } else {
                    ++misses;
                }
            }
            printStats(hits, misses, 1, 2, 1, 1);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printStats(int hits, int misses, int cacheNum, int cacheSizeKB, int associativity, int blockSize) {
        System.out.println("Cache #" + cacheNum);
        System.out.println("Cache Size: " + cacheSizeKB*1024 + "B\t" + "Associativity: " + associativity + "\t" + "Block Size: " + blockSize);
        System.out.print("Hits: " + hits + "\t"); System.out.format("Hit Rate: %.2f%%", (double) hits/(hits+misses) * 100);
    }
}