package test.java;

import main.java.core.*;
import main.java.train.ANNTrain;
import main.java.train.SVMTrain;
import main.java.util.Convert;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.junit.Test;

import java.util.Vector;

import static main.java.core.CoreFunc.getPlateType;
import static main.java.core.CoreFunc.showImage;
import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_highgui.imread;

/**
 * @author lyj
 * @version 1.0
 * @date 2019/12/15 10:27
 */
public class Main {
    /**
     * 测试查找“车牌矩形”
     */
    @Test
    public void testPlateLocate() {
        String imgPath = "res/image/test_image/test.jpg";
        opencv_core.Mat src = imread(imgPath);
        PlateLocate plate = new PlateLocate();
        plate.setDebug(true);
        plate.setLifemode(true);
        Vector<opencv_core.Mat> resultVec = plate.plateLocate(src);
        int num = resultVec.size();
        for (int j = 0; j < num; j++) {
            showImage("Plate Located " + j, resultVec.get(j));
        }
    }

    /**
     * 测试利用SVM模型判断“车牌矩形”是否为车牌
     */
    @Test
    public void testPlateJudge(){
        String imgPath = "tmp/debug_resize_4.jpg";
        opencv_core.Mat src = imread(imgPath);
        PlateJudge plateJudge = new PlateJudge();
        int i = plateJudge.plateJudge(src);
        if(i==1){
            System.out.println("是车牌");
            showImage("find license plate",src);
        }else {
            System.out.println("非车牌");
            showImage("non license plate",src);
        }
    }
    /**
     * 显示车牌矩形（包括寻找车牌位置，从车牌矩形中找到车牌）
     */
    @Test
    public void testPlateDetect() {
        String imgPath = "res/image/test_image/test.jpg";
        opencv_core.Mat src = imread(imgPath);
        PlateDetect plateDetect = new PlateDetect();
        plateDetect.setPDLifemode(true);
        Vector<opencv_core.Mat> matVector = new Vector<opencv_core.Mat>();
        if (0 == plateDetect.plateDetect(src, matVector)) {
            for (int i = 0; i < matVector.size(); ++i) {
                showImage("Plate Detected", matVector.get(i));
            }
        }
    }

    /**
     * 测试字符分割
     */
    @Test
    public void testCharsSegment () {
        String imgPath = "D:\\LPR0.11\\LPR\\tmp/debug_resize_1.jpg";
        opencv_core.Mat src = imread(imgPath);
        CharsSegment charsSegment = new CharsSegment();
        charsSegment.setDebug(true);
        charsSegment.charsSegment(src,new Vector<opencv_core.Mat>());
    }
    /**
     * 车牌颜色识别
     */
    @Test
    public void testPlateColor(){
        String imgPath = "tmp/debug_resize_4.jpg";
        opencv_core.Mat src = imread(imgPath);
        CoreFunc.Color color = getPlateType(src, true);
        System.out.println("Color Deteted: " + color);
    }
    /**
     * 字符识别
     */
    @Test
    public void testCharsIdentify(){
        String imgPath = "tmp/debug_char_auxRoi_1.jpg";
        opencv_core.Mat src = imread(imgPath);
        CharsIdentify charsIdentify = new CharsIdentify();
        String result = charsIdentify.charsIdentify(src, false, true);
        System.out.println(result);
    }
    /**
     * 车牌分割与识别
     */
    @Test
    public void testCharsRecognise(){
        String imgPath = "D:\\OpenCV\\EasyPR-Maven\\res\\image\\baidu_image\\test18.jpg";
        opencv_core.Mat src = imread(imgPath);
        CharsRecognise cr = new CharsRecognise();
        cr.setCRDebug(true);
        String result = cr.charsRecognise(src);
        System.out.println("Chars Recognised: " + result);
    }
    /**
     * 识别图片中的车牌
     */
    @Test
    public void testPlateRecognise() {
        String imgPath = "D:\\OpenCV\\EasyPR-Maven\\res\\image\\baidu_image\\test18.jpg";
        opencv_core.Mat src = imread(imgPath);
        PlateDetect plateDetect = new PlateDetect();
        plateDetect.setPDLifemode(true);
        Vector<opencv_core.Mat> matVector = new Vector<opencv_core.Mat>();
        if (0 == plateDetect.plateDetect(src, matVector)) {
            CharsRecognise cr = new CharsRecognise();
            for (int i = 0; i < matVector.size(); ++i) {
                String result = cr.getPlateType(matVector.get(i))+" "+cr.charsRecognise(matVector.get(i));
                System.out.println("Chars Recognised: " + result);
            }
        }
    }
    /**
     * 训练SVM
     */
    @Test
    public void testSVM(){
        SVMTrain svmTrain = new SVMTrain();
        svmTrain.svmTrain(true,true);
    }
    /**
     * 训练ANN
     */
    @Test
    public void testANN(){
        ANNTrain annTrain = new ANNTrain();
        annTrain.annMain();
    }
}
