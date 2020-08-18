package ad35988.sakugabooru;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by andrew on 10/20/16.
 */

public class Post {

    public enum MediaType {
        MP4("MP4"),
        JPG("JPG"),
        PNG("PNG"),
        GIF("GIF"),
        WEBM("WEBM");

        private String name;

        MediaType(String n) {
            name = n;
        }

        String getName() {
            return name;
        }
    }

    public int id;
    public URL videoUrl;
    public URL previewUrl;
    public HashMap<String, String> tags;
    public String showTags;
    public String artist;
    public int score;
    public String previewBitmapKey;
    public MediaType mediaType;

    /**
     * Post constructor
     * @param i
     * @param videoString
     * @param previewString
     * @param tagList
     * @param s
     */
    public Post(int i, String videoString, String previewString, HashMap<String, String> tagList, int s) {
        id = i;
        try {
            videoUrl = new URL(videoString);
            previewUrl = new URL(previewString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        tags = tagList;
        showTags = tagsToString();
        score = s;
        previewBitmapKey = previewString;
        String extension = FileUtils.getExtension(videoString);
        assignMediaType(extension);
    }

    public void findArtist(HashMap<String, String> artistsMap) {
        Iterator it = tags.entrySet().iterator();
        String currentTag = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            currentTag = (String) pair.getValue();
            if (artistsMap.containsKey(currentTag)) {
                artist = artistsMap.get(currentTag);
                return;
            }
        }
    }

    /**
     * takes extension with period and assigns the posts media type
     * @param extension
     * @return
     */
    private void assignMediaType(String extension) {
        if(extension.equals("mp4")) {
            mediaType = MediaType.MP4;
        } else if(extension.equals("jpg")) {
            mediaType = MediaType.JPG;
        } else if(extension.equals("png")) {
            mediaType = MediaType.PNG;
        } else if(extension.equals("gif")) {
            mediaType = MediaType.GIF;
        } else if(extension.equals("webm")) {
            mediaType = MediaType.WEBM;
        }
    }

    /**
     * Iterate through tags hashmap to create a string of all the tags
     * @return
     */
    private String tagsToString() {
        String tagsString = "";
        Iterator it = tags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            tagsString += pair.getValue() + " ";
        }
        return tagsString;
    }

    public static final Comparator<Post> sortByNew = new Comparator<Post>() {
        @Override
        public int compare(Post p1, Post p2) {
            boolean greater = p1.id > p2.id;
            boolean lesser = p1.id < p2.id;
            if (greater) {
               return -1;
            } else if(lesser) {
                return 1;
            }
            return 0;
        }
    };

    public static final Comparator<Post> sortByScore = new Comparator<Post>() {
        @Override
        public int compare(Post p1, Post p2) {
            boolean greater = p1.score > p2.score;
            boolean lesser = p1.score < p2.score;
            if (greater) {
                return -1;
            } else if(lesser) {
                return 1;
            }
            return 0;
        }
    };
}
