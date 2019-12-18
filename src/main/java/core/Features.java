package main.java.core;

import org.bytedeco.javacpp.opencv_core;

import static main.java.core.CoreFunc.features;
import static org.bytedeco.javacpp.opencv_core.merge;
import static org.bytedeco.javacpp.opencv_core.split;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 11:54
 */
public class Features implements SVMCallback{
    @Override
    public opencv_core.Mat getHisteqFeatures(final opencv_core.Mat image) {
        return histeq(image);
    }

    @Override
    public opencv_core.Mat getHistogramFeatures(opencv_core.Mat image) {
        opencv_core.Mat grayImage = new opencv_core.Mat();
        cvtColor(image, grayImage, CV_RGB2GRAY);
        opencv_core.Mat img_threshold = new opencv_core.Mat();
        threshold(grayImage, img_threshold, 0, 255, CV_THRESH_OTSU + CV_THRESH_BINARY);
        return features(img_threshold, 0);
    }

    @Override
    public opencv_core.Mat getSIFTFeatures(opencv_core.Mat image) {
        return null;
    }

    @Override
    public opencv_core.Mat getHOGFeatures(opencv_core.Mat image) {
        return null;
    }
    private opencv_core.Mat histeq(opencv_core.Mat in) {
        opencv_core.Mat out = new opencv_core.Mat(in.size(), in.type());
        if (in.channels() == 3) {
            opencv_core.Mat hsv = new opencv_core.Mat();
            opencv_core.MatVector hsvSplit = new opencv_core.MatVector();
            cvtColor(in, hsv, CV_BGR2HSV);
            split(hsv, hsvSplit);
            equalizeHist(hsvSplit.get(2), hsvSplit.get(2));
            merge(hsvSplit, hsv);
            cvtColor(hsv, out, CV_HSV2BGR);
            hsv = null;
            hsvSplit = null;
            System.gc();
        } else if (in.channels() == 1) {
            equalizeHist(in, out);
        }
        return out;
    }
}
