import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        String inputFile = args[0];
        if (inputFile == null) {
            System.out.println("Please provide an input file");
            return;
        }
        MIPSParser parser = new MIPSParser();
        parser.printFileToBinary(inputFile);
    }
}
