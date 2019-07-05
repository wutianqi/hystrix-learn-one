package com.wutqi.p4;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.commons.lang.StringUtils;

/**
 * 信号量隔离
 *
 * @author wuqi
 * @date 2019-05-30 17:27:24
 */
public class HelloWorldCommand extends HystrixCommand<String> {
    private final String name;

    protected HelloWorldCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("HelloWorldGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationStrategy(
                        HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE
                ))
        );
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "Hystrix Thread:" + Thread.currentThread().getName();
    }

    public static void main(String[] args){
        HelloWorldCommand command = new HelloWorldCommand("semaphore");
        String result = command.execute();
        System.out.println(result);
        System.out.println("MainThread:" + Thread.currentThread().getName());
    }
}
