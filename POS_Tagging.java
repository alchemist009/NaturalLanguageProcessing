import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class POS_Tagging {

    static Scanner in;
    static String argument1;
    static String argument2;
    static String argument3;
    static BufferedReader reader;
    static String allWords = "";
    static String token;
    static Map<String, LinkedList<TagAndCount>> wordAndTagCountMap;

    public static void main(String[] args) throws FileNotFoundException {
        try {
            in = new Scanner(System.in);

            System.out.println("Please enter the corpus file path: ");
            argument1 = in.nextLine();

            if(argument1 != null && !argument1.isEmpty()) {
                reader = new BufferedReader(new FileReader(argument1));
            }
            else {
                System.out.println("Please try again to enter the corpus path!");
                in.close();
                return;
            }

            ArrayList<String> allLines = new ArrayList<String>();
            String singleLine = reader.readLine();

            //create an array of individual lines in the corpus and find most probable tag for each word in the corpus
            while (singleLine != null) {
                allLines.add(singleLine);
                singleLine = reader.readLine();
            }

            wordAndTagCountMap = new HashMap<String, LinkedList<TagAndCount>>();
            for(String line: allLines) {
                String[] wordsWithTags = line.split(" ");
                for(String s1 : wordsWithTags) {
                    String[] individualWordWithTag = s1.split("_");
                    int flag = 0;
                    if (wordAndTagCountMap.containsKey(individualWordWithTag[0])) {
                        LinkedList<TagAndCount> linkedList = wordAndTagCountMap.get(individualWordWithTag[0]);
                        Iterator<TagAndCount> iterator = linkedList.iterator();

                        while (iterator.hasNext()) {
                            TagAndCount next = iterator.next();
                            if (next.tag.equals(individualWordWithTag[1])) {
                                next.count += 1;
                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {
                            TagAndCount newTag = new TagAndCount(individualWordWithTag[1], 1);
                            linkedList.add(newTag);
                        }
                        wordAndTagCountMap.put(individualWordWithTag[0], linkedList);
                    }
                    else {
                        LinkedList<TagAndCount> linkedList = new LinkedList<>();
                        linkedList.add(new TagAndCount(individualWordWithTag[1], 1));
                        wordAndTagCountMap.put(individualWordWithTag[0], linkedList);
                    }
                }
            }

            //re-tag corpus with the most probable POS tags
            ArrayList<String> newAllLines = new ArrayList<String>();

            for(String line: allLines) {
                StringBuilder newSingleLine = new StringBuilder();
                String[] wordsWithTags = line.split(" ");

                for(String s1 : wordsWithTags) {
                    String[] individualWordWithTag = s1.split("_");
                    LinkedList<TagAndCount> linkedList = wordAndTagCountMap.get(individualWordWithTag[0]);
                    Iterator<TagAndCount> iterator = linkedList.iterator();
                    Integer finalCount = 0;
                    String finalTag = "";

                    while (iterator.hasNext()) {
                        TagAndCount next = iterator.next();
                        if(next.count > finalCount) {
                            finalCount = next.count;
                            finalTag = next.tag;
                        }
                    }
                    newSingleLine.append(individualWordWithTag[0] + "_" + finalTag + " ");
                }

                newAllLines.add(newSingleLine.toString());
            }

            //tag the input sentence with tags from corpus
            System.out.println("Please enter the input sentence to check: ");
            argument2 = in.nextLine();

            StringBuilder inputSentenceTagged = new StringBuilder();
            String[] word = argument2.split(" ");

            for(int j=0; j<word.length; j++) {
                outerloop:
                for(int i=0; i<newAllLines.size(); i++) {
                    if(newAllLines.get(i).contains(word[j])) {
                        String[] split = newAllLines.get(i).split(" ");
                        for(String s1 : split) {
                            String[] split2 = s1.split("_");
                            for(String s2 : split2) {
                                if(s2.equals(word[j])) {
                                    inputSentenceTagged.append(word[j] + "_" + split2[1] +" ");
                                    break outerloop;
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("Please enter the gold-tagged sentence to check: ");
            argument3 = in.nextLine();

            //compare inputSentenceTagged and argument3 word by word
            int error = 0;
            String[] wordsInArgument = argument3.split(" ");
            String[] wordsInInputSentenceTagged = inputSentenceTagged.toString().split(" ");
            for(int i=0; i<wordsInArgument.length; i++) {
                if(!(wordsInArgument[i].equals(wordsInInputSentenceTagged[i]))) {
                    error++;
                }
            }

            double errorRate = ((double) error / (double) wordsInArgument.length) * 100;
            System.out.println("The input sentence with tags is: " + argument3 + "\n");
            System.out.println("The most probable sentence with tags is: " + inputSentenceTagged + "\n");
            System.out.println("The error rate with the most probable tag is: " + Double.toString(errorRate));


            /*
             *
             *
             * Finding the rules:
             */

        StringBuilder oldTaggedLines = new StringBuilder();
        String line = "";
        while((line = reader.readLine()) != null) {
            oldTaggedLines.append(line);
        }

        String[] goldCorpus = oldTaggedLines.toString().split("\n");
        String[] newTaggedCorpus = inputSentenceTagged.toString().split("\n");


        Map<FromToTag, Integer> rulesMap = new HashMap<>();
        int index = 1;
        for(int len = 0; len < goldCorpus.length; len++) {
            String oldCorpusWordsWithTag = goldCorpus[len];
            String newCorpusWordsWithTag = newTaggedCorpus[len];
            String[] wordsOld = oldCorpusWordsWithTag.split(" ");
            String[] wordsNew = newCorpusWordsWithTag.split(" ");
            for(int i = 1; i < wordsOld.length; i++) {
                String wordOldOne = wordsOld[i];
                String wordNewOne = wordsNew[i];
                String[] wordAlt = wordOldOne.split("_");
                String[] wordNeu = wordNewOne.split("_");
                if(!wordAlt[1].equals(wordNeu[1])) {
                    String from = wordNeu[1];

                    String to = wordAlt[1];
                    String newPrevious = wordNeu[i-1];
                    String[] newCorpusWordPreviousTag = newPrevious.split("_");
                    String previousTag = newCorpusWordPreviousTag[1];

                    FromToTag newRule = new FromToTag(from, to, previousTag);
                    if(rulesMap.containsKey(newRule)) {
                    }
                    else {
                        rulesMap.put(newRule, index);
                        index++;
                    }
                }
            }
        }

        rulesMap = FromToTag.sortByAscendingValue(rulesMap);
        Map<FromToTag, Integer> rulesWithCount = new HashMap<>();
        for(Map.Entry<FromToTag, Integer> entry : rulesMap.entrySet()) {
            FromToTag fromToTag = entry.getKey();
            String from = fromToTag.fromTag;
            String to = fromToTag.toTag;
            int value = 0;
            String previousTag = fromToTag.previousTag;
            for(int i = 0; i <newTaggedCorpus.length; i++) {
                String newCorpusSentence = newTaggedCorpus[i];
                String oldCorpusSentence = goldCorpus[i];
                String[] oldSentenceWords = newCorpusSentence.split(" ");
                String[] newSentenceWords = oldCorpusSentence.split(" ");
                for(int j = 1; j < oldSentenceWords.length; j++) {
                    String oldCorpusWord = oldSentenceWords[j];
                    String newCorpusWord = newSentenceWords[j];
                    String newCorpusPreviousWord = newSentenceWords[j-1];
                    String[] oldWordsWithTag = oldCorpusWord.split("_");
                    String[] newWordsWithTag = newCorpusWord.split("_");
                    String[] newPreviousWordWithTag = newCorpusPreviousWord.split("_");
                    if(oldWordsWithTag[1].equals(to) && newWordsWithTag[1].equals(from) && newPreviousWordWithTag[1].equals(previousTag)) {
                        value++;
                        rulesWithCount.put(fromToTag, value);
                    }
                    else {
                        if(oldWordsWithTag[1].equals(from) && newWordsWithTag[1].equals(to) && newPreviousWordWithTag[1].equals(previousTag)) {
                            value--;
                            rulesWithCount.put(fromToTag, value);
                        }
                    }
                }
            }
        }

        rulesWithCount = FromToTag.sortByDescendingValue(rulesWithCount);
        Map<FromToTag, Integer> finalRules = new HashMap<>();
        for(Map.Entry<FromToTag, Integer> entry : rulesWithCount.entrySet()) {
            FromToTag fromToTag = entry.getKey();
            if(entry.getValue() > 0) {
                finalRules.put(fromToTag, entry.getValue());
            }
        }

        finalRules = FromToTag.sortByDescendingValue(finalRules);
        StringBuilder sb = new StringBuilder();
        for(String s : newAllLines) {
            sb.append(s);
        }
        String newLines = sb.toString();
        String[] finalString = newLines.split(" ");
        for(int len = 1; len < finalString.length; len++) {
            String wordWithTag = finalString[len];
            String previousWordWithTag = finalString[len - 1];
            String[] previousWord = previousWordWithTag.split("_");
            String[] wordWithoutTag = wordWithTag.split("_");
            for(Map.Entry<FromToTag, Integer> entry : finalRules.entrySet()) {
                FromToTag fromToTag = entry.getKey();
                String to = fromToTag.toTag;
                String from = fromToTag.fromTag;
                String prev = fromToTag.previousTag;
                if(wordWithoutTag[1].equals(from) && previousWord[1].equals(prev)) {
                    String wordsWithFinalTag = wordWithoutTag[0] + "_" + to;
                    finalString[len] = wordsWithFinalTag;
                    break;
                }
            }
        }

        int errorCount = 0;
        for(int i = 0; i < wordsInArgument.length; i++) {
            if(!finalString[i].equals(wordsInArgument[i])) {
                errorCount++;
            }
        }

        System.out.println("The error rate with Brill's Tagger is: " + 100 * (1.0 * errorCount / wordsInArgument.length) + "%");



        }

        catch(Exception e) {
            System.out.println("File not found or error parsing the file!");
        }
    }
}
