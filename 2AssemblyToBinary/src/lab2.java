public class lab2 {
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
