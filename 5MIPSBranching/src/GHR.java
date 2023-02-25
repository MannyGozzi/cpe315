public class GHR {
    private static String GHR;
    private static int[] GHR_counts;

    public static void initGHR(int bits) {
        GHR = "";
        for (int i = 0; i < bits; i++) {
            GHR += "0";
        }
        GHR_counts = new int[(int) Math.pow(2, bits)];
    }

    /*
    returns if the branch prediction was correct
     */
    public static boolean pushGHR(String bit) {
        if (GHR_counts == null) { System.err.println("GHR not initialized"); return false;}
        int GHR_index = getDecimalGHR();
        boolean isTaken = bit.equals("1");
        boolean isTakenPrediction = isTaken(GHR_counts[GHR_index]);
        if (isTaken) incIndex(GHR_index);
        else decIndex(GHR_index);
        GHR = GHR.substring(1);
        GHR += bit;
        return isTaken==isTakenPrediction;
    }

    private static int getDecimalGHR() {
        if (GHR == null) { System.err.println("GHR not initialized"); return -1;}
        return Integer.parseInt(GHR, 2);
    }

    private static void incIndex(int index) {
        if (GHR_counts[index] < 3) GHR_counts[index] = GHR_counts[index] + 1;
    }

    private static void decIndex(int index) {
        if (GHR_counts[index] > 0) GHR_counts[index] = GHR_counts[index] - 1;
    }

    /*
    returns true if the given value is predicted to be taken (val >= 2)
    return false if the given value is predicted to be not taken (val <= 1)
     */
    private static boolean isTaken(int value) {
        return value >= 2;
    }
}
