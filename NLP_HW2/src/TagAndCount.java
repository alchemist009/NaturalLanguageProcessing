import java.util.Objects;

public class TagAndCount {

    String tag;
    Integer count;

    public TagAndCount(String tag, Integer count) {
        this.tag = tag;
        this.count = count;
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
        final TagAndCount other = (TagAndCount) obj;
        if (!Objects.equals(this.tag, other.tag)) {
            return false;
        }
        if (!Objects.equals(this.count, other.count)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.tag);
        hash = 37 * hash + Objects.hashCode(this.count);
        return hash;
    }
}
