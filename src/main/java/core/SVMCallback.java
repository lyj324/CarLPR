package main.java.core;

import org.bytedeco.javacpp.opencv_core;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 11:55
 */
public interface SVMCallback {
    /***
     * getFeatures回调函数,本函数是生成直方图均衡特征的回调函数
     *
     * @param image
     * @return
     */
    public abstract opencv_core.Mat getHisteqFeatures(final opencv_core.Mat image);
    /**
     * getFeatures回调函数, 本函数是获取垂直和水平的直方图图值
     *
     * @param image
     * @return
     */
    public abstract opencv_core.Mat getHistogramFeatures(final opencv_core.Mat image);

    /**
     * 本函数是获取SITF特征子的回调函数
     *
     * @param image
     * @return
     */
    public abstract opencv_core.Mat getSIFTFeatures(final opencv_core.Mat image);

    /**
     * 本函数是获取HOG特征子的回调函数
     *
     * @param image
     * @return
     */
    public abstract opencv_core.Mat getHOGFeatures(final opencv_core.Mat image);
}
