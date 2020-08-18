package ad35988.sakugabooru;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by andrew on 10/25/16.
 */

public class RequestParser {

    public static ArrayList<Post> parsePostsRequest(String response) {
        ArrayList<Post> results = new ArrayList<Post>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return results;
        }
        StringBuilder xmlStringBuilder = new StringBuilder(response);
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return results;
        }
        Document doc = null;
        try {
            doc = builder.parse(input);
        } catch (SAXException e) {
            e.printStackTrace();
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return results;
        }
        NodeList postList = doc.getElementsByTagName("post");
        Post newPost;
        Element postElement;
        Node postNode;
        for (int i = 0; i < postList.getLength(); i++) {
            postNode = postList.item(i);
            if (postNode.getNodeType() == Node.ELEMENT_NODE) {
                postElement = (Element) postNode;
                String tagsString = postElement.getAttribute("tags");
                String videoUrlString = postElement.getAttribute("file_url");
                String previewUrlString = postElement.getAttribute("preview_url");
                int score = Integer.parseInt(postElement.getAttribute("score"));
                int postId = Integer.parseInt(postElement.getAttribute("id"));
                HashMap<String,String> tags = parseTags(tagsString);
                newPost = new Post(postId, videoUrlString, previewUrlString, tags, score);
                results.add(newPost);
            }
        }
        return results;
    }

    private static HashMap<String, String> parseTags(String tagsString) {
        HashMap<String,String> tags = new HashMap<String, String>();
        Scanner scanner = new Scanner(tagsString);
        while(scanner.hasNext()) {
            String tag = scanner.next();
            tags.put(tag, tag);
        }
        return tags;
    }

    public static ArrayList<Tag> parseTagListRequest(String response) {
        ArrayList<Tag> results = new ArrayList<Tag>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return results;
        }
        StringBuilder xmlStringBuilder = new StringBuilder(response);
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return results;
        }
        Document doc = null;
        try {
            doc = builder.parse(input);
        } catch (SAXException e) {
            e.printStackTrace();
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return results;
        }
        NodeList tagList = doc.getElementsByTagName("tag");
        Tag newTag;
        Element tagElement;
        Node tagNode;
        for (int i = 0; i < tagList.getLength(); i++) {
            tagNode = tagList.item(i);
            if (tagNode.getNodeType() == Node.ELEMENT_NODE) {
                tagElement = (Element) tagNode;
                String tagsString = tagElement.getAttribute("tags");
                int tagId = Integer.parseInt(tagElement.getAttribute("id"));
                String tagName = tagElement.getAttribute("name");
                int tagCount = Integer.parseInt(tagElement.getAttribute("count"));
                int tagType = Integer.parseInt(tagElement.getAttribute("type"));
                newTag = new Tag(tagId, tagName, tagCount, tagType);
                results.add(newTag);
            }
        }
        return results;
    }

    public static HashMap<String, String> parseArtistsRequest(String response) {
        HashMap<String, String> artists = new HashMap<String, String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return artists;
        }
        StringBuilder xmlStringBuilder = new StringBuilder(response);
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return artists;
        }
        Document doc = null;
        try {
            doc = builder.parse(input);
        } catch (SAXException e) {
            e.printStackTrace();
            return artists;
        } catch (IOException e) {
            e.printStackTrace();
            return artists;
        }
        NodeList artistList = doc.getElementsByTagName("artist");
        String newArtist;
        Element artistElement;
        Node artistNode;
        for (int i = 0; i < artistList.getLength(); i++) {
            artistNode = artistList.item(i);
            if (artistNode.getNodeType() == Node.ELEMENT_NODE) {
                artistElement = (Element) artistNode;
                newArtist = artistElement.getAttribute("name");
                artists.put(newArtist, newArtist);
            }
        }
        return artists;
    }

    public static Post parseRandomPostRequest(String response) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder xmlStringBuilder = new StringBuilder(response);
        ByteArrayInputStream input = null;
        try {
            input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        Document doc = null;
        try {
            doc = builder.parse(input);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        NodeList postList = doc.getElementsByTagName("post");
        Post randomPost = null;
        Element postElement;
        Node postNode;

        Random random = new Random();
        int randomIndex = random.nextInt(postList.getLength());
        postNode = postList.item(randomIndex);
        if (postNode.getNodeType() == Node.ELEMENT_NODE) {
            postElement = (Element) postNode;
            String tagsString = postElement.getAttribute("tags");
            String videoUrlString = postElement.getAttribute("file_url");
            String previewUrlString = postElement.getAttribute("preview_url");
            int score = Integer.parseInt(postElement.getAttribute("score"));
            int postId = Integer.parseInt(postElement.getAttribute("id"));
            HashMap<String,String> tags = parseTags(tagsString);
            randomPost = new Post(postId, videoUrlString, previewUrlString, tags, score);
        }
        return randomPost;
    }
}
