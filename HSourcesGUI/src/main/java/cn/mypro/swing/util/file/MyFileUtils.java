package cn.mypro.swing.util.file;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class MyFileUtils {

    public static byte[] getBytesFromFile(File cntIdImg) throws IOException {
        FileInputStream is = new FileInputStream(cntIdImg);
        long length = cntIdImg.length();
        byte[] idImgBytes = new byte[(int) length];
        is.read(idImgBytes);
        is.close();
        return idImgBytes;

    }

    public static void downloadImgByNet(String imgSrc, String filePath, String fileName) {
        if (imgSrc == null) return;

        try {
            URL url = new URL(imgSrc);
            URLConnection conn = url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //输出流
            InputStream str = conn.getInputStream();
            //控制流的大小为1k
            byte[] bs = new byte[1024];
            //读取到的长度
            int len = 0;
            //是否需要创建文件夹
            File saveDir = new File(filePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            //实例输出一个对象
            FileOutputStream out = new FileOutputStream(file);
            //循环判断，如果读取的个数b为空了，则is.read()方法返回-1，具体请参考InputStream的read();
            while ((len = str.read(bs)) != -1) {
            //将对象写入到对应的文件中
                out.write(bs, 0, len);
            }
            //刷新流
            out.flush();
            //关闭流
            out.close();
            str.close();
            System.out.println("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getImgByteByNet(String imgSrc) {
        if (imgSrc == null) return null;

        try {
            URL url = new URL(imgSrc);
            URLConnection conn = url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //输出流
            InputStream str = conn.getInputStream();
            //控制流的大小为1k
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = str.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();

            //关闭流
            swapStream.close();
            str.close();
            return in2b;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        byte[] imgByteByNet = MyFileUtils.getImgByteByNet("https://m1.xslist.org/gallery/0/260/1576399312.jpg");
        System.out.println(imgByteByNet.length);
    }

}
