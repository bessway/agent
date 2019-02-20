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

    private static Boolean isBlank(BufferedImage img, int x, int height) {
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

    private static BufferedImage[] splitImage(BufferedImage img, int width, int height) {
        BufferedImage[] imgs = new BufferedImage[3];
        int start = 0;
        int end = width - 1;
        int middleStart = 0;
        Boolean result = false;
        for (int i = start; i < width; i++) {
            if (!result && !isBlank(img, i, height)) {
                start = i;
                result = true;
            }
            if (result && isBlank(img, i, height)) {
                end = i - 1;
                middleStart = i;
                break;
            }
        }
        imgs[0] = img.getSubimage(start, 0, end - start + 1, height);
        imgs[0] = removeHeightBlank(imgs[0]);
        result = false;
        for (int i = width - 1; i > 0; i--) {
            if (!result && !isBlank(img, i, height)) {
                start = i;
                result = true;
            }
            if (result && isBlank(img, i, height)) {
                end = i + 1;
                break;
            }
        }
        imgs[2] = img.getSubimage(end, 0, start - end + 1, height);
        imgs[2] = removeHeightBlank(imgs[2]);
        result = false;
        for (int i = middleStart; i <= end; i++) {
            if (!result && !isBlank(img, i, height)) {
                start = i;
                result = true;
            }
            if (result && isBlank(img, i, height)) {
                end = i - 1;
                break;
            }
        }
        imgs[1] = img.getSubimage(start, 0, end - start + 1, height);
        imgs[1] = removeHeightBlank(imgs[1]);
        return imgs;
    }

    private static BufferedImage removeHeightBlank(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int start = 0;
        int end = height;
        for (int j = 0; j < height; j++) {
            int cnt = 0;
            for (int i = 0; i < width; i++) {
                if (img.getRGB(i, j) != -1) {
                    cnt = cnt + 1;
                }
            }
            if (cnt > 1) {
                start = j;
                break;
            }
        }
        for (int j = height - 1; j >= 0; j--) {
            int cnt = 0;
            for (int i = 0; i < width; i++) {
                if (img.getRGB(i, j) != -1) {
                    cnt = cnt + 1;
                }
            }
            if (cnt > 1) {
                end = j;
                break;
            }
        }
        return img.getSubimage(0, start, width, end - start + 1);
    }

    private static Map<BufferedImage, String> loadTrainData(Boolean isFront) throws Exception {
        Map<BufferedImage, String> codes = new HashMap<BufferedImage, String>();
        File dir = new File(getResourcePath() + "code/");
        File[] files = dir.listFiles();
        for (File file : files) {
            String name = file.getName();
            if (isFront && name.startsWith("f")) {
                codes.put(ImageIO.read(file), name.substring(1, name.length() - 4));
            } else if (!isFront && name.startsWith("b")) {
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

    private static String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> trainedData, Boolean isFront) {
        String result = "";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = 0;

        Label1: for (BufferedImage item : trainedData.keySet()) {
            // printImg(img);
            // System.out.println("========================================");
            // System.out.println(trainedData.get(item));
            // printImg(item);
            // System.out.println("========================================");
            int count = 0;
            int codeWidth = item.getWidth();
            int codeHeight = item.getHeight();
            if (height - codeHeight > 1 || codeHeight - height > 1) {
                continue;
            }
            int boundWidth = width < codeWidth ? width : codeWidth;
            int boundHeight = height < codeHeight ? height : codeHeight;
            if (isFront) {
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

    public static String[] ocr(File file, Boolean isFront) throws Exception {
        BufferedImage downloadImg = ImageIO.read(file);

        int width = downloadImg.getWidth();
        int height = downloadImg.getHeight();
        downloadImg = grayImage(downloadImg, width, height);
        downloadImg = removeSingle(downloadImg, width, height);
        BufferedImage[] singleCode = splitImage(downloadImg, width, height);

        Map<BufferedImage, String> codes = loadTrainData(isFront);
        String[] result = new String[3];
        for (int i = 0; i < 3; i++) {
            // File newFile = new File("/Users/zhengliliang/Desktop/p/test"+i+".jpg");
            // ImageIO.write(singleCode[i], "jpg", newFile);
            result[i] = getSingleCharOcr(singleCode[i], codes, isFront);
        }
        if (result[1].equals("minus")) {
            result[1] = "-";
        } else {
            result[1] = "+";
        }
        return result;
    }

    private static void trainData(File file) throws Exception {
        String name = file.getAbsolutePath();
        BufferedImage downloadImg = ImageIO.read(file);

        int width = downloadImg.getWidth();
        int height = downloadImg.getHeight();
        downloadImg = grayImage(downloadImg, width, height);
        downloadImg = removeSingle(downloadImg, width, height);
        BufferedImage[] singleCode = splitImage(downloadImg, width, height);

        String desPath = name.split("\\.")[0];
        for (int i = 0; i < 3; i++) {
            // printImg(singleCode[i]);
            // System.out.println("========================================");
            File newFile = new File(desPath + "_result" + i + ".jpg");
            ImageIO.write(singleCode[i], "jpg", newFile);
        }
        // File dir = new
        // File("/Users/zhengliliang/Desktop/p/getVerifyCode4_result1.jpg");
        // File dir = new
        // File("/Users/zhengliliang/Documents/ui/agent/src/main/resources/code/fplus.jpg");
        // printImg(ImageIO.read(dir));
    }

    // public static void main(String[] args) throws Exception {
    //     File dir = new File("/Users/zhengliliang/Desktop/p/");
    //     File[] files = dir.listFiles();
    //     for (File file : files) {
    //         if (!file.getName().endsWith("jpeg") && !file.getName().endsWith("png")) {
    //             continue;
    //         }
    //         // if(file.getName().contains("Captcha")){
    //         // trainData(file);
    //         // }

    //         String[] result = null;
    //         if (file.getName().contains("Code")) {
    //             // System.out.print(file.getName()+" ");
    //             // result=ocr(file, true);
    //             // System.out.println(result[0]+result[1]+result[2]);
    //         } else if (file.getName().contains("Captcha")) {
    //             System.out.print(file.getName() + " ");
    //             result = ocr(file, false);
    //             System.out.println(result[0] + result[1] + result[2]);
    //         }
    //     }
    // }
}