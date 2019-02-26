package utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import pojo.Action;

public class Utils {
    // id=action
    public static Hashtable<String, Action> cachedAction = null;
    // id=name@@xpath
    public static Map<String, String> cachedUiObj = null;
    public static String uiObjSeperator = "@@";
    public static String paraSeperator = "@";
    public static String dataVersion = null;
    public static String logLevel = null;
    public static String browserType = null;

    public enum ExecStatus {
        READYTOSTART, RUNNING, FAILED, SUCCESS, EXCEPTION, FAILEDTOSTART, FORCESTOP,
    }

    public static Properties readPropery(String fileName) throws Exception {
        InputStream io = new BufferedInputStream(new FileInputStream(new File(getResourcePath() + fileName)));
        Properties result = new Properties();
        result.load(io);
        return result;
    }

    public static String getResourcePath() throws Exception {
        String path = Utils.class.getResource("/").toURI().getRawPath();
        path = URLDecoder.decode(path, "UTF-8");
        return "/" + path;
    }

    public static String getRandomString(int length) {
        // 定义一个字符串（A-Z，a-z，0-9）即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        // 由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    private static BufferedImage grayImage(BufferedImage img, int width, int height) throws Exception {
        // 重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = img.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }
        return grayImage;
    }

    //挨着的去除独立的躁点
    private static BufferedImage removeSingle(BufferedImage img, int width, int height) {
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                if (isSingle(img, i, j)) {
                    img.setRGB(i, j, -1);
                }
            }
        }
        return img;
    }
    //少于4个黑点时，移除
    private static Boolean isSingle(BufferedImage img, int i, int j) {
        int cnt = 0;
        if (img.getRGB(i, j) == -1) {
            return false;
        }
        for (int k = i - 1; k <= i + 1; k++) {
            for (int v = j - 1; v <= j + 1; v++) {
                if (img.getRGB(k, v) != -1) {
                    cnt = cnt + 1;
                }
            }
        }
        if (cnt < 4) {
            return true;
        }
        return false;
    }
    //判断左右是否空行，黑点小于10%时为空行
    private static Boolean isColBlank(BufferedImage img, int x, int height) {
        int cnt = 0;
        for (int j = 0; j < height; j++) {
            if (img.getRGB(x, j) != -1) {
                cnt = cnt + 1;
            }
        }
        if(cnt * 100 >= height * 20){
            return false;
        }
        return true;
    }
    //多余一个黑点则不是空行
    private static Boolean isColBlankRaw(BufferedImage img, int x, int height){
        int cnt = 0;
        for (int j = 0; j < height; j++) {
            if (img.getRGB(x, j) != -1) {
                cnt = cnt + 1;
            }
        }
        if (cnt > 1) {
            return false;
        }
        return true;
    }
    private static BufferedImage[] rawSplitImage(BufferedImage img, int width, int height) {
        BufferedImage[] imgs = new BufferedImage[3];
        int start = 0;
        int end = width - 1;
        int middleStart = 0;
        Boolean result = false;
        for (int i = start; i < width; i++) {
            if (!result && !isColBlankRaw(img, i, height)) {
                start = i;
                result = true;
            }
            if (result && isColBlankRaw(img, i, height)) {
                end = i - 1;
                middleStart = i;
                break;
            }
        }
        imgs[0] = img.getSubimage(start, 0, end - start + 1, height);
        result = false;
        for (int i = width - 1; i > 0; i--) {
            if (!result && !isColBlankRaw(img, i, height)) {
                start = i;
                result = true;
            }
            if (result && isColBlankRaw(img, i, height)) {
                end = i + 1;
                break;
            }
        }
        imgs[2] = img.getSubimage(end, 0, start - end + 1, height);
        result = false;
        for (int i = middleStart; i <= end; i++) {
            if (!result && !isColBlankRaw(img, i, height)) {
                start = i;
                result = true;
            }
            if (result && isColBlankRaw(img, i, height)) {
                end = i - 1;
                break;
            }
        }
        imgs[1] = img.getSubimage(start, 0, end - start + 1, height);
        return imgs;
    }
    //判断上下是否空行，黑点小于10%时为空行
    private static Boolean isRowBlank(BufferedImage img, int width, int y){
        int cnt = 0;
        for (int i = 0; i < width; i++) {
            if (img.getRGB(i, y) != -1) {
                cnt = cnt + 1;
            }
        }
        if(cnt * 100 >= width * 20){
            return false;
        }
        return true;
    }
    //多余一个黑点则不是空行
    private static Boolean isRowBlankRaw(BufferedImage img, int width, int y){
        int cnt = 0;
        for (int i = 0; i < width; i++) {
            if (img.getRGB(i, y) != -1) {
                cnt = cnt + 1;
            }
        }
        if (cnt > 1) {
            return false;
        }
        return true;
    }
    private static BufferedImage rawRemoveRowBlank(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();
        int start = 0;
        int end = height;
        for (int j = 0; j < height; j++) {
            if (!isRowBlankRaw(img, width, j)) {
                start = j;
                break;
            }
        }
        for(int j=height-1;j>=0;j--){
            if (!isRowBlankRaw(img, width, j)) {
                end = j;
                break;
            }
        }
        return img.getSubimage(0, start, width, end - start + 1);
    }
    //移除上下的空行
    private static BufferedImage removeRowBlank(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int start = 0;
        int end = height;
        for (int j = 0; j < height; j++) {
            if (!isRowBlank(img, width, j)) {
                start = j;
                break;
            }
        }
        for(int j=height-1;j>=0;j--){
            if (!isRowBlank(img, width, j)) {
                end = j;
                break;
            }
        }
        return img.getSubimage(0, start, width, end - start + 1);
    }
    private static BufferedImage removeColBlank(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int start = 0;
        int end = width;
        for (int i = 0; i < width; i++) {
            if (!isColBlank(img, i, height)) {
                start = i;
                break;
            }
        }
        for(int i=width-1;i>=0;i--){
            if (!isColBlank(img, i, height)) {
                end = i;
                break;
            }
        }
        return img.getSubimage(start, 0, end - start + 1, height);
    }
    private static Map<BufferedImage, String> loadTrainData(String isFront) throws Exception {
        Map<BufferedImage, String> codes = new HashMap<BufferedImage, String>();
        File dir = new File(getResourcePath() + "code/");
        File[] files = dir.listFiles();
        for (File file : files) {
            String name = file.getName();
            if ("gwFront".equals(isFront) && name.startsWith("f")) {
                codes.put(ImageIO.read(file), name.substring(1, name.length() - 4));
            } else if (!"gwFront".equals(isFront) && name.startsWith("b")) {
                String codeName = name.substring(1, name.length() - 4);
                if (codeName.length() > 1 && codeName.length() < 3) {
                    codes.put(ImageIO.read(file), codeName.substring(0, 1));
                } else {
                    codes.put(ImageIO.read(file), codeName);
                }
            }
        }
        return codes;
    }

    private static void printImg(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(img.getRGB(i, j) + " ");
            }
            System.out.println("");
        }
    }

    private static String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> trainedData, String isFront) {
        String result = "";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = 0;

        Label1: for (BufferedImage item : trainedData.keySet()) {
            int count = 0;
            int codeWidth = item.getWidth();
            int codeHeight = item.getHeight();
            if (height - codeHeight > 1 || codeHeight - height > 1) {
                continue;
            }
            int boundWidth = width < codeWidth ? width : codeWidth;
            int boundHeight = height < codeHeight ? height : codeHeight;
            if ("gwFront".equals(isFront)) {
                min = boundWidth * boundHeight * 90;
            } else {
                min = boundWidth * boundHeight * 85;
            }

            for (int i = 0; i < boundWidth; i++) {
                for (int j = 0; j < boundHeight; j++) {
                    if (item.getRGB(i, j) < -10000000 && img.getRGB(i, j) < -10000000
                            || item.getRGB(i, j) > -10000000 && img.getRGB(i, j) > -10000000) {
                        count++;
                        if (count * 100 >= min) {
                            result = trainedData.get(item);
                            break Label1;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String[] ocr(File file, String page) throws Exception {
        BufferedImage downloadImg = ImageIO.read(file);

        int width = downloadImg.getWidth();
        int height = downloadImg.getHeight();
        downloadImg = grayImage(downloadImg, width, height);
        downloadImg = removeSingle(downloadImg, width, height);
        BufferedImage[] singleCode = rawSplitImage(downloadImg, width, height);

        Map<BufferedImage, String> codes = loadTrainData(page);
        String[] result = new String[3];
        for (int i = 0; i < 3; i++) {
            singleCode[i]=rawRemoveRowBlank(singleCode[i]);
            if(!"gwFront".equals(page)){
                singleCode[i]=removeRowBlank(singleCode[i]);
                singleCode[i]=removeColBlank(singleCode[i]);
            }
            result[i] = getSingleCharOcr(singleCode[i], codes, page);
        }
        if (result[1].equals("minus")) {
            result[1] = "-";
        } else {
            result[1] = "+";
        }
        return result;
    }

    private static void trainData(File file, Boolean isFront) throws Exception {
        String name = file.getAbsolutePath();
        BufferedImage downloadImg = ImageIO.read(file);

        int width = downloadImg.getWidth();
        int height = downloadImg.getHeight();
        downloadImg = grayImage(downloadImg, width, height);
        downloadImg = removeSingle(downloadImg, width, height);
        BufferedImage[] singleCode = rawSplitImage(downloadImg, width, height);

        String desPath = name.split("\\.")[0];
        for (int i = 0; i < 3; i++) {
            singleCode[i]=rawRemoveRowBlank(singleCode[i]);
            if(!isFront){
                singleCode[i]=removeRowBlank(singleCode[i]);
                singleCode[i]=removeColBlank(singleCode[i]);
            }
            
            File newFile = new File(desPath + "_result" + i + ".jpg");
            ImageIO.write(singleCode[i], "jpg", newFile);
        }
    }

    // public static void main(String[] args) throws Exception {
    //     File dir = new File("/Users/zhengliliang/Desktop/p/");
    //     File[] files = dir.listFiles();
    //     for (File file : files) {
    //         if (!file.getName().endsWith("jpeg") && !file.getName().endsWith("png")) {
    //             continue;
    //         }
    //         if(file.getName().startsWith("a")){
    //             trainData(file, true);
    //         }
    //         if(file.getName().startsWith("b")){
    //             trainData(file, false);
    //         }

    //         String[] result = null;
    //         if (file.getName().startsWith("a")) {
    //             System.out.print(file.getName()+" ");
    //             result=ocr(file, true);
    //             System.out.println(result[0]+result[1]+result[2]);
    //         } else if (file.getName().startsWith("b")) {
    //             // System.out.print(file.getName() + " ");
    //             // result = ocr(file, false);
    //             // System.out.println(result[0] + result[1] + result[2]);
    //         }
    //     }
    // }
}