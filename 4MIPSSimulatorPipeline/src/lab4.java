public class lab4 {
    public static void main(String[] args) {
        String inputFile = args[0];
        MIPSParser parser = new MIPSParser();
        if (inputFile == null) {
            System.out.println("Please provide an input file");
            return;
        } else if (args.length > 1 && args[1] != null) {
            String script = args[1];
            parser.runInteractiveMode(inputFile, script);
        } else {
            parser.runInteractiveMode(inputFile, "");
        }
    }
}
