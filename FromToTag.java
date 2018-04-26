import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FromToTag {

    String fromTag;
    String toTag;
    String previousTag;

    public FromToTag(String from, String to, String previous) {

        this.fromTag = from;
        this.toTag = to;
        this.previousTag = previous;
    }

    public String returnFromTag() {
        return this.fromTag;
    }

    public String returnToTag() {
        return this.toTag;
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
}
