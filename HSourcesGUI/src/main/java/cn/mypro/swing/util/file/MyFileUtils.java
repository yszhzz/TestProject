package cn.mypro.swing.util.file;

import cn.mypro.swing.constant.LabelConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MyFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(MyFileUtils.class);

    public static byte[] getBytesFromFile(File cntIdImg) throws IOException {
        if (cntIdImg == null) return null;
        logger.info("Read Picture :"  + cntIdImg.getCanonicalPath());
        FileInputStream is = new FileInputStream(cntIdImg);
        long length = cntIdImg.length();
        byte[] idImgBytes = new byte[(int) length];
        is.read(idImgBytes);
        is.close();
        return idImgBytes;
    }

    public static void downloadImgByNet2(String imgSrc, String filePath, String fileName) {
        if (imgSrc == null) return;
        logger.info("Down Picture :"  + imgSrc);
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
            //System.out.println("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean downloadImgByNet(String imgSrc, String filePath, String fileName) {
        if (imgSrc == null) return false;
        if (!imgSrc.startsWith("http")) return false;
        logger.info("Down Picture :"  + imgSrc);
        try {
            URL url = new URL(imgSrc);
            URLConnection conn = url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(20 * 1000);

            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //输出流
            InputStream str = conn.getInputStream();
            //控制流的大小为1k
            byte[] bs = new byte[1024 * 10];
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

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, Object> getImgByteByNet(String imgSrc) {

        Map<String, Object> returnMap = new HashMap<>(){
            {
                put(LabelConstant.RETURN_ANSWER_SUCCESS,false);
            }
        };

        if (imgSrc == null) return returnMap;
        logger.info("Load Picture :"  + imgSrc);

        try {
            URL url = new URL(imgSrc);
            URLConnection conn = url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(20*1000);
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
            returnMap.put(LabelConstant.RETURN_ANSWER_SUCCESS,true);
            returnMap.put(LabelConstant.RETURN_ANSWER_OBJECT,in2b);
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnMap;
    }

    public static void main(String[] args) {
        Map<String, Object> imgByteByNet = MyFileUtils.getImgByteByNet("https://m1.xslist.org/gallery/0/260/1576399312.jpg");
        System.out.println(((byte[])imgByteByNet.get(LabelConstant.RETURN_ANSWER_OBJECT)).length);
    }

}
