package com.wutqi.jianshu.p3;

import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 合并请求
 *
 * @author wuqi
 * @date 2019-05-31 17:48:05
 */
public class HelloWorldHystrixCollapser extends HystrixCollapser<List<String>,String,Integer> {
    private Integer requestKey;

    public HelloWorldHystrixCollapser(Integer requestKey){
        this.requestKey = requestKey;
    }


    @Override
    public Integer getRequestArgument() {
        return this.requestKey;
    }

    @Override
    protected HystrixCommand<List<String>> createCommand(Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
        return new CollapsedCommand(collapsedRequests);
    }

    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> collapsedRequests) {
        int count = 0;
        for(CollapsedRequest<String,Integer> request : collapsedRequests ){
            request.setResponse(batchResponse.get(count++));
        }
    }

    private class CollapsedCommand extends HystrixCommand<List<String>>{
        private Collection<CollapsedRequest<String, Integer>> requests;

        protected CollapsedCommand(Collection<CollapsedRequest<String, Integer>> requests) {
            super(
                    Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Collapser Group"))
            );
            this.requests = requests;
        }

        @Override
        protected List<String> run() throws Exception {
            List<String> responses = new ArrayList<>();
            for (CollapsedRequest request : requests){
                responses.add("ValueForKey: " + request.getArgument() + "thread name: " + Thread.currentThread().getName());
            }
            return responses;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        try {
            Future<String> f1 = new HelloWorldHystrixCollapser(1).queue();
            Future<String> f2 = new HelloWorldHystrixCollapser(2).queue();
            Future<String> f3 = new HelloWorldHystrixCollapser(3).queue();
            Future<String> f4 = new HelloWorldHystrixCollapser(4).queue();
            Future<String> f5 = new HelloWorldHystrixCollapser(5).queue();

            TimeUnit.MILLISECONDS.sleep(13);
            Future<String> f6 = new HelloWorldHystrixCollapser(6).queue();
            System.out.println(f1.get());
            System.out.println(f2.get());
            System.out.println(f3.get());
            System.out.println(f4.get());
            System.out.println(f5.get());
            System.out.println(f6.get());

            int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();
            System.out.println("numExecuted=" + numExecuted);

            int numLogs = 0;
            for(HystrixInvokableInfo<?> command : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()){
                numLogs++;

                System.out.println(command.getCommandKey().name() + " => command.getExecutionEvents: " + command.getExecutionEvents());

                System.out.println(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
            }

            System.out.println(numExecuted == numLogs);

        } finally {
            context.close();
        }

    }

}
