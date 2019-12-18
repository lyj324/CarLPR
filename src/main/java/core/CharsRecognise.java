package main.java.core;

import org.bytedeco.javacpp.opencv_core;

import java.util.Vector;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 12:40
 */
public class CharsRecognise {
    public void loadANN(final String s) {
        charsIdentify.loadModel(s);
    }

    /**
     * Chars segment and identify 字符分割与识别
     *
     * @param plate
     *            the input plate
     * @return the result of plate recognition
     */
    public String charsRecognise(final opencv_core.Mat plate) {

        // the set of plate character after segment 车牌字符方块集合
        Vector<opencv_core.Mat> matVec = new Vector<opencv_core.Mat>();
        // the result of plate recognition
        String plateIdentify = "";

        int result = charsSegment.charsSegment(plate, matVec);
        if (0 == result) {
            for (int j = 0; j < matVec.size(); j++) {
                opencv_core.Mat charMat = matVec.get(j);
                // the first is Chinese char as default 默认首个字符块是中文字符
                String charcater = charsIdentify.charsIdentify(charMat, (0 == j), (1 == j));
                plateIdentify = plateIdentify + charcater;
            }
        }

        return plateIdentify;
    }

    /**
     * 是否开启调试模式
     *
     * @param isDebug
     */
    public void setCRDebug(final boolean isDebug) {
        charsSegment.setDebug(isDebug);
    }

    /**
     * 获取调试模式状态
     *
     * @return
     */
    public boolean getCRDebug() {
        return charsSegment.getDebug();
    }

    /**
     * 获得车牌颜色
     *
     * @param input
     * @return
     */
    public final String getPlateType(final opencv_core.Mat input) {
        String color = "未知";
        CoreFunc.Color result = CoreFunc.getPlateType(input, true);
        if (CoreFunc.Color.BLUE == result) {
            color = "蓝牌";
        }
        if (CoreFunc.Color.YELLOW == result) {
            color = "黄牌";
        }
        return color;
    }

    /**
     * 设置柳丁大小变量
     *
     * @param param
     */
    public void setLiuDingSize(final int param) {
        charsSegment.setLiuDingSize(param);
    }

    /**
     * 设置颜色阈值
     *
     * @param param
     */
    public void setColorThreshold(final int param) {
        charsSegment.setColorThreshold(param);
    }

    /**
     * 设置蓝色百分比
     *
     * @param param
     */
    public void setBluePercent(final float param) {
        charsSegment.setBluePercent(param);
    }

    /**
     * 得到蓝色百分比
     *
     */
    public final float getBluePercent() {
        return charsSegment.getBluePercent();
    }

    /**
     * 设置白色百分比
     *
     * @param param
     */
    public void setWhitePercent(final float param) {
        charsSegment.setWhitePercent(param);
    }

    /**
     * 得到白色百分比
     *
     */
    public final float getWhitePercent() {
        return charsSegment.getWhitePercent();
    }

    private CharsSegment charsSegment = new CharsSegment();

    private CharsIdentify charsIdentify = new CharsIdentify();
}