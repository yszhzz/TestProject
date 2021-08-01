package cn.mypro.test;

import it.sauronsoftware.jave.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class TestFFMPEG {

    private static Logger logger = LoggerFactory.getLogger(TestFFMPEG.class);

    public static void test(String path) throws Exception {
        Encoder encoder = new Encoder();
        File file = new File(path);
        //InputStream inputStream =  new FileInputStream(file);
        //FileUtils.copyInputStreamToFile(inputStream, file);
        MultimediaInfo encoderInfo = encoder.getInfo(file);
        //视频播放时长
        long duration = encoderInfo.getDuration();
        logger.debug("视频播放时长:{}秒", duration / 1000);
        //多媒体文件格式名称
        String encoderInfoFormat = encoderInfo.getFormat();
        logger.debug("多媒体文件格式名称:{}", encoderInfoFormat);
        //音频 返回一组特定于音频的信息。如果为空，则多媒体文件中没有音频*流。
        AudioInfo audio = encoderInfo.getAudio();
        if (audio != null) {
            //音频流解码器名称
            String audioDecoder = audio.getDecoder();
            logger.debug("音频流解码器名称:{}", audioDecoder);
        }
        //视频
        VideoInfo videoInfo = encoderInfo.getVideo();
        if (videoInfo == null) {
            throw new RuntimeException("多媒体文件中没有视频流");
        }
        //视频流解码器名称
        String videoInfoDecoder = videoInfo.getDecoder();
        logger.debug("视频流解码器名称:{}", videoInfoDecoder);
        //返回视频大小。如果为空，则此信息不可用
        VideoSize videoInfoSize = videoInfo.getSize();
        if (videoInfoSize == null) {
            throw new RuntimeException("视频分辨率获取失败");
        }
        //视频高度
        int height = videoInfoSize.getHeight();
        //视频宽度
        int width = videoInfoSize.getWidth();
        logger.debug("视频高度:{},视频宽度:{}",height,width);
    }

    public static void main(String[] args) throws Exception {
        String path = "E:\\H Sources\\DealSourcePath\\HAVJ\\VEC-448\\VEC-448-C.mp4";
        TestFFMPEG.test(path);
    }
}

