package ad35988.sakugabooru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** * Created by andrew on 7/9/17.
 */

public class Trie {
  private class TrieNode {
    Character value;
    HashMap<Character, TrieNode> children;
    boolean isEndOfWord;

    public TrieNode() {
      children = new HashMap<Character, TrieNode>();
    }

    public TrieNode(Character v) {
      value = v;
      children = new HashMap<Character, TrieNode>();
    }
  }

  private TrieNode root;

  public Trie() {
    root = new TrieNode();
  }

  public void insert(String word) {
    HashMap<Character, TrieNode> children = root.children;
    int lastIndex = word.length() - 1;
    for (int i = 0; i < word.length(); i++) {
      Character c = word.charAt(i);
      TrieNode nextNode;
      if (children.containsKey(c)) {
        nextNode = children.get(c);
      } else {
        nextNode = new TrieNode();
        if (i == lastIndex) {
          nextNode.isEndOfWord = true;
          children.put(c, nextNode);
        }
      }
      children = nextNode.children;
    }
  }

  public ArrayList<String> search(String searchTerm) {
      TrieNode searchNode = travelToNode(searchTerm);
      return viableWords(searchNode);
  }

  public TrieNode travelToNode(String word) {
    TrieNode currentNode = root;
    for(int i = 0; i < word.length(); i++) {
      Character c = word.charAt(i);
      if (currentNode.children.containsKey(c)) {
        currentNode = currentNode.children.get(c);
      } else {
        return currentNode;
      }
    }
    return currentNode;
  }

  public ArrayList<String> viableWords(TrieNode lastSearchNode) {
    ArrayList<String> viableWords = new ArrayList<String>();
    viableWordsHelper(lastSearchNode, "", viableWords);
    return viableWords;
  }

  private void viableWordsHelper(TrieNode currentNode, String buildingWord, ArrayList<String> viableWords) {
      Iterator it = currentNode.children.entrySet().iterator();
      TrieNode nextNode;
      buildingWord += currentNode.value;
      if (currentNode.isEndOfWord)
        viableWords.add(buildingWord);
      while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        nextNode = (TrieNode) pair.getValue();
        viableWordsHelper(nextNode, buildingWord, viableWords);
        it.remove(); // avoids a ConcurrentModificationException
      }
  }
}
