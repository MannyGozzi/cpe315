import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        String input = "test1.asm";
        MIPSParser parser = new MIPSParser();
        parser.printFileToBinary(input);
    }
}
