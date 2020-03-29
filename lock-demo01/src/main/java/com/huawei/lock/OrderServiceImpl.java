package com.huawei.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xixi
 * @Description： 订单服务类
 * @create 2020/3/29
 * @since 1.0.0
 */
public class OrderServiceImpl implements Runnable {

    private static OrderCodeGenerator ong = new OrderCodeGenerator();

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    // 同时并发线程数
    private static final int NUM = 10;
    // 按照线程数初始化倒计数器，倒计数器
    private static CountDownLatch cdl = new CountDownLatch(NUM);

    // jdk （可重入）锁
//    private static Lock lock = new ReentrantLock();
    // zookeeper 实现的锁
//    private static Lock lock = new ZookeeperLock();

    private static ZookeeperImproveLock lock = new ZookeeperImproveLock();

    // 非线程安全的
    /*
    public void createOrder(){
        String orderCode = null;

        // 获取订单编号
        orderCode = ong.getOrderCode();

        // .... 业务代码

        logger.info(Thread.currentThread().getName() + "===========" + orderCode);
    }
    */

    // jdk 锁实现 （ 单个JVM有效）
    public void createOrder(){
        String orderCode = null;

        lock.lock();
        try {

            // 获取订单编号
            System.out.println(Thread.currentThread().getName() + "进来...");
            orderCode = ong.getOrderCode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        // .... 业务代码

        System.out.println(Thread.currentThread().getName() + "===========" + orderCode);
    }

    public void run() {
        try {
            // 等待其他线程初始化
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 创建订单
        createOrder();
    }


    public static void main(String[] args) {
        for (int i = 0; i < NUM; i++) {
            new Thread(new OrderServiceImpl()).start();
            // 创建一个线程，倒计数器减1
            cdl.countDown();
        }
    }

}
