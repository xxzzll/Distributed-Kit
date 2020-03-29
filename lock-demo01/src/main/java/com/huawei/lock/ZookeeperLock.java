package com.huawei.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ZookeeperLock implements Lock {
    private Logger logger = LoggerFactory.getLogger(ZookeeperLock.class);

    private static final String ZK_IP_PORT = "172.17.0.3:2181";
    private static final String LOCK_NODE = "/LOCK";

    private ZkClient client = new ZkClient(ZK_IP_PORT, 1000, 1000);

    private CountDownLatch cdl;

    public void lock() {
        if (tryLock()) {
            return;
        }
        // 等待加锁
        waitForLock();
        // 竞争加锁
        lock();
    }

    /**
     * 阻塞方法
     */
    private void waitForLock() {
        IZkDataListener listener = new IZkDataListener() {

            public void handleDataChange(String dataPath, Object data) throws Exception {

            }


            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("----------------capture zk node are deleted-----------------");
                // 释放门闩
                if(cdl != null){
                    cdl.countDown();
                }
            }
        };

        // add watch to node point
        this.client.subscribeDataChanges(LOCK_NODE, listener);

        if (this.client.exists(LOCK_NODE)) {
            cdl = new CountDownLatch(1);
            try {
                // 阻塞等待
                cdl.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // delete watch to node point
        this.client.unsubscribeDataChanges(LOCK_NODE, listener);
    }


    public boolean tryLock() {
        try {
            this.client.createPersistent(LOCK_NODE);
            return true;
        } catch (ZkNodeExistsException e) {
            return false;
        }
    }


    public void unlock() {
        this.client.delete(LOCK_NODE);
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
