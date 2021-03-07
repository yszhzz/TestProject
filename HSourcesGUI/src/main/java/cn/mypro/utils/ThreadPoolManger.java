package cn.mypro.utils;

import cn.mypro.utils.exception.ThreadPoolInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

public class ThreadPoolManger {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManger.class);

    private static final String CURRENT_DIR = System.getProperty("user.dir");

    private static final String MAX_THREAD_CONFIG_ITEM = "max_thread_count";

    private static final String DB_NAME_LIST_CONFIG_ITEM = "db_name_list";

    private static final String MANUL_SHUTDOWN_CONFIG_ITEM = "manul_shutdown";

    private static final int MAX_MAX_TREAD = 200;

    private int maxThread;

    private Map<DbName, Integer> currentDbNameMap = new ConcurrentHashMap<>();

    private Map<DbName, Integer> shrinkingDbNameMap = new ConcurrentHashMap<>();

    private int shrinkingMaxThread = 0;

    private boolean shrinkingLocked = false;

    private boolean needShrinkingMaxThread = false;

    private ReentrantLock lock = new ReentrantLock();
    /**
     * 配置文件名称
     */
    private String configFileName;

    private Properties currentProperties;

    /**
     * 需调整的连接池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 需调整的数据库名队列
     */
    private LinkedBlockingQueue<DbName> dbNameLinkedBlockingQueue;

    public ThreadPoolManger(String configFileName) throws ThreadPoolInitException {
        this.configFileName = configFileName;
        Properties properties;
        try {
            properties = loadConfigFileProperties();
        } catch (IOException e) {
            throw new ThreadPoolInitException(e);
        }
        currentProperties = properties;
        List<DbName> dbNameList = getDbNameListFromProperties(properties);
        if (dbNameList.size() == 0) {
            throw new ThreadPoolInitException("未提供有效的数据库节点列表");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (DbName dbName : dbNameList) {
            stringBuilder.append(dbName.getName()).append("; ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        try {
            maxThread = Integer.valueOf(properties.getProperty(MAX_THREAD_CONFIG_ITEM));
        } catch (NumberFormatException e) {
            throw new ThreadPoolInitException("未提供有效的最大线程数");
        }
        threadPoolExecutor = new ThreadPoolExecutor(maxThread,
                maxThread,
                Long.MAX_VALUE,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>());
        dbNameLinkedBlockingQueue = new LinkedBlockingQueue<>(MAX_MAX_TREAD);
        for (int i = 0; i < maxThread; i++) {
            try {
                DbName dbName = dbNameList.get(i % dbNameList.size());
                dbNameLinkedBlockingQueue.put(dbName);
                currentDbNameMap.compute(dbName, addBiFuction);
            } catch (InterruptedException e) {
                //
            }
        }
        watchConfigThread.start();
        logger.info("线程池初始化成功，当前最大连接数为[{}]，使用的数据库节点为[{}]...", maxThread, stringBuilder.toString());
    }

    public synchronized boolean executeTask(ThreadPoolTask threadPoolTask) throws InterruptedException {
        if (threadPoolExecutor.isShutdown()) {
            return false;
        }
        DbName dbName = dbNameLinkedBlockingQueue.take();
        threadPoolTask.setDbName(dbName);
        threadPoolTask.setThreadPoolManger(this);
        String index = threadPoolTask.getThreadIdString();
        try {
            threadPoolExecutor.execute(threadPoolTask);
        } catch (RejectedExecutionException e) {
            logger.error("线程[{}]在数据库节点[{}]上的任务被拒绝执行！", index, dbName.getName());
            logger.error("错误：", e);
            return false;
        }
        return true;
    }

    void taskEnded(DbName dbName) throws InterruptedException {
        Integer n = shrinkingDbNameMap.computeIfPresent(dbName, (k, v) -> {
            if (v == 0) {
                return null;
            } else {
                return v - 1;
            }
        });
        if (n == null) {
            dbNameLinkedBlockingQueue.put(dbName);
            logger.info("数据库节点已放回队列...");
        } else {
            currentDbNameMap.compute(dbName, subBiFuction);
            logger.info("数据库节点已被收回！");
        }
        lock.lock();
        if (shrinkingLocked && shrinkingDbNameMap.isEmpty()) {
            maxThread = maxThread - shrinkingMaxThread;
            shrinkingMaxThread = 0;
            logger.info("线程池收缩完毕！最大线程数已调整为：[{}]，正在运行线程数为：[{}]。", maxThread, threadPoolExecutor.getActiveCount());
            shrinkingLocked = false;
            needShrinkingMaxThread = true;
        } else if (needShrinkingMaxThread) {
            needShrinkingMaxThread = false;
            threadPoolExecutor.setCorePoolSize(maxThread);
            threadPoolExecutor.setMaximumPoolSize(maxThread);
            logger.info("最大线程数已调整为：[{}]，正在运行线程数为：[{}]。", maxThread, threadPoolExecutor.getActiveCount());
        }
        lock.unlock();
    }

    public void threadPoolExecutorShutdown() throws InterruptedException {
        watchConfigThread.interrupt();
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    public static void main(String[] args) throws ThreadPoolInitException, InterruptedException {
        System.out.println(CURRENT_DIR);
        ThreadPoolManger threadPoolManger = new ThreadPoolManger("threadconfig.properties");
        threadPoolManger.threadPoolExecutorShutdown();
    }

    private void configFileChanged(Properties properties) throws InterruptedException {
        String manulShutDown = properties.getProperty(MANUL_SHUTDOWN_CONFIG_ITEM);
        if ((!StringUtils.isEmpty(manulShutDown)) && manulShutDown.toUpperCase().equals("TRUE")) {
            threadPoolExecutorShutdown();
        }
        if (shrinkingLocked || needShrinkingMaxThread) {
            logger.info("上一次修改未生效完成，本次修改被忽略！");
            return;
        }
        String dbNameString = properties.getProperty(DB_NAME_LIST_CONFIG_ITEM);
        String maxThreadString = properties.getProperty(MAX_THREAD_CONFIG_ITEM);
        if (dbNameString.equals(currentProperties.getProperty(DB_NAME_LIST_CONFIG_ITEM)) && maxThreadString.equals(currentProperties.getProperty(MAX_THREAD_CONFIG_ITEM))) {
            logger.info("本次文件修改属性未变，本次修改被忽略！");
            return;
        }
        shrinkingLocked = true;
        currentProperties = properties;
        logger.info("配置文件已修改\n[{}]:[{}]\n[{}]:[{}]", DB_NAME_LIST_CONFIG_ITEM, dbNameString, MAX_THREAD_CONFIG_ITEM, maxThreadString);
        int maxThreadReaded = Integer.valueOf(maxThreadString);
        if (!StringUtils.isEmpty(dbNameString)) {
            String[] dbNameStringArray = dbNameString.split(";");
            Set<DbName> dbNameSet = new HashSet<>();
            for (String dbName : dbNameStringArray) {
                DbName dbNameEnum = getDbName(dbName);
                if (dbNameEnum != null) {
                    dbNameSet.add(dbNameEnum);
                }
            }
            List<DbName> dbNameListReaded = new ArrayList<>(dbNameSet);
            Map<DbName, Integer> dbNameIntegerMap = new HashMap<>();
            for (int i = 0; i < maxThreadReaded; i++) {
                DbName dbName = dbNameListReaded.get(i % dbNameListReaded.size());
                Integer n = dbNameIntegerMap.get(dbName);
                n = n == null ? 1 : n + 1;
                dbNameIntegerMap.put(dbName, n);
            }
            for (DbName dbName : currentDbNameMap.keySet()) {
                Integer current = currentDbNameMap.get(dbName);
                Integer target = dbNameIntegerMap.get(dbName);
                if (current < target) {
                    for (int i = 0; i < target - current; i++) {
                        currentDbNameMap.compute(dbName, addBiFuction);
                        dbNameLinkedBlockingQueue.put(dbName);
                    }
                    maxThread = maxThread + target - current;
                }
                if (current > target) {
                    shrinkingDbNameMap.put(dbName, current - target);
                    shrinkingMaxThread = shrinkingMaxThread + current - target;
                    // System.out.println(String.format("%s : %d : %d", dbName.getName(), current - target, shrinkingMaxThread));
                }
                dbNameIntegerMap.remove(dbName);
            }
            for (DbName dbName : dbNameIntegerMap.keySet()) {
                Integer target = dbNameIntegerMap.get(dbName);
                for (int i = 0; i < target; i++) {
                    currentDbNameMap.compute(dbName, addBiFuction);
                    dbNameLinkedBlockingQueue.put(dbName);
                }
                maxThread = maxThread + target;
            }
            threadPoolExecutor.setCorePoolSize(maxThread);
            threadPoolExecutor.setMaximumPoolSize(maxThread);
            logger.info("最大线程数已调整为：[{}]，正在运行线程数为：[{}]。", maxThread, threadPoolExecutor.getActiveCount());
        }
        if (shrinkingDbNameMap.isEmpty()) {
            shrinkingLocked = false;
        }
    }

    public int getActiveThreadCount() {
        return threadPoolExecutor.getActiveCount();
    }

    private List<DbName> getDbNameListFromProperties(Properties properties) {
        List<DbName> dbNameList = new ArrayList<>();
        String dbNameStrings = properties.getProperty(DB_NAME_LIST_CONFIG_ITEM);
        if (!StringUtils.isEmpty(dbNameStrings)) {
            String[] dbNameStringArray = dbNameStrings.split(";");
            for (String dbNameString : dbNameStringArray) {
                DbName dbName = getDbName(dbNameString);
                if (dbName != null) {
                    dbNameList.add(dbName);
                }
            }
        }
        return dbNameList;
    }

    private Properties loadConfigFileProperties() throws IOException {
        File configFile = new File(CURRENT_DIR, configFileName);
        Properties properties = new Properties();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
        properties.load(bufferedReader);
        return properties;
    }

    private Thread watchConfigThread = new Thread(new Runnable() {
        @Override
        public void run() {
            WatchService watchService = null;
            try {
                Path path = Paths.get(CURRENT_DIR);
                watchService = FileSystems.getDefault().newWatchService();
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                //noinspection InfiniteLoopStatement
                while (true) {
                    try {
                        WatchKey watchKey = watchService.take();
                        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                        for (WatchEvent<?> event : watchEvents) {
                            if (((Path) event.context()).getFileName().toString().equals(configFileName)) {
                                File configFile = new File(CURRENT_DIR, configFileName);
                                waitForReady(configFile);
                                Properties properties = loadConfigFileProperties();
                                configFileChanged(properties);
                            }
                        }
                        watchKey.reset();
                    } catch (Exception e) {
                        if (e instanceof InterruptedException) {
                            break;
                        }
                        logger.error("配置文件监听服务发生错误。", e);
                    }
                }
            } catch (IOException e) {
                logger.error("配置文件监听线程发生错误，线程已退出。", e);
            } finally {
                DataBaseUtils.closeQuietly(watchService);
            }
        }
    });

    private DbName getDbName(String name) {
        for (DbName dbName : DbName.values()) {
            if (dbName.getName().toUpperCase().equals(name.toUpperCase())) {
                return dbName;
            }
        }
        return null;
    }

    private static void waitForReady(File file) {
        while (true) {
            try (RandomAccessFile ignored = new RandomAccessFile(file, "rws")) {
                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    //
                }
            }
        }
    }

    private BiFunction<DbName, Integer, Integer> addBiFuction = (dbName, n) -> n == null ? 1 : n + 1;
    private BiFunction<DbName, Integer, Integer> subBiFuction = (dbName, n) -> n > 0 ? n - 1 : null;
}
