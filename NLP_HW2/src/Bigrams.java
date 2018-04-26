/**
 *
 *
 * Implementation of Bigrams
 * @author: Gunjan Tomer
 *
 */

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Bigrams {

    static BufferedReader reader;
    static Map<String, Integer> bigrams_Map;
    static Map<String, Integer> unigrams_Map;
    static Map<Integer, Integer> freqCalculatorMap;
    static String allLines = "";
    static String token1;
    static String token2;
    static String argument1;
    static String argument2;
    static Scanner in;
    static final String noSmoothing = "NO_SMOOTHING";
    static final String addOneSmoothing = "ADD_ONE_SMOOTHING";
    static final String goodTuringSmoothing = "GOOD_TURING_SMOOTHING";

    public static void main(String[] args) throws FileNotFoundException {
        try {
            in = new Scanner(System.in);

            System.out.println("Please enter the corpus file path: ");
            argument1 = in.nextLine();
            System.out.println("Please enter the sentence to check: ");
            argument2 = in.nextLine();

            if(argument1 != null && !argument1.isEmpty()) {
                reader = new BufferedReader(new FileReader(argument1));
            }
            else {
                System.out.println("Please try again to enter the corpus path!");
                in.close();
                return;
            }

            String singleLine = reader.readLine();
            while (singleLine != null) {
                allLines = allLines + " " + singleLine;
                singleLine = reader.readLine();
            }
            Tokenize(allLines);
            in.close();

        } catch (Exception e) {
            System.out.println("File not found or error parsing the file!");
        }

        if(argument2 != null && !argument2.isEmpty()) {
            CalculateSentence(argument2);
        }
        else {
            System.out.println("Please try again to enter the sentence!");
            return;
        }
    }

    //Method to tokenize the corpus text
    //Creates two hash maps, one each for unigrams and bigrams
    static void Tokenize(String allLines) {
        try {
            bigrams_Map = new HashMap<String, Integer>();
            unigrams_Map = new HashMap<String, Integer>();

            StringTokenizer tokenizer = new StringTokenizer(allLines);
            token1 = tokenizer.nextToken();

            while (tokenizer.hasMoreTokens()) {
                String token2 = tokenizer.nextToken();
                String word = token1 + " " + token2;

                if (bigrams_Map.containsKey(word)) {
                    bigrams_Map.put(word, bigrams_Map.get(word) + 1);
                } else {
                    bigrams_Map.put(word, 1);
                }

                if (unigrams_Map.containsKey(token1)) {
                    unigrams_Map.put(token1, unigrams_Map.get(token1) + 1);
                } else {
                    unigrams_Map.put(token1, 1);
                }
                token1 = token2;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error parsing the file");
        }
    }

    //Method to do all the calculations
    static void CalculateSentence(String sentence) {

        doSmoothing(sentence, noSmoothing);
        doSmoothing(sentence, addOneSmoothing);
        doSmoothing(sentence, goodTuringSmoothing);

        noSmoothingFreq(sentence);
        noSmoothingProb(sentence);

        addOneSmoothingFreq(sentence);
        addOneSmoothingProb(sentence);

        goodTuringSmoothingFreq(sentence);
        goodTuringSmoothingProb(sentence);
    }

    //Method to do the smoothing based on the smoothing type
    static void doSmoothing(String sentence, String smoothingType) {
        System.out.println();
        System.out.println("Smoothing type is: " + smoothingType);

        String tempToken1 = "", tempToken2 = "";
        int unigramCount, bigramCount, sumOfBigramCounts = 0;

        double sentenceProb = 1.0, conditionalProb;
        StringTokenizer tempTokenize = new StringTokenizer(sentence);

        if (tempTokenize.hasMoreTokens()) {
            tempToken1 = tempTokenize.nextToken();
        }

        for(Map.Entry<String, Integer> entry : bigrams_Map.entrySet()) {
            sumOfBigramCounts += entry.getValue();
        }

        while (tempTokenize.hasMoreTokens()) {
            tempToken2 = tempTokenize.nextToken();
            String bigramToken = tempToken1 + " " + tempToken2;

            boolean isBigramPresent = bigrams_Map.containsKey(bigramToken);
            boolean isUnigramPresent = unigrams_Map.containsKey(tempToken1);

            bigramCount = (isBigramPresent) ? bigrams_Map.get(bigramToken) : 0;
            unigramCount = (isUnigramPresent) ? unigrams_Map.get(tempToken1) : 0;

            if (smoothingType == noSmoothing) {
                if (isBigramPresent) {
                    conditionalProb = (double) (bigramCount) / (double) (unigramCount);
                    sentenceProb *= conditionalProb;
                }
            }

            else if (smoothingType == addOneSmoothing) {
                conditionalProb = (double) (bigramCount + 1) / (double) (unigramCount + unigrams_Map.size());
                sentenceProb *= conditionalProb;
            }
            else if (smoothingType == goodTuringSmoothing) {
                int countOfZeroBucket = 0, countOfNextBigram = 0;
                if(isBigramPresent) {
                    if(bigramCount == 0) {
                        countOfZeroBucket = 1;
                        for(Map.Entry<String, Integer> entry : bigrams_Map.entrySet()) {
                            if(entry.getValue() == bigramCount + 1) {
                                countOfNextBigram++;
                            }
                        }
                    }
                    else {
                        for(Map.Entry<String, Integer> entry : bigrams_Map.entrySet()) {
                            if(entry.getValue() == bigramCount) {
                                countOfZeroBucket++;
                            }
                            if(entry.getValue() == bigramCount + 1) {
                                countOfNextBigram++;
                            }
                        }
                    }
                }

                conditionalProb = (double) ((bigramCount+1) * (countOfNextBigram)) /
                        (double) (countOfZeroBucket * sumOfBigramCounts);
                sentenceProb *= conditionalProb;
            }
            tempToken1 = tempToken2;
        }
        System.out.println("Sentence probability is: " + sentenceProb);
        System.out.println();
    }

    //Method to print frequency for 'no smoothing'
    static void noSmoothingFreq(String sentence) {
        System.out.println("************************ Frequency: No Smoothing***************************** ");
        DecimalFormat dF = new DecimalFormat("#.###");
        String[] tokenz = sentence.split(" ");
        System.out.print(addSpace(" "));
        for (int cntr = 0; cntr < tokenz.length; cntr++) {
            System.out.print("\t" + tokenz[cntr]);
        }
        System.out.println("\n");
        for (int cntr = 0; cntr < tokenz.length; cntr++) {
            System.out.print(addSpace(tokenz[cntr]));
            for (int cntr1 = 0; cntr1 < tokenz.length; cntr1++) {
                String bigram = tokenz[cntr] + " " + tokenz[cntr1];
                if (bigrams_Map.containsKey(bigram)) {
                    System.out.print("\t" + dF.format(bigrams_Map.get(bigram)));
                } else {
                    System.out.print("\t0");
                }
            }
            System.out.println("\n");
        }
    }

    //Method to print frequency for 'add one smoothing'
    static void addOneSmoothingFreq(String sentence) {
        System.out.println("************************ Frequency: Add One Smoothing***************************** ");
        printFrequency(sentence);
    }

    //Method to print frequency for 'good Turing smoothing'
    static void goodTuringSmoothingFreq(String sentence) {
        System.out.println("************************ Frequency: Good Turing Smoothing***************************** ");
        printFrequency(sentence);
    }

    //Method to print probability for 'no smoothing'
    static void noSmoothingProb(String sentence) {
        System.out.println("************************ Probability for No Smoothing ***************************** ");
        DecimalFormat dF = new DecimalFormat("#.###");
        String[] tempToken = sentence.split(" ");
        System.out.print(addSpace(" "));
        for (int cntr = 0; cntr < tempToken.length; cntr++) {
            System.out.print("\t" + tempToken[cntr]);
        }
        System.out.println("\n");
        for (int cntr = 0; cntr < tempToken.length; cntr++) {
            System.out.print(addSpace(tempToken[cntr]));
            for (int cntr1 = 0; cntr1 < tempToken.length; cntr1++) {
                String bigram = tempToken[cntr] + " " + tempToken[cntr1];
                if (bigrams_Map.containsKey(bigram)) {
                    System.out
                            .print("\t"
                                    + dF.format(((double) bigrams_Map.get(bigram) /
                                    (double) unigrams_Map.get(tempToken[cntr1]))));
                } else {
                    System.out.print("\t0");
                }
            }
            System.out.println("\n");
        }
    }

    //Method to print probability for 'add one smoothing'
    static void addOneSmoothingProb(String sentence) {
        System.out.println("************************ Probability for Add One Smoothing***************************** ");
        DecimalFormat dF = new DecimalFormat("#.###");
        String[] tokens = sentence.split(" ");
        System.out.print(addSpace(" "));
        for (int cntr = 0; cntr < tokens.length; cntr++) {
            System.out.print("\t" + tokens[cntr]);
        }
        System.out.println("\n");
        for (int cntr = 0; cntr < tokens.length; cntr++) {
            System.out.print(addSpace(tokens[cntr]));
            for (int cntr1 = 0; cntr1 < tokens.length; cntr1++) {
                String bigram = tokens[cntr] + " " + tokens[cntr1];
                if (bigrams_Map.containsKey(bigram)) {
                    System.out
                            .print("\t"
                                    + dF.format(((double) (bigrams_Map .get(bigram) + 1) /
                                    ((double) unigrams_Map.get(tokens[cntr1]) + unigrams_Map.size()))));
                } else {
                    System.out.print("\t0");
                }
            }
            System.out.println("\n");
        }
    }

    //Method to print probability for 'good Turing smoothing'
    static void goodTuringSmoothingProb(String sentence) {
        System.out.println("************************ Probability for Good Turing Smoothing***************************** ");
        DecimalFormat dF = new DecimalFormat("#.###");
        String[] tokens = sentence.split(" ");
        System.out.print(addSpace(" "));
        for (int cntr = 0; cntr < tokens.length; cntr++) {
            System.out.print("\t" + tokens[cntr]);
        }
        System.out.println("\n");
        int bigramCount, sumOfBigramCounts = 0;

        for(Map.Entry<String, Integer> entry : bigrams_Map.entrySet()) {
            sumOfBigramCounts += entry.getValue();
        }

        for (int cntr = 0; cntr < tokens.length; cntr++) {
            System.out.print(addSpace(tokens[cntr]));
            for (int cntr1 = 0; cntr1 < tokens.length; cntr1++) {
                String bigram = tokens[cntr] + " " + tokens[cntr1];
                boolean isBigramPresent = bigrams_Map.containsKey(bigram);
                bigramCount = (isBigramPresent) ? bigrams_Map.get(bigram) : 0;

                int countOfZeroBucket = 0, countOfNextBigram = 0;
                if(isBigramPresent) {
                    if(bigramCount == 0) {
                        countOfZeroBucket = 1;
                        for(Map.Entry<String, Integer> entry : bigrams_Map.entrySet()) {
                            if(entry.getValue() == bigramCount + 1) {
                                countOfNextBigram++;
                            }
                        }
                    }
                    else {
                        for(Map.Entry<String, Integer> entry : bigrams_Map.entrySet()) {
                            if(entry.getValue() == bigramCount) {
                                countOfZeroBucket++;
                            }
                            if(entry.getValue() == bigramCount + 1) {
                                countOfNextBigram++;
                            }
                        }
                    }
                    System.out
                            .print("\t"
                                    + dF.format(((double) ((bigramCount+1) * (countOfNextBigram)) /
                                    (double) (countOfZeroBucket * sumOfBigramCounts))));
                }
                else {
                    System.out.print("\t0");
                }
            }
            System.out.println("\n");
        }
    }

    //Helper method for adding spaces between strings
    static String addSpace(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s);
        for (int i=1; i<(10-s.length()); i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    //Helper method to print frequency for both add one smoothing and Good Turing smoothing
    static void printFrequency(String sentence) {
        DecimalFormat dF = new DecimalFormat("#.###");
        String[] tokens = sentence.split(" ");
        System.out.print(addSpace(" "));
        for (int cntr = 0; cntr < tokens.length; cntr++) {
            System.out.print("\t" + tokens[cntr]);
        }
        System.out.println("\n");
        for (int cntr = 0; cntr < tokens.length; cntr++) {
            System.out.print(addSpace(tokens[cntr]));
            for (int cntr1 = 0; cntr1 < tokens.length; cntr1++) {
                String bigram = tokens[cntr] + " " + tokens[cntr1];
                if (bigrams_Map.containsKey(bigram)) {
                    System.out.print("\t" + dF.format(bigrams_Map.get(bigram)+1));

                } else {
                    System.out.print("\t0");
                }
            }
            System.out.println("\n");
        }
    }
}
