package com.huawei.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author xixi
 * @Description：
 * @create 2020/3/29
 * @since 1.0.0
 */
public class ZookeeperImproveLock implements Lock {
    private Logger logger = LoggerFactory.getLogger(ZookeeperImproveLock.class);

    private static final String ZK_IP_PORT = "192.168.2.148:2181,192.168.2.148:2182,192.168.2.148:2183";
    private static final String LOCK_PATH = "/LOCK";

    private ZkClient client = new ZkClient(ZK_IP_PORT, 1000, 1000, new SerializableSerializer());

    private CountDownLatch cdl;

    private String beforePath; // 当前请求的前一个节点路径
    private String currentPath; // 当前请求的节点路径

    public ZookeeperImproveLock(){
        if(!this.client.exists(LOCK_PATH)){
            this.client.createPersistent(LOCK_PATH);
        }
    }

    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        }else{
            System.out.println(Thread.currentThread().getName() + "获取分布式锁！");
        }
    }

    /**
     * 阻塞方法
     */
    private void waitForLock() {
        IZkDataListener listener = new IZkDataListener() {
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("--------------trigger watch event-------------");
                if (cdl != null) {
                    cdl.countDown();
                }
            }
        };

        this.client.subscribeDataChanges(beforePath, listener);

        if (this.client.exists(beforePath)) {
            cdl = new CountDownLatch(1);
            try {
                cdl.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.client.unsubscribeDataChanges(beforePath, listener);
    }

    public boolean tryLock() {
        try {
            // 若 currentPath 为空，则第一次加锁
            if (currentPath == null || currentPath.length() <= 0) {
                // 创建一个临时顺序节点
                currentPath = this.client.createEphemeralSequential(LOCK_PATH + "/", "lock");
                System.out.println("---------------->" + currentPath);
            }

            // 获取所有临时节点并排序，临时节点为自增长的字符串，如：0000000002
            List<String> childrens = this.client.getChildren(LOCK_PATH);
            Collections.sort(childrens);

            // 如当前节点在所有子节点中排最小，则获取锁成功
            if(currentPath.equals(LOCK_PATH + "/" + childrens.get(0))){
                return true;
            }else{
                // 如当前节点在所有子节点中不是排最小，则获取它前面的节点路径赋值给变量 beforePath（为了处理当前节点能监控前一个节点使用）
                int wz = Collections.binarySearch(childrens, currentPath.substring(6));
                beforePath = LOCK_PATH + "/" + childrens.get(wz - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unlock() {
        this.client.delete(currentPath);
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public Condition newCondition() {
        return null;
    }
}
