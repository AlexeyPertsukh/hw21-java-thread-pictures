package prog02_picture_bmp;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ForFile {
    private ForFile() {
    }


    public static String getFilenameWithAbsolutePatch(String localPatch, String fileName) {
        String path = new File(".").getAbsolutePath();
        return path + localPatch + fileName;
    }


    //файл существует?
    public static boolean isFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    //добавляет строку к имени файла, напр.: source.txt-> source_copy.txt
    public static String addFileName(String fileName, String addStr) {
        int num = -1;
        for (int i = fileName.length() - 1; i >= 0; i--) {
            if(fileName.charAt(i) == '.') {
                num = i;
                break;
            }
        }

        if(num == -1) {
            return fileName + addStr;
        }

        return fileName.substring(0, num) + addStr + fileName.substring (num);
    }

}
