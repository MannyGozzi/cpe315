public class lab5 {
    // GHR must support 2, 4, and 8 bit predictions
    public static void main(String[] args) {
        String inputFile = args[0];
        MIPSParser parser = new MIPSParser();
        if (inputFile == null) {
            System.out.println("Please provide an input file");
            return;
        } else if (args.length > 2) {
            String script = args[1];
            String GHR_bits = args[2];
            parser.runInteractiveMode(inputFile, script, Integer.parseInt(GHR_bits));
        } else if (args.length > 1) {
            String script = args[1];
            parser.runInteractiveMode(inputFile, script, 2);
        } else {
            parser.runInteractiveMode(inputFile, "", 2);
        }
    }
}
