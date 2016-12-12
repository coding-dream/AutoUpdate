package com.ruoxu.update.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by wangli on 16/12/12.
 */
public class IOUtils {

    static void close(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
