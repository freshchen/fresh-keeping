package com.github.freshchen.keeping;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

/**
 * @author darcy
 * @since 2021/4/30
 **/
public class FileAlterationMonitorTest {

    public static void main(String[] args) {
        FileAlterationMonitor monitor = new FileAlterationMonitor(10);
        // 文件观察者
        FileAlterationObserver observer = new FileAlterationObserver(
                "/Users/darcy/Code/fresh-keeping/java/file/src/main/java/com/github/freshchen/keeping",
                FileFilterUtils.suffixFileFilter(".txt"));
        // 监听到文件变动后的处理器
        observer.addListener(new MyFileListenerAdaptor());
        observer.addListener(new My1FileListenerAdaptor());
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
