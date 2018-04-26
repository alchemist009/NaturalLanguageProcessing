import javax.swing.text.html.HTML;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NaiveBayes {

    static Scanner scn;
    static HashMap<String, Integer> tag_map = new HashMap<>();
    static HashMap<String, String> best_tag_map = new HashMap<>();
    static HashMap<String, LinkedList<TagAndCount>> wordAndTagCountMap;

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
                    //newSingleLine.append(individualWordWithTag[0] + "_" + finalTag + " ");
                    best_tag_map.put(individualWordWithTag[0], finalTag);
                }
            }

            // Calculating priors

            HashMap<String, Integer> tagFrequency = new HashMap<>();
            HashMap<String, Double> priorProbab = new HashMap<>();
            StringBuilder wordsWithNewTags = new StringBuilder();

            for(String line : allLines) {
                String[] word_tag = line.split(" ");
                for(String w : word_tag) {
                    String[] splitWordAndTag = w.split("_");
                    wordsWithNewTags.append(splitWordAndTag[0] + "_" + best_tag_map.get(splitWordAndTag[0]) + " ");
                }
            }
            //System.out.println(wordsWithNewTags.toString());
            int z = best_tag_map.size();

            for(String line : allLines) {
                String[] word_tag = line.split(" ");
                for(String w : word_tag) {
                    String[] splitWordAndTag = w.split("_");
                    if(wordAndTagCountMap.containsKey(splitWordAndTag[0])){
                        LinkedList<TagAndCount> listOfTags = new LinkedList<>(wordAndTagCountMap.get(splitWordAndTag[0]));
                        for(TagAndCount tagAndCount : listOfTags) {
                            if(tagFrequency.containsKey(tagAndCount.tag))
                                tagFrequency.put(tagAndCount.tag, tagFrequency.get(tagAndCount.tag) + 1);
                            else
                                tagFrequency.put(tagAndCount.tag, 1);
                        }
                    }
                }
            }

            for(String line : allLines) {
                String[] word_tag = line.split(" ");
                for(String w : word_tag) {
                    String[] splitWordAndTag = w.split("_");
                    if(wordAndTagCountMap.containsKey(splitWordAndTag[0])){
                        LinkedList<TagAndCount> listOfTags = new LinkedList<>(wordAndTagCountMap.get(splitWordAndTag[0]));
                        for(TagAndCount tagAndCount : listOfTags) {
                            priorProbab.put(tagAndCount.tag, (double) tagFrequency.get(tagAndCount.tag) / z);
                        }
                    }
                }
            }

            System.out.println(priorProbab);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}