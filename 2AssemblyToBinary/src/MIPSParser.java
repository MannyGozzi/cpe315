import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
Resource https://www.dcc.fc.up.pt/~ricroc/aulas/1920/ac/apontamentos/P04_encoding_mips_instructions.pdf
 */
public class MIPSParser {
    Map<String, String> labelToBinLoc;
    String inputFile;

    public void printFileToBinary(String inputFile) {
        this.inputFile = inputFile;
        labelToBinLoc = new TreeMap<>();
        firstPass();
        printMapping();
        secondPass();
    }

    /*
    Finds the labels via the first pass and stores them
     */
    private void firstPass() {
        try {
            BufferedReader asm = new BufferedReader(new FileReader(inputFile));
            int counter = 0;
            while (asm.ready()) {
                String line = asm.readLine().trim();
                if (line.length() == 0 || line.charAt(0) == '#') continue;
                Pattern labelPattern = Pattern.compile("(\\w+):(.*)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = labelPattern.matcher(line);
                if (matcher.find()) {
                    System.out.println("match " + counter + ": " + matcher.group(1) + " remainder: \"" + matcher.group(2) + "\""); // gets the first match
//                    if (matcher.group(2).length() > 0) {
//                        labelToBinLoc.put(matcher.group(1), Integer.toBinaryString(counter));
//                    }
                    labelToBinLoc.put(matcher.group(1), Operations.getBinaryWithSize(counter, 26));
//                    else {
//                        while(asm.ready()) {
//                            line = asm.readLine().trim();
//                            if (line.length() != 0 && line.charAt(0) != '#') break;
//                        }
//                        labelToBinLoc.put(matcher.group(1), Integer.toBinaryString(counter));
//                    }
                }
                ++counter;
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
            int counter = 0;
            while (asm.ready()) {
                String line = asm.readLine().trim();
                if (line.length() == 0 || line.charAt(0) == '#') continue;
                Pattern labelPattern = Pattern.compile("(\\w+):(.*)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = labelPattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.group(2).length() > 0) {
                        processCommand(matcher.group(2).trim());
                    }
                    else {
                        while(asm.ready()) {
                            line = asm.readLine().trim();
                            if (line.length() != 0 && line.charAt(0) != '#') break;
                        }
                        processCommand(line.trim());
                    }
                }
                ++counter;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processCommand(String command) {
        if (command.charAt(0) == '#') return;

        Pattern labelPattern = Pattern.compile("(\\w+):(.*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = labelPattern.matcher(command);
        if (matcher.find()) {
            System.out.println("match: " + matcher.group(1) + " remainder: \"" + matcher.group(2) + "\""); // gets the first match
        }
    }

    private void printMapping() {
        for (String label : labelToBinLoc.keySet()) {
            System.out.println(label + ":\t " + labelToBinLoc.get(label));
        }
    }
}
