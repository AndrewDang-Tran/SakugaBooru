package ad35988.sakugabooru;

/**
 * Created by andrew on 11/8/16.
 */

public class FileUtils {
    public static String getExtension(String filePath) {
        if (filePath == null || filePath.length() <= 0) {
           return "";
        }
        int indexOfPeriod = filePath.lastIndexOf(".");
        return filePath.substring(indexOfPeriod + 1);
    }
}
