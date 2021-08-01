package cn.mypro.swing.util.compress;

import cn.mypro.swing.constant.LabelConstant;
import cn.mypro.swing.dao.PublicMethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CompressionUtil {
    private Logger logger = LoggerFactory.getLogger(CompressionUtil.class);
    //7z a -t7z D:\Work\test.7z -p123 -mhe -m D:\Work\Base64Convert.exe
    //-mhe 加密文件名 -sdel 删除原文件 -m 指定加密方式 默认LZMA2
    private static final String pathOf7z = "D:\\SoftWare\\ComputerTool\\7z\\7-Zip\\7zG.exe";
    private ThreadPoolExecutor compressPool = null;
    private int MAX_THREADS = 5;
    private int ThreadPollQueueSize = 100;
    private CompressionUtil util = this;
    private int total_count = 0;
    private int batch_count = 0;
    private int success_count = 0;
    private static final int DEFAULT_CORE = 12;
    private static final Map<Integer,String> errorValueMap = new HashMap<>() {
        {
            put(0,"正常，没有错误");
            put(1,"警告，没有致命的错误，例如某些文件正在被使用，没有被压缩");
            put(2,"致命错误");
            put(7,"命令行错误");
            put(8,"没有足够的内存");
            put(255,"用户停止了操作");
        }
    };


    private JTextArea runMessage = null;
    private volatile boolean runLogountThread = false;

    public CompressionUtil() {
        initPool();
    }

    public CompressionUtil(JTextArea runMessage) {
        this.runMessage = runMessage;
        initPool();
        startRunMessageThread();
    }
    public CompressionUtil(JTextArea runMessage,boolean runCountThread) {
        this.runMessage = runMessage;
        this.runLogountThread = runCountThread;
        initPool();
        startRunMessageThread();
    }
    private void initPool() {
        compressPool = new ThreadPoolExecutor(MAX_THREADS,
                MAX_THREADS,
                Long.MAX_VALUE,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>(ThreadPollQueueSize),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                        try {
                            threadPoolExecutor.getQueue().put(runnable);
                            logger.info("压缩进程达到上限，已加入压缩队列！并行数为[{}]，现队列等待数量[{}]",MAX_THREADS,threadPoolExecutor.getQueue().size());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public boolean compressBy7z(String filePath, String zipPath, String model, boolean isFile, String password) {
        return compressBy7z(filePath,zipPath,model,isFile,password,DEFAULT_CORE);
    }
    public boolean compressBy7z(String filePath, String zipPath, String model, boolean isFile, String password,int core) {
        total_count ++;
        logger.info("开始压缩文件[{}]，压缩至[{}]，密码[{}]。",filePath,zipPath,password);
        Process process = null;
        int exitVal = 0;
        StringBuilder commandBuilder = new StringBuilder();
        String command = null;
        try {
            commandBuilder.append(pathOf7z).append(" a -t7z ").append("\""+zipPath+"\"");
            commandBuilder.append(" -mmt");
            //压缩后删除原文件
            if (password != null) commandBuilder.append(" -p").append(password).append(" -mhe").append(" -mmt=").append(core);
            if ("D".equals(model)) commandBuilder.append(" -sdel");
            if (!isFile) commandBuilder.append(" -r");
            commandBuilder.append(" ").append("\""+filePath+"\"");
            command = commandBuilder.toString();

            System.out.println(command);
            process = Runtime.getRuntime().exec(command);
            new RedirCmdStreamThread(process.getInputStream(), "INFO").start();
            new RedirCmdStreamThread(process.getErrorStream(),"ERR").start();
            exitVal = process.waitFor();
            /*
            * 0 ： 正常，没有错误；
                1 ： 警告，没有致命的错误，例如某些文件正在被使用，没有被压缩；
                2 ： 致命错误；
                7 ： 命令行错误；
                8 ： 没有足够的内存；
                255 ： 用户停止了操作；
            *
            * */
            if (exitVal != 0){
                logger.error("cmd任务执行失败,执行命令[{}]，返回原因[{}]",command,errorValueMap.get(exitVal));
                return false;
            }
            logger.info("压缩文件[{}]至[{}]成功！",filePath,zipPath);
        } catch (IOException | InterruptedException e) {
            logger.error("cmd任务执行出错,执行命令[{}]。",command.toString(),e);
            return false;
        }
        return true;
    }

    public boolean addCompressPool(String filePath,String zipPath,String model,boolean isFile,String password) {
        if(filePath == null || zipPath == null) {
            logger.error("路径存在错误！");
            return false;
        }

        batch_count ++;
        try {
            if (compressPool == null) initPool();
        } catch (Exception e) {
            logger.error("压缩池初始化错误！！",e);
            return false;
        }
        try {
            compressPool.execute(new Runnable() {
                @Override
                public void run() {
                    util.compressBy7z(filePath,zipPath,model,isFile,password,5);
                }
            });
            logger.info("已添加压缩命令至队列！文件[{}]，压缩至[{}]，密码[{}]。",filePath,zipPath,password);
        } catch (Exception e) {
            logger.error("添加压缩命令至队列失败！文件[{}]，压缩至[{}]，密码[{}]。",filePath,zipPath,password,e);
            return false;
        }
        return true;
    }

    public void close() {
        compressPool.shutdown();
        int size = compressPool.getQueue().size();
        if (size != 0) {
            logger.info("队列中仍存在[{}]个压缩命令未执行，等待执行完毕。",size);
        }
        while (compressPool.isTerminated()) {
            logger.info("所有压缩命令执行完毕，本次累计完成压缩[{}]个，成功[{}]个。",batch_count,success_count);
        }
        runLogountThread = false;
    }

    private void startRunMessageThread() {
        if (runLogountThread) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    logger.info("定时日志输出线程启动");
                    while (runLogountThread) {
                        try {
                            if (compressPool.getActiveCount() > 0 && runMessage != null) {
                                PublicMethodUtil.runMessagePrint(runMessage,"压缩正在运行中，活跃线程数：["+compressPool.getActiveCount()+"]，等待人物数：["+compressPool.getQueue().size()+"]\n",LabelConstant.TEXT_APPEND_MODEL_TIME_AND_NEXT);
                            }
                            TimeUnit.MINUTES.sleep(5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    logger.info("定时日志输出线程结束");
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        CompressionUtil compressionUtil = new CompressionUtil();
        compressionUtil.compressBy7z("G:\\A-MyFree\\Music\\Cloud","G:\\A-MyFree\\TR.7z","N",false,LabelConstant.password,4);
/*        for (int i = 0; i < 15; i++) {
            compressionUtil.addCompressPool("G:\\A-MyFree\\Music\\提琴","G:\\A-MyFree\\TEST-"+i+".7z","N",false,LabelConstant.password);
        }*/
    }


}

class RedirCmdStreamThread extends Thread {
    InputStream is;
    String printType;

    RedirCmdStreamThread(InputStream is, String printType) {
        this.is = is;
        this.printType = printType;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                System.out.println(printType + ">" + line);
            }

        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}