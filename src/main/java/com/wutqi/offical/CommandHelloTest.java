package com.wutqi.offical;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author wuqi
 * @date 2019-06-16 09:28:15
 */
public class CommandHelloTest {

    @Test
    public void myTest() throws ExecutionException, InterruptedException {
//        test1();
        test2();


    }

    @Test
    public void test2() {
        Observable<String> fWorld = new CommandHelloWorld("World").toObservable();
        Observable<String> fBob = new CommandHelloWorld("Bob").toObservable();

        fWorld.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                // nothing needed here
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onNext(String v) {
                System.out.println("onNext: " + v);
            }
        });

        fBob.subscribe(new Action1<String>() {
            @Override
            public void call(String v) {
                System.out.println("onNext: " + v);
            }
        });
    }

    private void test1() {
        Observable<String> fWorld = new CommandHelloWorld("World").observe();
        Observable<String> fBob = new CommandHelloWorld("Bob").observe();


        // 阻塞模式
        assertEquals("Hello World!", fWorld.toBlocking().single());
        assertEquals("Hello Bob!", fBob.toBlocking().single());
        // 非阻塞模式
        // - 匿名内部类形式，本测试不做任何断言
        fWorld.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                // nothing needed here
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onNext(String v) {
                System.out.println("onNext: " + v);
            }
        });
        // 非阻塞模式
        // - 同样是匿名内部类形式，忽略“异常”和“完成”回调
        fBob.subscribe(new Action1<String>() {
            @Override
            public void call(String v) {
                System.out.println("onNext: " + v);
            }
        });
    }
}
