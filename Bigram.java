import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Bigram {

    static Scanner scn;
    static String path;
    static String sentence;
    static BufferedReader reader;
    static String allLines = "";
    static String token1;
    static String token2;
    static Map<String, Integer> unigramsMap;
    static Map<String, Integer> bigramsMap;
    static final String noSmoothing = "NO_SMOOTHING";
    static final String addOneSmoothing = "ADD_ONE_SMOOTHING";
    static final String goodTuringSmoothing = "GOOD_TURING_SMOOTHING";
    static final DecimalFormat probFormat = new DecimalFormat("#.##");

    public static void main(String[] args) throws FileNotFoundException{

        scn = new Scanner(System.in);
        try{
            System.out.println("Please enter the corpus file path.");
            path = scn.nextLine();
            System.out.println(path);
            System.out.println("Please enter the sentence to be checked.");
            sentence = scn.nextLine();

            if(path != null && !path.isEmpty()) {
                reader = new BufferedReader(new FileReader(path));
            }
            else {
                System.out.println("Unable to find the corpus, please enter path again.");
                scn.close();
                return;
            }

            String line = reader.readLine();
            System.out.println(line);
            while(line != null) {
                allLines += " " + line;
                line = reader.readLine();
                System.out.println(line);
            }
            Tokenize(allLines);
            scn.close();
        }
        catch (Exception e) {
            System.out.println("There was an error parsing the file.");
        }

        if(sentence != null && !sentence.isEmpty()) {
            processSentence(sentence);
        }

        else {
            System.out.println("Please enter the sentence again.");
        }

    }

    private static void processSentence(String sentence) {

        smoothing(sentence, noSmoothing);
        smoothing(sentence, addOneSmoothing);
        smoothing(sentence, goodTuringSmoothing);

        noSmoothingProb(sentence);
        addOneSmoothingProb(sentence);
        goodTuringSmoothingProb(sentence);

    }

    private static void smoothing(String sentence, String smoothingMethod) {

        System.out.println();
        System.out.println("\nSmoothing method being used is: " + smoothingMethod);

        String tempToken1 = "", tempToken2 = "";
        int totalBigramCount = 0;
        int unigramCount;
        int bigramCount;

        double sentenceProbability = 1.0;
        double conditionalProbability;

        StringTokenizer sentenceTokenizer = new StringTokenizer(sentence);

        if(sentenceTokenizer.hasMoreTokens())
            tempToken1 = sentenceTokenizer.nextToken();

        for(Map.Entry<String, Integer> e : bigramsMap.entrySet()) {
            totalBigramCount += e.getValue();
        }

        while(sentenceTokenizer.hasMoreTokens()) {
            tempToken2 = sentenceTokenizer.nextToken();
            String token = tempToken1 + " " + tempToken2;

            boolean hasBigram = bigramsMap.containsKey(token);
            boolean hasUnigram = unigramsMap.containsKey(tempToken1);

            bigramCount = (hasBigram) ? bigramsMap.get(token) : 0;
            unigramCount = (hasUnigram) ? unigramsMap.get(tempToken1) : 0;

            switch (smoothingMethod) {
                case noSmoothing:
                    if (hasBigram) {
                        conditionalProbability = (double) bigramCount / (double) (unigramCount);
                        sentenceProbability *= conditionalProbability;
                    }
                    break;
                case addOneSmoothing:
                    conditionalProbability = (double) bigramCount / (double) (unigramCount + unigramsMap.size());
                    sentenceProbability *= conditionalProbability;
                    break;
                case goodTuringSmoothing:
                    int zeroBucketCount = 0;
                    int nextBigramCount = 0;

                    if (hasBigram) {
                        if (bigramCount == 0) {
                            zeroBucketCount = 1;
                            for (Map.Entry<String, Integer> entry : bigramsMap.entrySet()) {
                                if (entry.getValue() == bigramCount + 1) {
                                    nextBigramCount++;
                                }
                            }
                        } else {
                            for (Map.Entry<String, Integer> entry : bigramsMap.entrySet()) {
                                if (entry.getValue() == bigramCount) {
                                    zeroBucketCount++;
                                }
                                if (entry.getValue() == bigramCount + 1) {
                                    nextBigramCount++;
                                }
                            }
                        }
                    }

                    conditionalProbability = (double) (bigramCount + 1) * (nextBigramCount) / (double) (zeroBucketCount * totalBigramCount);
                    sentenceProbability *= conditionalProbability;
                    break;
            }
            tempToken1 = tempToken2;
        }
        System.out.println("The sentence probability is: " + sentenceProbability);
        System.out.println();

    }

    private static void Tokenize(String allLines) {

        try {
            unigramsMap = new HashMap<>();
            bigramsMap = new HashMap<>();
            StringTokenizer stringTokenizer = new StringTokenizer(allLines);
            token1 = stringTokenizer.nextToken();
            while (stringTokenizer.hasMoreTokens()) {
                token2 = stringTokenizer.nextToken();
                String token = token1 + " " + token2;
                if (bigramsMap.containsKey(token)) {
                    bigramsMap.put(token, bigramsMap.get(token) + 1);
                } else
                    bigramsMap.put(token, 1);

                if (unigramsMap.containsKey(token1)) {
                    unigramsMap.put(token1, unigramsMap.get(token1) + 1);
                } else
                    unigramsMap.put(token1, 1);
                token1 = token2;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error parsing the file");
        }
    }

    static void noSmoothingProb(String sentence) {
        System.out.println("\n Probability without any smoothing done is: ");
        String[] splitToken = sentence.split(" ");
        System.out.print("    ");
        for (String aSplitToken : splitToken) {
            System.out.print("\t" + aSplitToken);
        }
        System.out.println("\n");
        for (String aSplitToken1 : splitToken) {
            System.out.print("  ");
            for (String aSplitToken2 : splitToken) {
                String bigramString = aSplitToken1 + " " + aSplitToken2;
                if (bigramsMap.containsKey(bigramString)) {
                    System.out.print("\t" + probFormat.format((double) bigramsMap.get(bigramString) / (double) unigramsMap.get(aSplitToken1)));
                } else {
                    System.out.print("\t0");
                }
            }
            System.out.print("\n");
        }
    }

    static void addOneSmoothingProb(String sentence) {
        System.out.println("\n Probability with the Add One smoothing method is: ");
        String[] splitToken = sentence.split(" ");
        System.out.print("    ");
        for(String aSplitToken : splitToken) {
            System.out.print("\t" + aSplitToken);
        }
        System.out.print("\n");
        for (String aSplitToken1 : splitToken) {
            System.out.print("  ");
            for (String aSplitToken2 : splitToken) {
                String bigramString = aSplitToken1 + " " + aSplitToken2;
                if (bigramsMap.containsKey(bigramString)) {
                    System.out.print("\t" + probFormat.format((double) (bigramsMap.get(bigramString) + 1)/ (double) (unigramsMap.get(aSplitToken1)) + unigramsMap.size()));
                } else {
                    System.out.print("\t0");
                }
            }
            System.out.print("\n");
        }
    }

    static void goodTuringSmoothingProb(String sentence) {
        System.out.print("Probability with the Good Turing Smoothing method is: ");
        System.out.println();
        String[] splitToken = sentence.split(" ");
        System.out.print("    ");
        for(String aSplitToken : splitToken)
            System.out.print("\t" + aSplitToken);
        System.out.println("\n");
        int bigramCount, totalBigramCount = 0;

        for(Map.Entry<String, Integer> entry : bigramsMap.entrySet())
            totalBigramCount += entry.getValue();

        for (String aSplitToken1 : splitToken) {
            System.out.print("  ");
            for (String aSplitToken2 : splitToken) {
                String bigramString = aSplitToken1 + " " + aSplitToken2;
                boolean hasBigram = bigramsMap.containsKey(bigramString);
                bigramCount = (hasBigram) ? bigramsMap.get(bigramString) : 0;

                int zeroBucketCount = 0;
                int nextBigramCount = 0;
                if (hasBigram) {
                    if (bigramCount == 0) {
                        zeroBucketCount = 1;
                        for (Map.Entry<String, Integer> entry : bigramsMap.entrySet()) {
                            if (entry.getValue() == bigramCount + 1) {
                                nextBigramCount++;
                            }
                        }
                    } else {
                        for (Map.Entry<String, Integer> entry : bigramsMap.entrySet()) {
                            if (entry.getValue() == bigramCount) {
                                zeroBucketCount++;
                            }
                            if (entry.getValue() == bigramCount + 1) {
                                nextBigramCount++;
                            }
                        }
                    }
                    System.out.print("\t" + probFormat.format((double) (bigramCount + 1) * (nextBigramCount) / (double) (zeroBucketCount + totalBigramCount)));
                }
                else {
                    System.out.print("\t0");
                }
            }
            System.out.print("\n");
        }

    }

    public static void printNoSmoothingFrequency(String sentence) {

        System.out.println("Bigram count using no smoothing method is: ");
        String[] words = sentence.split(" ");

        for(String w : words){

            if(bigramsMap.containsKey(w)) {

            }
        }


    }

}
