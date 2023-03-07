import java.util.Scanner;
import java.io.File;
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <input file>");
            System.exit(1);
        }
        try {
            File file = new File("input.txt");
            Scanner input = new Scanner(file);
            CacheDirect cache = new CacheDirect(2, 1);
            while (input.hasNext()) {
                String line = input.nextLine();

            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}