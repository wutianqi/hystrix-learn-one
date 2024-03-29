package com.wutqi.p3;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

/**
 * 
 * @author wuqi
 * @date 2019-05-30 16:56:45
 */
public class RequestCacheCommand extends HystrixCommand<String> {
    private final int id;
    public RequestCacheCommand( int id) {
        super(HystrixCommandGroupKey.Factory.asKey("RequestCacheCommand"));
        this.id = id;
    }
    @Override
    protected String run() throws Exception {
        System.out.println(Thread.currentThread().getName() + " execute id=" + id);
        return "executed=" + id;
    }
    //重写getCacheKey方法,实现区分不同请求的逻辑
    @Override
    protected String getCacheKey() {
        return String.valueOf(id);
    }

    public static void main(String[] args){
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            RequestCacheCommand command2a = new RequestCacheCommand(2);
            RequestCacheCommand command2b = new RequestCacheCommand(2);
            Assert.assertTrue(StringUtils.isNotBlank(command2a.execute()));
            //isResponseFromCache判定是否是在缓存中获取结果
            Assert.assertFalse(command2a.isResponseFromCache());
            Assert.assertTrue(StringUtils.isNotBlank(command2b.execute()));
            Assert.assertTrue(command2b.isResponseFromCache());
        } finally {
            context.shutdown();
        }
        context = HystrixRequestContext.initializeContext();
        try {
            RequestCacheCommand command3b = new RequestCacheCommand(2);
            Assert.assertTrue(StringUtils.isNotBlank(command3b.execute()));
            Assert.assertFalse(command3b.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }
}
