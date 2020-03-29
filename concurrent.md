## [zookeeper图谱](https://www.processon.com/view/link/5b46f930e4b07b023103bcaf#map)
## [鲁班学院架构师图谱](https://www.processon.com/view/link/5db53025e4b0893e9a654cda#map)

## CountDownLatch
1. A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
   一种同步辅助，允许一个或多个线程等待，直到在在其他线程中一组操作被执行完成。
   
2. A {@code CountDownLatch} is initialized with a given <em>count</em>.
   CountDownLatch 使用给定count初始化。
   The {@link #await await} methods block until the current count reaches
   zero due to invocations of the {@link #countDown} method, after which
   all waiting threads are released and any subsequent invocations of
   {@link #await await} return immediately.  
   await方法将阻塞直到由于调用countDown方法而导致当前count达到0为止，此后所有等待线程都被释放，await的后续调用就会立即返回。

3. A {@code CountDownLatch} initialized with a count of one serves as a simple on/off latch, or gate: 
   all threads invoking {@link #await await} wait at the gate until it is opened by a thread invoking {@link#countDown}. 
   用一个计数初始化的CountDownLatch用于简单的 on/off 门闩 或 大门：所有调用await的线程都在大门等待直到countDown被一个线程打开为止。
   
   
   
   