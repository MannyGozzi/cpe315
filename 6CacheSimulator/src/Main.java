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
            Cache cache = new Cache(2, 1, 1);
            while (input.hasNext()) {
                int address = Integer.parseInt(input.nextLine(), 16);
                cache.read(address);
            }
            printStats(1, cache);
        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    private static void printStats(int cacheNum, Cache cache) {
        System.out.println("Cache #" + cacheNum);
        System.out.println("Cache size: " + cache.getCacheSizeBytes() + "B\t" + "Associativity: " + cache.getAssociativity() + "\t" + "Block Size: " + cache.getSizeBlock());
        System.out.print("Hits: " + cache.getHits() + "\t"); System.out.format("Hit Rate: %.2f%%", (double) cache.getHits()/(cache.getHits()+cache.getMisses()) * 100);
    }
}