package main.java.core;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_ml;

import java.util.Vector;

import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 11:43
 * 判断由PlateLocate找到的矩形块是否为“车牌矩形”
 */
public class PlateJudge {
    public PlateJudge() {
        loadModel();
    }

    public void loadModel() {
        loadModel(path);
    }

    public void loadModel(String s) {
        svm.clear();
        svm.load(s, "svm");
    }

    /**
     * 对单幅图像进行SVM判断
     *
     * @param inMat
     * @return
     */
    public int plateJudge(final opencv_core.Mat inMat) {
        opencv_core.Mat features = this.features.getHistogramFeatures(inMat);
        // 通过直方图均衡化后的彩色图进行预测
        opencv_core.Mat p = features.reshape(1, 1);
        p.convertTo(p, CV_32FC1);
        float ret = svm.predict(features);
        return (int) ret;
    }

    /**
     * 对多幅图像进行SVM判断
     *
     * @param inVec
     * @param resultVec
     * @return
     */
    public int plateJudge(Vector<opencv_core.Mat> inVec, Vector<opencv_core.Mat> resultVec) {
        for (int j = 0; j < inVec.size(); j++) {
            opencv_core.Mat inMat = inVec.get(j);
            if (1 == plateJudge(inMat)) {
                resultVec.add(inMat);
            } else { // 再取中间部分判断一次
                int w = inMat.cols();
                int h = inMat.rows();
                opencv_core.Mat tmpDes = inMat.clone();
                opencv_core.Mat tmpMat = new opencv_core.Mat(inMat, new opencv_core.Rect((int) (w * 0.05), (int) (h * 0.1), (int) (w * 0.9),
                        (int) (h * 0.8)));
                resize(tmpMat, tmpDes, new opencv_core.Size(inMat.size()));

                if (plateJudge(tmpDes) == 1) {
                    resultVec.add(inMat);
                }
            }
        }
        return 0;
    }

    public void setModelPath(String path) {
        this.path = path;
    }

    public final String getModelPath() {
        return path;
    }

    private opencv_ml.CvSVM svm = new opencv_ml.CvSVM();

    /**
     * getFeatures回调函数, 用于从车牌的image生成svm的训练特征features
     */
    private SVMCallback features = new Features();

    /**
     * 模型存储路径
     */
    private String path = "res/model/svm.xml";
}
