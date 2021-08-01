package cn.mypro.swing.util.video;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.oro.text.regex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VideoAnalyticalUtil {
    private static Logger logger = LoggerFactory.getLogger(VideoAnalyticalUtil.class);

    public static Map<String,Object> analyticalWithXuggler(String path) {
        Map<String,Object> returnMap = null;
        if (path != null) {
            try {
                returnMap = new HashMap<>();

                IContainer container = IContainer.make();
                int result = container.open(path, IContainer.Type.READ, null);
                if (result < 0) {
                    logger.error("文件读取失败！");
                    return null;
                }
                IStream stream = container.getStream(0);
                IStreamCoder coder = stream.getStreamCoder();

                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    returnMap.put("resolution",coder.getHeight() + "*" + coder.getWidth());
                }
                returnMap.put("duration",container.getDuration() / 1000); //秒级
                returnMap.put("size",String.valueOf(container.getFileSize() / (1024 * 1024)));
                returnMap.put("bit",String.valueOf(container.getBitRate()));

                container.close();
            } catch (Exception e) {
                logger.error("视频解析出错！",e);
            }
        }
        return returnMap;
    }

    public static Map<String,Object> analyticalWithFFmpeg(String path) {

        Map<String,Object> returnMap = null;

        //ffmepg工具地址
        String ffmpegPath = "D:\\SoftWare\\WorkTool\\ffmpeg\\bin\\ffmpeg.exe";

        if (path != null) {

            returnMap = new HashMap<>();

            //视频文件地址
            String videoPath = "\"" + path + "\"";

            File file = new File(path);
            if (file.exists() && file.isFile()) {
                returnMap.put("size",String.valueOf(file.length() / (1024 * 1024)));
            }

            //拼接cmd命令语句
            StringBuffer buffer = new StringBuffer();
            buffer.append(ffmpegPath);
            //注意要保留单词之间有空格
            buffer.append(" -i ");
            buffer.append(videoPath);
            //执行命令语句并返回执行结果
            try {

                Process process = Runtime.getRuntime().exec(buffer.toString());
                InputStream in = process.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line ;
                while((line=br.readLine())!=null) {
                    System.out.println("读取数据："+line);
                    if(line.trim().startsWith("Duration:")){
                        //根据字符匹配进行切割
                        String duration = line.trim().substring(0,line.trim().indexOf(","));
                        String[] time = duration.split(" ")[1].trim().split("\\.")[0].split(":");

                        long duration_count = 0;
                        if (time.length == 3) {
                            duration_count = Integer.valueOf(time[0]) * 3600 + Integer.valueOf(time[1]) * 60 + Integer.valueOf(time[2]);
                            returnMap.put("duration",duration_count);
                        }

                        String bitRate = line.trim().split(",")[2].trim().split(" ")[1];
                        returnMap.put("bit",String.valueOf(duration_count * Integer.valueOf(bitRate)));

                    }
                    //一般包含fps的行就包含分辨率
                    if(line.contains("Stream") && line.contains("Video")){
                        //System.out.println(line);
                        //System.out.println(line);
                        if (line.contains("[")) {
                            String definition = line.split("\\[")[0];
                            String[] split = definition.trim().split(",");
                            String resolution = split[split.length-1].trim();
                            try {
                                if (resolution.contains("x")) {
                                    String[] xes = resolution.trim().split("x");
                                    for (String x : xes) {
                                        Integer.valueOf(x);
                                    }
                                    if (returnMap.containsKey("resolution")) {
                                        int oldR = Integer.valueOf(((String) returnMap.get("resolution")).split("x")[0]);
                                        int newR = Integer.valueOf(resolution.split("x")[0]);
                                        if (newR > oldR) returnMap.put("resolution",resolution);
                                    } else {
                                        returnMap.put("resolution",resolution);
                                    }
                                    //returnMap.put("resolution",resolution);
                                    //System.out.println("PUT："+resolution);
                                }
                            } catch (Exception e) {

                            }
                            //returnMap.put("resolution",split[split.length-1].trim());
                        } else {
                            if (line.contains("kb/s")) {
                                String[] split = line.split("kb/s")[0].trim().split(",");
                                String resolution = split[split.length-2].trim().split(" ")[0];
                                try {
                                    if (resolution.contains("x")) {
                                        String[] xes = resolution.trim().split("x");
                                        for (String x : xes) {
                                            Integer.valueOf(x);
                                        }
                                        returnMap.put("resolution",resolution);
                                        System.out.println("PUT："+resolution);
                                    }
                                } catch (Exception e) {

                                }
                            } else if (line.contains("fps")){
                                String[] split = line.split("fps")[0].trim().split(",");
                                String resolution = split[split.length-2].trim().split(" ")[0];
                                try {
                                    if (resolution.contains("x")) {
                                        String[] xes = resolution.trim().split("x");
                                        for (String x : xes) {
                                            Integer.valueOf(x);
                                        }
                                        returnMap.put("resolution",resolution);
                                        System.out.println("PUT："+resolution);
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                String[] split = line.split(",");
                                for (String s : split) {
                                    if (s.contains("x")) {
                                        boolean ok = true;
                                        String[] xes = s.trim().split("x");
                                        for (String x : xes) {
                                            try {
                                                Integer.valueOf(x);
                                            }catch (Exception e) {
                                                ok = false;
                                            }
                                        }

                                        if (ok) {
                                            returnMap.put("resolution",s.trim());
                                            break;
                                        }
                                    }
                                }
                            }
                        }


                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return returnMap;

    }

    public static Map<String,String> getEncodingFormat(String filePath) {
        String processFLVResult = processFLV(filePath);
        Map<String,String> retMap = new HashMap<>();
        if (StringUtils.isNotBlank(processFLVResult)) {
            PatternCompiler compiler = new Perl5Compiler();
            try {
                String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
                String regexVideo = "Video: (.*?), (.*?), (.*?)[,\\s]";
                String regexAudio = "Audio: (\\w*), (\\d*) Hz";

                Pattern patternDuration = compiler.compile(regexDuration, Perl5Compiler.CASE_INSENSITIVE_MASK);
                Perl5Matcher matcherDuration = new Perl5Matcher();
                if (matcherDuration.contains(processFLVResult, patternDuration)) {
                    MatchResult re = matcherDuration.getMatch();
                    retMap.put("提取出播放时间", re.group(1));
                    retMap.put("开始时间", re.group(2));
                    retMap.put("bitrate 码率 单位 kb", re.group(3));
                    System.out.println("提取出播放时间 ===" + re.group(1));
                    System.out.println("开始时间 =====" + re.group(2));
                    System.out.println("bitrate 码率 单位 kb==" + re.group(3));
                }

                Pattern patternVideo = compiler.compile(regexVideo, Perl5Compiler.CASE_INSENSITIVE_MASK);
                Perl5Matcher matcherVideo = new Perl5Matcher();

                if (matcherVideo.contains(processFLVResult, patternVideo)) {
                    MatchResult re = matcherVideo.getMatch();
                    retMap.put("编码格式", re.group(1));
                    retMap.put("视频格式", re.group(2));
                    retMap.put("分辨率", re.group(3));
                    System.out.println("编码格式 ===" + re.group(1));
                    System.out.println("视频格式 ===" + re.group(2));
                    System.out.println(" 分辨率 == =" + re.group(3));
                }

                Pattern patternAudio = compiler.compile(regexAudio, Perl5Compiler.CASE_INSENSITIVE_MASK);
                Perl5Matcher matcherAudio = new Perl5Matcher();

                if (matcherAudio.contains(processFLVResult, patternAudio)) {
                    MatchResult re = matcherAudio.getMatch();
                    retMap.put("音频编码", re.group(1));
                    retMap.put("音频采样频率", re.group(2));
                    System.out.println("音频编码 ===" + re.group(1));
                    System.out.println("音频采样频率 ===" + re.group(2));
                }
            } catch (MalformedPatternException e) {
                e.printStackTrace();
            }
        }
        return retMap;

    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    private static String processFLV(String inputPath) {
        List commend = new java.util.ArrayList();

        commend.add("D:\\SoftWare\\WorkTool\\ffmpeg\\bin\\ffmpeg");//可以设置环境变量从而省去这行
        commend.add("ffmpeg");
        commend.add("-i");
        commend.add(inputPath);

        try {

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.redirectErrorStream(true);
            Process p = builder.start();

//1. start
            BufferedReader buf = null; // 保存ffmpeg的输出结果流
            String line = null;
//read the standard output

            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuffer sb = new StringBuffer();
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
                continue;
            }
            int ret = p.waitFor();//这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行
//1. end
            return sb.toString();
        } catch (Exception e) {

            return null;
        }
    }




    public static void main(String[] args) {
        String path = "E:\\H Sources\\DealSourcePath\\HAVJ\\STARS-305\\STARS-305-CS-fbfb.me.mp4";
        //String path = "E:\\H Sources\\DealSourcePath\\HAVJ\\VEC-454\\VEC-454-CS-fbfb.me.mp4";
        Map<String, Object> ss = VideoAnalyticalUtil.analyticalWithFFmpeg(path);
        for (String s : ss.keySet()) {
            System.out.println(s + ":" + ss.get(s));
        }

    }
}

