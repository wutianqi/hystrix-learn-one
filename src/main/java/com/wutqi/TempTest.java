package com.wutqi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author wuqi
 * @date 2019-06-20 16:06:28
 */
public class TempTest {
    public static void main(String[] args) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = contextClassLoader
                .getResourceAsStream("/foot.png");
        InputStream footResourceAsStream = TempTest.class.getResourceAsStream("/foot.png");

        byte[] bytes = IOUtils.toByteArray(resourceAsStream);
        System.out.println(bytes);

    }
}
