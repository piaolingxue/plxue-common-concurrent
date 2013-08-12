package com.plxue.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * 示例用法：下面给出了两个类，其中一组 worker 线程使用了两个倒计数锁存器：
 * 
 * 第一个类是一个启动信号，在 driver 为继续执行 worker 做好准备之前，它会阻止所有的 worker 继续执行。 第二个类是一个完成信号，它允许 driver 在完成所有 worker
 * 之前一直等待。
 */
public class CountDownLatchDemo1 {
    private static final Integer N = 10;

    class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        private final int threadId;

        Worker(int num, CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.threadId = num;
        }

        public void run() {
            try {
                startSignal.await();
                doWork();
                doneSignal.countDown();
            } catch (InterruptedException ex) {} // return;
        }

        void doWork() {
            System.out.println(String.format("%d work", this.threadId));
        }
    }

    class Driver {
        public void process() throws InterruptedException {
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch doneSignal = new CountDownLatch(N);

            for (int i = 0; i < N; ++i)
                // create and start threads
                new Thread(new Worker(i, startSignal, doneSignal)).start();

            // do something else
            // ...
            startSignal.countDown(); // let all threads proceed
            // do something else
            // ...
            doneSignal.await(); // wait for all to finish
        }
    }



    public static void main(String[] args) throws InterruptedException {
        new CountDownLatchDemo1().new Driver().process();
    }
}
