package main.java.util;

import java.io.File;
import java.util.Vector;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 9:17
 */
public class Util {
    /**
     * 获取文件夹中的所有文件
     * @param path 文件路径
     * @param files 自增长文件数组
     */
    public static void getFiles(final String path, Vector<String> files){
        getFiles(new File(path), files);
    }

    /**
     * 获取文件夹中的所有文件
     * @param dir
     * @param files
     */
    private static void getFiles(final File dir, Vector<String> files) {
        File[] filelist = dir.listFiles();
        for (File file : filelist) {
            if (file.isDirectory()) {
                getFiles(file, files);
            } else {
                files.add(file.getAbsolutePath());
            }
        }
    }

    /**
     * 在指定位置删除并创建一个同名文件夹
     * @param dir
     */
    public static void recreateDir(final String dir) {
        new File(dir).delete();
        new File(dir).mkdir();
    }
}
