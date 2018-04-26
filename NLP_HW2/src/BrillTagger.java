/**
 *
 * Implementation of Brill Tagger
 * @author: Gunjan Tomer
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

class rule {

    String from_Tag;
    String to_Tag;
    String previous_Tag;

    rule(String from, String to, String previous) {
        from_Tag = from;
        to_Tag = to;
        previous_Tag = previous;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.from_Tag);
        hash = 37 * hash + Objects.hashCode(this.to_Tag);
        hash = 37 * hash + Objects.hashCode(this.previous_Tag);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final rule other = (rule) obj;
        if (!Objects.equals(this.from_Tag, other.from_Tag)) {
            return false;
        }
        if (!Objects.equals(this.to_Tag, other.to_Tag)) {
            return false;
        }
        if (!Objects.equals(this.previous_Tag, other.previous_Tag)) {
            return false;
        }
        return true;
    }

}

class List {

    String tag;
    int count;

    List(String t, int c) {
        tag = t;
        count = c;
    }
}

public class BrillTagger {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByDescendingValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByAscendingValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //Part-A
        Scanner scn = new Scanner(System.in);
        System.out.println("Enter the path to the corpus.");
        String arg1 = scn.nextLine();
        FileReader fReader = new FileReader(arg1);
        BufferedReader br = new BufferedReader(fReader);
        String eachLine = "";
        String linesOfCorpus = "";
        Map<String, LinkedList<List>> posCounter = new HashMap<>();
        while ((eachLine = br.readLine()) != null) {
            linesOfCorpus += eachLine + "\n";

        }

        // Map for Word: List of tags with Count
        String[] goldCorpusLines = linesOfCorpus.split("\n");
        for (String line : goldCorpusLines) {
            String[] words = line.split(" ");

            for (String wordsWithTag : words) {
                String[] word = wordsWithTag.split("_");
                int flag = 0;
                if (posCounter.containsKey(word[0])) {
                    LinkedList<List> ll = posCounter.get(word[0]);
                    Iterator<List> iterator = ll.iterator();
                    while (iterator.hasNext()) {
                        List next = iterator.next();
                        if (next.tag.equals(word[1])) {
                            next.count += 1;
                            flag = 1;
                            break;
                        }

                    }
                    if (flag == 0) {
                        List newTag = new List(word[1], 1);
                        ll.add(newTag);
                    }
                    posCounter.put(word[0], ll);
                } else {
                    LinkedList<List> ll = new LinkedList<>();
                    ll.add(new List(word[1], 1));
                    posCounter.put(word[0], ll);
                }
            }
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the test sentence.");
        String str = sc.nextLine();
        System.out.println("Enter the gold-tagged sentence.");
        String answer = sc.nextLine();
        String finalString = "";

        String[] words = str.split(" ");
        for (String word : words) {
            String Tag = "";
            for (Map.Entry<String, LinkedList<List>> entry : posCounter.entrySet()) {
                if (entry.getKey().equals(word)) {
                    LinkedList<List> ll = entry.getValue();
                    Iterator<List> iterator = ll.iterator();
                    int max = 0;

                    while (iterator.hasNext()) {

                        List next = iterator.next();
                        if (next.count > max) {
                            max = next.count;
                            Tag = next.tag;
                        }

                    }
                    break;
                }
            }
            finalString += word + "_" + Tag + " ";

        }
        String[] partA = finalString.split(" ");
        String[] partAprime = answer.split(" ");
        int stringLength = partAprime.length;
        int errorCount = 0;
        for (int len = 0; len < partAprime.length; len++) {
            if (!partA[len].equals(partAprime[len])) {
                errorCount++;
            }
        }
        //Part B : Rules
        String[] newCorpusLines = new String[goldCorpusLines.length];
        int lineNumber = 0;
        for (String line : goldCorpusLines) {
            String[] wordsInaLine = line.split(" ");
            String lineWithProbableTag = "";
            for (int len = 0; len < wordsInaLine.length; len++) {
                String[] wordT = wordsInaLine[len].split("_");
                //System.out.println(wordT[0]);
                String wordPart = wordT[0];
                for (Map.Entry<String, LinkedList<List>> entry : posCounter.entrySet()) {
                    String key = entry.getKey();
                    if (key.equals(wordPart)) {
                        LinkedList<List> ll = entry.getValue();
                        Iterator<List> iterator = ll.iterator();
                        int max = 0;
                        String Tag = "";
                        while (iterator.hasNext()) {
                            List next = iterator.next();
                            if (next.count > max) {
                                max = next.count;
                                Tag = next.tag;
                            }
                        }
                        String tag = Tag;
                        if (len == wordsInaLine.length - 1) {
                            lineWithProbableTag += wordPart + "_" + tag;
                        } else {
                            lineWithProbableTag += wordPart + "_" + tag + " ";
                        }
                        break;
                    }

                }
            }
            newCorpusLines[lineNumber++] = lineWithProbableTag;
        }
        Map<rule, Integer> Rules = new HashMap<>();
        int index = 1;
        System.out.println("The rules learned are: ");
        for (int len = 0; len < newCorpusLines.length; len++) {

            String oldCorpusWordWithTag = goldCorpusLines[len];
            String newCorpusWordWithTag = newCorpusLines[len];
            String[] wordsOld = oldCorpusWordWithTag.split(" ");
            String[] wordsNew = newCorpusWordWithTag.split(" ");
            for (int k = 1; k < wordsOld.length; k++) {
                String wordOldEach = wordsOld[k];
                String wordNewEach = wordsNew[k];
                String[] wordOld = wordOldEach.split("_");
                String[] wordNew = wordNewEach.split("_");
                if (!wordOld[1].equals(wordNew[1])) {
                    String from = wordNew[1];

                    String to = wordOld[1];
                    String newCorpusWordPrevious = wordsNew[k - 1];
                    String[] newCorpusWordPreviousTag = newCorpusWordPrevious.split("_");
                    String previous_Tag = newCorpusWordPreviousTag[1];
                    System.out.println();
                    System.out.println(from + "->" + to + "  if prev: " + previous_Tag);

                    rule NewRule = new rule(from, to, previous_Tag);
                    if (Rules.containsKey(NewRule)) {
                    } else {

                        Rules.put(NewRule, index);
                        index++;
                    }
                }
            }
        }
        Rules = sortByAscendingValue(Rules);
        Map<rule, Integer> RuleswithCount = new HashMap<>();
        for (Map.Entry<rule, Integer> entry : Rules.entrySet()) {
            rule r = entry.getKey();
            String from = r.from_Tag;
            String to = r.to_Tag;
            int value = 0;
            String previous_tag = r.previous_Tag;
            for (int i = 0; i < newCorpusLines.length; i++) {
                String newCorpusSentence = newCorpusLines[i];
                String oldCorpusSentence = goldCorpusLines[i];
                String[] oldSentenceWords = oldCorpusSentence.split(" ");
                String[] newSentenceWords = newCorpusSentence.split(" ");
                for (int j = 1; j < oldSentenceWords.length; j++) {
                    String oldCorpusWord = oldSentenceWords[j];
                    String newCorpusPreviousWord = newSentenceWords[j - 1];
                    String newCorpusWord = newSentenceWords[j];
                    String[] oldWordWithTag = oldCorpusWord.split("_");
                    String[] newWordWithTag = newCorpusWord.split("_");
                    String[] newPreviousWordWithTag = newCorpusPreviousWord.split("_");
                    if (oldWordWithTag[1].equals(to) && newWordWithTag[1].equals(from) && newPreviousWordWithTag[1].equals(previous_tag)) {

                        value++;
                        RuleswithCount.put(r, value);

                    } else {
                        if (oldWordWithTag[1].equals(from) && newWordWithTag[1].equals(from) && newPreviousWordWithTag[1].equals(previous_tag)) {

                            value--;
                            RuleswithCount.put(r, value);
                        }
                    }
                }
            }

        }
        RuleswithCount = sortByDescendingValue(RuleswithCount);
        Map<rule, Integer> finalRules = new HashMap<>();
        for (Map.Entry<rule, Integer> entry : RuleswithCount.entrySet()) {
            rule r = entry.getKey();
            if (entry.getValue() > 0) {
                finalRules.put(r, entry.getValue());
            }
        }
        finalRules = sortByDescendingValue(finalRules);
        for (int len = 1; len < partA.length; len++) {
            String wordwithTag = partA[len];
            String prevwordwithTag = partA[len - 1];
            String[] prevWord = prevwordwithTag.split("_");
            String[] word = wordwithTag.split("_");
            for (Map.Entry<rule, Integer> entry : finalRules.entrySet()) {
                rule r = entry.getKey();
                String from = r.from_Tag;
                String to = r.to_Tag;
                String prev_tag = r.previous_Tag;
                if (word[1].equals(from) && prevWord[1].equals(prev_tag)) {
                    String wordWithUpdatedTag = word[0] + "_" + to;
                    partA[len] = wordWithUpdatedTag;
                    break;
                }
            }
        }
        System.out.println("The string made with most probable tag: " + finalString);
        System.out.println("Error rate with most probable tag: " + 100 * (1.0 * errorCount / stringLength) + "%");

        errorCount = 0;
        for (int len = 0; len < partAprime.length; len++) {
            if (!partA[len].equals(partAprime[len])) {
                errorCount++;
            }
        }
        System.out.println("Error rate-Brill's: " + 100 * (1.0 * errorCount / stringLength) + "%");

    }
}