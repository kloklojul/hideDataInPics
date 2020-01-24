import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static String desktopPath = "C:\\Users\\AlxAlx\\Desktop\\";
    private static File testArchive = new File(desktopPath + "test.7z");

    public static void main(String[] args) throws IOException {
        //writeImage(exampleForSO(), desktopPath, "test.png");
        //writeImage(convertToImage(desktopPath + "test.7z"), desktopPath, "test.7z.png");
        //System.out.println(t.getTest());
        File image1F = new File(desktopPath + "testImnage.png");
        File image2F = new File(desktopPath + "test.2 (1).png");

        //BufferedImage image1 = ImageIO.read(image1F);
        //BufferedImage image2 = ImageIO.read(image2F);
        System.out.println(testArchive.getAbsolutePath());
        writeImage(convertToImage(testArchive.getAbsolutePath()), desktopPath, "testImnage.png");
        //convertToBytes(image1,desktopPath + "erjk.7z");
    }

    public static File convertToBytes(BufferedImage b1, String filepath) throws IOException {
        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(file);

        int[] pixels = b1.getRGB(0,0,b1.getWidth(),b1.getHeight(),null,0,b1.getWidth());
        for (int i : pixels){
            if (i != 0) {
                int newPixel = i >> 16 & 0xff; // getting the red value
                outputStream.write(newPixel);
            }
        }
        outputStream.flush();
        outputStream.close();
        return file;
        /*
        for(int i = 0; i < b1.getWidth(); i++) {
            for (int j = 0; j < b1.getHeight(); j++){
                System.out.println(b1.getRGB(i,j));
            }
        }

         */
    }

    public static BufferedImage convertToImage(String fileToConvert) {
        int maxWidth = 800;
        int maxHeight = 800;

        int alpha = (0xFF000000) >> 24;
        int red = (0 & 0x00FF0000) >> 16;
        int green = (0 & 0x0000FF00) >> 8;
        int blue = (0 & 0x000000FF) >> 0;

        try (
                InputStream inputStream = new FileInputStream(fileToConvert);
        ) {

            int byteRead;
            ArrayList<Integer> imagePixels = new ArrayList<Integer>();
            int bytesConverted = 0;
            while ((byteRead = inputStream.read()) != -1) {
                red = byteRead;
                imagePixels.add((alpha & 0xFF) << 24
                        | (red & 0xFF) << 16
                        | (green & 0xFF) << 8
                        | (blue & 0xFF));
                bytesConverted++;
            }

            int pixels = imagePixels.size();
            maxHeight = 1000;
            maxWidth = (int) Math.ceil(imagePixels.size()/maxHeight);
            maxWidth++;
            while (imagePixels.size() < maxHeight*maxWidth) {
                imagePixels.add(0);
            }
            System.out.println(maxHeight*maxWidth);
            BufferedImage imageOut =
                    new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_4BYTE_ABGR);
            int[] imageOutPixels = convertIntegers(imagePixels);

            System.out.println("PIXELS : " + imagePixels.size());
            imageOut.setRGB(0, 0, maxWidth, maxHeight, imageOutPixels, 0, maxWidth);

            System.out.println((imageOut.getHeight() * imageOut.getWidth()) + " pixels and only : " + bytesConverted + " bytes converted!");
            return imageOut;
        } catch (IOException ex) {
            ex.printStackTrace();
        }





/*
        for (int i = 0; i < pixels; i++) {
            int alpha = (0xFF000000) >> 24;
            int red = (0x00FF0000) >> 16;
            int green = (0x0000FF00) >> 8;
            int blue = (0x000000FF) >> 0;

            if(i%2 == 1) {
                 alpha = (0xFF000000 & 0xFF000000) >> 24;
                 red = (0x00000000) >> 16;
                 green = (0 & 0x0000FF00) >> 8;
                 blue = (0x000000FF & 0x000000FF) >> 0;
            } else {
                 alpha = (0xFF000000 & 0xFF000000) >> 24;
                 red = (0x00FF0000) >> 16;
                 green = (0 & 0x0000FF00) >> 8;
                 blue = (0 & 0x000000FF) >> 0;
            }


            // At last, store in output array:
            imageOutPixels[i] = (alpha & 0xFF) << 24
                    | (red & 0xFF) << 16
                    | (green & 0xFF) << 8
                    | (blue & 0xFF);

        }
        */

        return null;
    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    public static void writeImage(BufferedImage b, String path, String filename) {
        try {
            File outputfile = new File(path + filename);
            ImageIO.write(b, "png", outputfile);
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }
    }

    public static boolean compareImage(BufferedImage b1, BufferedImage b2) {
        if (b1.getHeight() == b2.getHeight() && b1.getWidth() == b2.getWidth()){
            for (int i = 0; i < b1.getWidth(); i++){
                for (int j = 0; j < b1.getHeight(); j++) {
                    System.out.println(" pixel i,j : " + i + ", " + j + " b1 : " + b1.getRGB(i,j) + " b2 : " + b2.getRGB(i,j));
                    if (b1.getRGB(i,j) != b2.getRGB(i,j)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

}
