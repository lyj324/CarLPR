package main.java.train;

import main.java.core.CoreFunc;
import main.java.util.Convert;
import main.java.util.Util;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_ml;

import java.util.Vector;

import static main.java.core.CoreFunc.projectedHistogram;
import static main.java.core.CoreFunc.showImage;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 13:22
 */
public class ANNTrain {
    private opencv_ml.CvANN_MLP ann = new opencv_ml.CvANN_MLP();

    Mat classes = new Mat();
    Mat trainingDataf5 = new Mat();
    Mat trainingDataf10 = new Mat();
    Mat trainingDataf15 = new Mat();
    Mat trainingDataf20 = new Mat();
    // 中国车牌
    private final char strCharacters[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', /* 没有I */
            'J', 'K', 'L', 'M', 'N', /* 没有O */'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    private final int numCharacter = 34; /* 没有I和0,10个数字与24个英文字符之和 */

    // 以下都是我训练时用到的中文字符数据，并不全面，有些省份没有训练数据所以没有字符
    // 有些后面加数字2的表示在训练时常看到字符的一种变形，也作为训练数据存储
    private final String strChinese[] = { "zh_cuan" /* 川 */, "zh_e" /* 鄂 */, "zh_gan" /* 赣 */, "zh_hei" /* 黑 */,
            "zh_hu" /* 沪 */, "zh_ji" /* 冀 */, "zh_jl" /* 吉 */, "zh_jin" /* 津 */, "zh_jing" /* 京 */, "zh_shan" /* 陕 */,
            "zh_liao" /* 辽 */, "zh_lu" /* 鲁 */, "zh_min" /* 闽 */, "zh_ning" /* 宁 */, "zh_su" /* 苏 */, "zh_sx" /* 晋 */,
            "zh_wan" /* 皖 */, "zh_yu" /* 豫 */, "zh_yue" /* 粤 */, "zh_zhe" /* 浙 */};

    private final int numAll = 54; /* 34+20=54 */

    public Mat features(Mat in, int sizeData) {
        // Histogram features
        float[] vhist = projectedHistogram(in, CoreFunc.Direction.VERTICAL);
        float[] hhist = projectedHistogram(in, CoreFunc.Direction.HORIZONTAL);

        // Low data feature
        Mat lowData = new Mat();
        resize(in, lowData, new Size(sizeData, sizeData));

        // Last 10 is the number of moments components
        int numCols = vhist.length + hhist.length + lowData.cols() * lowData.cols();

        Mat out = Mat.zeros(1, numCols, CV_32F).asMat();
        // Asign values to feature,ANN的样本特征为水平、垂直直方图和低分辨率图像所组成的矢量
        int j = 0;
        for (int i = 0; i < vhist.length; i++, ++j) {
            //out.at<float>(j)=vhist.at<float>(i);
            out.ptr(0,j).put(Convert.getBytes(vhist[i]));
            //out.ptr(j).put(Convert.getBytes(vhist[i]));
        }
        for (int i = 0; i < hhist.length; i++, ++j) {
            out.ptr(0,j).put(Convert.getBytes(hhist[i]));
            //out.ptr(j).put(Convert.getBytes(hhist[i]));
        }
        for (int x = 0; x < lowData.cols(); x++) {
            for (int y = 0; y < lowData.rows(); y++, ++j) {
                float val = lowData.ptr(x, y).get() & 0xFF;
                out.ptr(0,j).put(Convert.getBytes(val));
                //out.ptr(j).put(Convert.getBytes(val));
            }
        }
        return out;
    }

    public void annTrain(Mat TrainData, Mat classes, int nNeruns) {
        ann.clear();
        Mat layers = new Mat(1, 3, CV_32SC1);
        layers.ptr(0,0).put(Convert.getBytes(TrainData.cols()));
        layers.ptr(0,1).put(Convert.getBytes(nNeruns));
        layers.ptr(0,2).put(Convert.getBytes(numAll));
        ann.create(layers, opencv_ml.CvANN_MLP.SIGMOID_SYM, 1, 1);
        // Prepare trainClases
        // Create a mat with n trained data by m classes
        Mat trainClasses = new Mat();
        trainClasses.create(TrainData.rows(), numAll, CV_32FC1);
        for (int i = 0; i < trainClasses.rows(); i++) {
            for (int k = 0; k < trainClasses.cols(); k++) {
                // If class of data i is same than a k class
                if (k == Convert.toInt(classes.ptr(i))) {
                    trainClasses.ptr(i, k).put(Convert.getBytes(1f));
                } else {
                    trainClasses.ptr(i, k).put(Convert.getBytes(0f));
                }
            }
        }
        Mat weights = new Mat(1, TrainData.rows(), CV_32FC1, Scalar.all(1));
        // Learn classifier
        ann.train(TrainData, trainClasses, weights);
    }

    public int saveTrainData() {
        System.out.println("Begin saveTrainData");


        Vector<Integer> trainingLabels = new Vector<Integer>();
        String path = "res/train/data/chars_recognise_ann/chars2";

        for (int i = 0; i < numCharacter; i++) {
            System.out.println("Character: " + strCharacters[i]);
            String str = path + '/' + strCharacters[i];
            Vector<String> files = new Vector<String>();
            Util.getFiles(str, files);

            int size = (int) files.size();
            for (int j = 0; j < size; j++) {
                System.out.println(files.get(j));
                Mat img = imread(files.get(j), 0);
                Mat f5 = features(img, 5);
                Mat f10 = features(img, 10);
                Mat f15 = features(img, 15);
                Mat f20 = features(img, 20);

                trainingDataf5.push_back(f5);
                trainingDataf10.push_back(f10);
                trainingDataf15.push_back(f15);
                trainingDataf20.push_back(f20);
                trainingLabels.add(i); // 每一幅字符图片所对应的字符类别索引下标
            }
        }

        path = "res/train/data/chars_recognise_ann/charsChinese";

        for (int i = 0; i < strChinese.length; i++) {
            System.out.println("Character: " + strChinese[i]);
            String str = path + '/' + strChinese[i];
            Vector<String> files = new Vector<String>();
            Util.getFiles(str, files);

            int size = (int) files.size();
            for (int j = 0; j < size; j++) {
                System.out.println(files.get(j));
                Mat img = imread(files.get(j), 0);
                Mat f5 = features(img, 5);
                Mat f10 = features(img, 10);
                Mat f15 = features(img, 15);
                Mat f20 = features(img, 20);

                trainingDataf5.push_back(f5);
                trainingDataf10.push_back(f10);
                trainingDataf15.push_back(f15);
                trainingDataf20.push_back(f20);
                trainingLabels.add(i + numCharacter);
            }
        }

        trainingDataf5.convertTo(trainingDataf5, CV_32FC1);
        trainingDataf10.convertTo(trainingDataf10, CV_32FC1);
        trainingDataf15.convertTo(trainingDataf15, CV_32FC1);
        trainingDataf20.convertTo(trainingDataf20, CV_32FC1);
        int[] labels = new int[trainingLabels.size()];
        for (int i = 0; i < labels.length; ++i) {
            labels[i] = trainingLabels.get(i).intValue();
        }
        new Mat(labels).copyTo(classes);
//        FileStorage fs = new FileStorage("res/train/ann_data.xml", FileStorage.WRITE);
//
//        fs.writeObj("TrainingDataF5", trainingDataf5.data());
//        fs.writeObj("TrainingDataF10", trainingDataf10.data());
//        fs.writeObj("TrainingDataF15", trainingDataf15.data());
//        fs.writeObj("TrainingDataF20", trainingDataf20.data());
//        fs.writeObj("classes", classes.data());
//        fs.release();

        System.out.println("End saveTrainData");
        return 0;
    }

    public void saveModel(int _predictsize, int _neurons,Mat TrainingData,Mat Classes) {
//        FileStorage fs = new FileStorage("res/train/ann_data.xml", FileStorage.READ);
        String training = "TrainingDataF" + _predictsize;

        // train the Ann
        System.out.println("Begin to saveModelChar predictSize:" + Integer.valueOf(_predictsize).toString());
        System.out.println(" neurons:" + Integer.valueOf(_neurons).toString());

        long start = getTickCount();
        annTrain(TrainingData, Classes, _neurons);
        long end = getTickCount();
        System.out.println("GetTickCount:" + Long.valueOf((end - start) / 1000).toString());

        System.out.println("End the saveModelChar");

        String model_name = "res/train/ann.xml";

        // if(1)
        // {
        // String str =
        // String.format("ann_prd:%d\tneu:%d",_predictsize,_neurons);
        // model_name = str;
        // }

        CvFileStorage fsto = CvFileStorage.open(model_name, CvMemStorage.create(), CV_STORAGE_WRITE);
        //ann.write(fsto, "ann");
        ann.save("res/train/ann.xml","ann");
    }

    public int annMain() {
        System.out.println("To be begin.");

        saveTrainData();

        // 可根据需要训练不同的predictSize或者neurons的ANN模型
        // for (int i = 2; i <= 2; i ++)
        // {
        // int size = i * 5;
        // for (int j = 5; j <= 10; j++)
        // {
        // int neurons = j * 10;
        // saveModel(size, neurons);
        // }
        // }

        // 这里演示只训练model文件夹下的ann.xml，此模型是一个predictSize=10,neurons=40的ANN模型。
        // 根据机器的不同，训练时间不一样，但一般需要10分钟左右，所以慢慢等一会吧。


        saveModel(10, 40,trainingDataf10,classes);

        System.out.println("To be end.");
        return 0;
    }
}
