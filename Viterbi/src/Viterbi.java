import java.util.Scanner;

public class Viterbi {

    private static Scanner scn = new Scanner(System.in);
    public enum States {
        Hot, Cold
    }

    private static double[][] outputProb = new double[][] {
            {0.2, 0.4, 0.4},
            {0.5, 0.4, 0.1}
    };

    private static double[][] inputProb = new double[][] {
            {0.7, 0.3},
            {0.4, 0.6}
    };

    private static double[] initialProb = new double[] {0.8, 0.2};


    private static int[] states = new  int[] {States.Hot.ordinal(), States.Cold.ordinal()};

    public static void main(String[] args) {

        System.out.println("Enter the input sequence.");
        String sequence = scn.nextLine();
        int[] seqArray = new int[sequence.length()];

        if(!sequence.isEmpty()) {
            for(int i = 0; i < sequence.length(); i++) {
                seqArray[i] = Character.getNumericValue(sequence.charAt(i));
            }
        }

        double[][] probCalculated = new double[sequence.length()][states.length];
        int[][] backTrackPath = new int[states.length][sequence.length()];

        for(int state : states) {
            probCalculated[0][state] = initialProb[state] * outputProb[state][seqArray[0]-1];
            backTrackPath[state][0] = state;
        }

        for(int i = 1; i < seqArray.length; i++) {
            int[][] newBackTrackPath = new int[states.length][seqArray.length];

            for(int statesA : states) {
                double prob = -1;
                int state;

                for(int statesB : states) {
                    double newProb = probCalculated[i-1][statesB] * inputProb[statesB][statesA] * outputProb[statesA][seqArray[i] - 1];

                    if(newProb > prob) {
                        prob = newProb;
                        state = statesB;
                        probCalculated[i][statesA] = prob;
                        System.arraycopy(backTrackPath[state], 0, newBackTrackPath[statesA], 0, i);
                        newBackTrackPath[statesA][i] = statesA;
                    }
                }
            }

            backTrackPath = newBackTrackPath;
        }

        double finalProb = -1;
        int state = 0;

        for(int s: states) {
            if(probCalculated[seqArray.length - 1][s] > finalProb) {
                finalProb = probCalculated[seqArray.length - 1][s];
                state = s;
            }
        }

        System.out.println("The final probability is:" + finalProb);

        System.out.println("The backtrack path is: ");
        for(int i : backTrackPath[state]) {
            System.out.println(States.values()[i] + " ");
        }
    }
}
