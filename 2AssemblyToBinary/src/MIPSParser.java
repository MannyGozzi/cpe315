import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class MIPSParser {
    Map<String, String> labelToBinLoc;
    String inputFile;

    public void printFileToBinary(String inputFile) {
        this.inputFile = inputFile;
        labelToBinLoc = new TreeMap<>();
        firstPass();
        secondPass();
    }

    /*
    Finds the labels via the first pass and stores them
     */
    private void firstPass() {
        try {
            BufferedReader asm = new BufferedReader(new FileReader(inputFile));
            while (asm.ready()) {
                String line = asm.readLine();

                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    Performs the MIPS to binary conversion utilizing the labels from the firstPass()
     */
    private void secondPass() {
        try {
            BufferedReader asm = new BufferedReader(new FileReader(inputFile));
            while (asm.ready()) {
                String line = asm.readLine();

                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
