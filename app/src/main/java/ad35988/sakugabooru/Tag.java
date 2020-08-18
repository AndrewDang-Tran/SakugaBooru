package ad35988.sakugabooru;

/**
 * Created by andrew on 11/29/16.
 */

public class Tag {
    public int tagId;
    public String name;
    public int count;
    public TagType type;

    public static enum TagType {
        GENERAL,
        ARTIST,
        COPYRIGHT,
        CHARACTER
    }

    public Tag(int id, String n, int c, int t) {
        tagId = id;
        name = n;
        count = c;
        if (t == 0) {
           type = TagType.GENERAL;
        } else if (t == 1) {
            type = TagType.ARTIST;
        } else if (t == 2) {
            type = TagType.COPYRIGHT;
        } else if (t == 3) {
            type = TagType.CHARACTER;
        }
    }

    public String toString() {
        return name;
    }
}
