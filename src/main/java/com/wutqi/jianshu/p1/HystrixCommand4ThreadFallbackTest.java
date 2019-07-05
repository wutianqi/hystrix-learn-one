package com.wutqi.jianshu.p1;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 
 * @author wuqi
 * @date 2019-05-31 14:41:47
 */
public class HystrixCommand4ThreadFallbackTest extends HystrixCommand<String> {
    private String name;


    protected HystrixCommand4ThreadFallbackTest(String name) {
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Group one"))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                        .withCoreSize(3)
                )
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(1000)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(10) //在信号隔离和线程隔离中均是适用的
                        .withCircuitBreakerRequestVolumeThreshold(30)
                        .withCircuitBreakerErrorThresholdPercentage(50)
                        .withCircuitBreakerEnabled(true)
                        .withCircuitBreakerSleepWindowInMilliseconds(50000)
                )
        );

        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        Thread.sleep(500L);
//        int a = 1/0;
        return name + "run(),Thread name = " + Thread.currentThread().getName();
    }

    @Override
    protected String getFallback() {
        System.out.println("fallback 执行了。。。");
        return name + "fallback(),Thread name = " + Thread.currentThread().getName();
    }

    public static void main(String[] args) throws InterruptedException {
        ;
        for(int i=0;i<5;i++){
            final int j = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HystrixCommand4ThreadFallbackTest hystrixCommand4ThreadFallbackTest1 = new HystrixCommand4ThreadFallbackTest("my Command " + j);
                    String result = hystrixCommand4ThreadFallbackTest1.execute();
                    System.out.println(result);
                }
            });
            thread.start();
        }

        Thread.sleep(5000L);
        System.out.println("***************从现在开始，熔断器是打开的**************");

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                HystrixCommand4ThreadFallbackTest hystrixCommand4ThreadFallbackTest1 = new HystrixCommand4ThreadFallbackTest("Circuit test 1");
                String result = hystrixCommand4ThreadFallbackTest1.execute();
                System.out.println(result);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                HystrixCommand4ThreadFallbackTest hystrixCommand4ThreadFallbackTest1 = new HystrixCommand4ThreadFallbackTest("Circuit test 2");
                String result = hystrixCommand4ThreadFallbackTest1.execute();
                System.out.println(result);
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                HystrixCommand4ThreadFallbackTest hystrixCommand4ThreadFallbackTest1 = new HystrixCommand4ThreadFallbackTest("Circuit test 3");
                String result = hystrixCommand4ThreadFallbackTest1.execute();
                System.out.println(result);
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        Thread.sleep(2000);
    }
}
