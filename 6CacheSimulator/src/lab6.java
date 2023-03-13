import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
public class lab6 {
    public static void main(String[] args) {
        ArrayList<Cache> caches = new ArrayList<>();
        if (args.length != 1) {
            System.out.println("Usage: java Main <input file>");
            System.exit(1);
        }
        try {
            String inputFilename = args[0];
            File file = new File(inputFilename);
            Scanner input = new Scanner(file);
            caches.add(new Cache(2, 1, 1));
            caches.add(new Cache(2, 2, 1));
            caches.add(new Cache(2, 4, 1));
            caches.add(new Cache(2, 1, 2));
            caches.add(new Cache(2, 1, 4));
            caches.add(new Cache(2, 4, 4));
            caches.add(new Cache(4, 1, 1));
            while (input.hasNext()) {
                int address = Integer.parseInt(input.nextLine(), 16);
                for (Cache cache : caches) cache.read(address);
            }
            for (int i = 0; i < caches.size(); ++i) {
                printStats(i+1, caches.get(i));
            }
        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    private static void printStats(int cacheNum, Cache cache) {
        System.out.println("Cache #" + cacheNum + "\n" + cache);
        System.out.println("---------------------------");
    }
}