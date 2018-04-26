/**
 * Implementation of POS Tagging using Naive Bayes
 * @author: Gunjan Tomer
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Naive_Bayes_POS_Tagger {

    static Scanner scn;
    static HashMap<String, Integer> tag_map = new HashMap<>();
    static HashMap<String, Integer> word_tag_map = new HashMap<>();

    public static void main(String[] args) throws FileNotFoundException {

        try {
            scn = new Scanner(System.in);
            System.out.println("Please enter the path to the corpus.");
            String corpusPath = scn.nextLine();
            BufferedReader reader = new BufferedReader(new FileReader(corpusPath));

            ArrayList<String> allLines = new ArrayList<>();
            String singleLine = reader.readLine();

            while (reader.readLine() != null) {
                allLines.add(reader.readLine());
                singleLine = reader.readLine();
            }
            System.out.println(corpusPath);
            for (String line : allLines) {
                String[] wordWithTag = line.split(" ");
                if (word_tag_map.containsKey(wordWithTag[0]))
                    word_tag_map.put(wordWithTag[0], word_tag_map.get(wordWithTag[0]) + 1);
                else
                    word_tag_map.put(wordWithTag[0], 1);
                //System.out.println(word_tag_map);
                for (String s : wordWithTag) {
                    //System.out.println(s);
                    String[] wordAndTag = s.split("_");
                    //System.out.println(wordAndTag[1]);
                    if (tag_map.containsKey(wordAndTag[1])) {
                        tag_map.put(wordAndTag[1], tag_map.get(wordAndTag[1]) + 1);
                    } else {
                        tag_map.put(wordAndTag[1], 1);
                    }
                }
                System.out.println(tag_map);
            }

            int total_no_of_tags = 0;
            for (int i : tag_map.values())
                total_no_of_tags += i;
            //System.out.println(total_no_of_tags);

            System.out.println();
            System.out.println("Enter the test sentence.");
            String testSentence = scn.nextLine();
            System.out.println("Enter the gold-tagged sentence.");
            String goldTaggedSentence = scn.nextLine();

            String predictedTagsWithMaxProb;
            HashMap<String, Double> tag_given_word_prob_map = new HashMap<>();
            HashMap<String, String> tag_given_word = new HashMap<>();
            double priorProbabilityOfTag = 0.0, maxProbOfTag = 0.0, likelihoodGivenTag = 0.0, probTagGivenWord = 0.0;
            String[] splitTestSentence = testSentence.split(" ");
            for (String word : splitTestSentence) {
                predictedTagsWithMaxProb = "";
                predictedTagsWithMaxProb = "NNP";
                for (String tag : tag_map.keySet()) {
                    //priorProbabilityOfTag = (double) tag_map.get(tag) / (double) total_no_of_tags;
                    priorProbabilityOfTag = 1.0;
                    String word_tag = word + "_" + tag;
                    if (word_tag_map.containsKey(word_tag)) {
                        likelihoodGivenTag = (double) word_tag_map.get(word_tag) / (double) tag_map.get(tag);
                        probTagGivenWord = priorProbabilityOfTag * likelihoodGivenTag;
                        if (probTagGivenWord > maxProbOfTag) {
                            predictedTagsWithMaxProb = "";
                            predictedTagsWithMaxProb = tag;
                            maxProbOfTag = probTagGivenWord;
                        } else if (probTagGivenWord == maxProbOfTag) {
                            predictedTagsWithMaxProb = tag;
                        }
                        if(!(tag_given_word_prob_map.containsKey(word_tag))) {
                            tag_given_word_prob_map.put(word_tag, probTagGivenWord);
                            //System.out.println("P(" + tag + "|" + splitTestSentence[0] + ")" + ":" + (tag_given_word.get(word_tag)) + "\n");
                        }
                        }
                    else if(!(tag_given_word_prob_map.containsKey(word_tag))) {
                        tag_given_word_prob_map.put(word_tag, 0.0);
                        //System.out.println("P(" + tag + "|" + splitTestSentence[0] + ")" + ":" + (tag_given_word.get(word_tag)) + "\n");
                    }

                    }
                tag_given_word.put(word, predictedTagsWithMaxProb);


            }

            int errorCount = 0;
            String newSentence = "";
            //goldTaggedSentence = allLines.toString();
            System.out.println(goldTaggedSentence);
            System.out.println();
            String[] wordsInArgument = goldTaggedSentence.split(" ");
            for (String aWordsInArgument : wordsInArgument) {
                String[] goldTagAndWord = aWordsInArgument.split("_");
                String goldTag = goldTagAndWord[1];
                newSentence += goldTagAndWord[0] + "_" + tag_given_word.get(goldTagAndWord[0]) + " ";
                if ((goldTagAndWord[1].equals(tag_given_word.get(goldTagAndWord[0])))) {
                    errorCount++;
                }
            }
            //System.out.println(newSentence);
            //System.out.println();
            System.out.println("The error rate with Naive Bayes is: " + 100 * (double) (errorCount) / (double) (wordsInArgument.length));

        }
        catch(Exception e) {
            System.out.println("File not found or error parsing file");
        }
    }
}
