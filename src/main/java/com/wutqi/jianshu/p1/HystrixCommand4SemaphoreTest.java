package com.wutqi.jianshu.p1;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * 
 * @author wuqi
 * @date 2019-05-31 13:20:55
 */
public class HystrixCommand4SemaphoreTest extends HystrixCommand<String> {
    private final String name;

    protected HystrixCommand4SemaphoreTest(String name) {
        super(
                Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SemaphoreTestGroup"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(3)
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(1)
                )
        );

        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "run(): name=" + name + ",线程名是" + Thread.currentThread().getName();
    }

    @Override
    protected String getFallback() {
        return "getFallback(): name=" + name + ",线程名是" + Thread.currentThread().getName();
    }

    public static class UnitTest{

        @Test
        public void testSynchronous() throws InterruptedException, IOException {
            try {
                Thread.sleep(2000);

                for (int i=0;i<5;i++){
                    final int j = i;

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HystrixCommand4SemaphoreTest hystrixCommand4SemaphoreTest = new HystrixCommand4SemaphoreTest("command" + j);
                            String result = hystrixCommand4SemaphoreTest.execute();
                            System.out.println(result);
                        }
                    });

                    thread.start();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("-----------开始打印现有线程---------------");
            Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
            for(Thread thread : map.keySet()){
                System.out.println(thread.getName());
            }
            System.out.println("thread num: " + map.size());
            Thread.sleep(30000);
        }

    }
}
