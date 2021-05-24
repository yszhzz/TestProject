package cn.mypro.swing.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {

    public static byte[] getBytesFromFile(File cntIdImg) throws IOException {
        FileInputStream is = new FileInputStream(cntIdImg);
        long length = cntIdImg.length();
        byte[] idImgBytes = new byte[(int) length];
        is.read(idImgBytes);
        is.close();
        return idImgBytes;

    }


}
