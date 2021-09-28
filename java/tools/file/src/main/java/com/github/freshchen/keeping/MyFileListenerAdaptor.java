package com.github.freshchen.keeping;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

/**
 * @author darcy
 * @since 2021/4/30
 **/
public class MyFileListenerAdaptor extends FileAlterationListenerAdaptor {

    @Override
    public void onFileChange(File file) {
        System.out.println("文件 " + file.getName() + "发生变化");
        super.onFileChange(file);
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("文件 " + file.getName() + "被删除了");
        super.onFileDelete(file);
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("文件 " + file.getName() + "被新建了");
        super.onFileCreate(file);
    }
}
