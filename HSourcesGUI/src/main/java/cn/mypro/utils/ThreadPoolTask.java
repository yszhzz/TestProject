package cn.mypro.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ThreadPoolTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTask.class);

    protected DbName dbName;

    private ThreadPoolManger threadPoolManger;

    public void setDbName(DbName dbName) {
        this.dbName = dbName;
    }

    public DbName getDbName() {
        return dbName;
    }

    void setThreadPoolManger(ThreadPoolManger threadPoolManger) {
        this.threadPoolManger = threadPoolManger;
    }


    @Override
    public void run() {
        Thread.currentThread().setName(getThreadIdString() + " - " + getDbName().getName());
        logger.info("线程启动...");
        long startTime = System.currentTimeMillis();
        runTask();
        try {
            threadPoolManger.taskEnded(getDbName());
        } catch (InterruptedException e) {
            //
        }
        long costTime = System.currentTimeMillis() - startTime;
        logger.info("线程结束，耗时[{}]。", DateFormatterHelp.toDurationTimeString(costTime));
    }

    public abstract void runTask();

    public abstract String getThreadIdString();

}
