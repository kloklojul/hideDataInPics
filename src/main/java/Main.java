import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.types.proto.Album;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static String desktopPath = "D:\\Temp\\";
    private static File testArchive = new File(desktopPath + "killlakill.7z");
    private static int convertedBytes = 0;

    private static Credentials getUserCredentials(String credentialsPath, List<String> selectedScopes)
            throws IOException, GeneralSecurityException {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY, new InputStreamReader(new FileInputStream(credentialsPath)));
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        selectedScopes)
                        .setDataStoreFactory(new FileDataStoreFactory(DATA_STORE_DIR))
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder().setPort(LOCAL_RECEIVER_PORT).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(credential.getRefreshToken())
                .build();
    }

    public static void main(String[] args) throws IOException {
        /*
        //writeImage(exampleForSO(), desktopPath, "test.png");
        //writeImage(convertToImage(desktopPath + "test.7z"), desktopPath, "test.7z.png");
        //System.out.println(t.getTest());
        File image1F = new File(desktopPath + "testImnagergb.17688774.png");
        File image2F = new File(desktopPath + "testgoogle.17906971.png");

        System.out.println(testArchive.getAbsolutePath());
        writeImage(convertToImage(testArchive.getAbsolutePath()), desktopPath, "testImnagergb");
        //convertToBytes(image2F, desktopPath, "testNEW.7z");

         */
        // Set up the Photos Library Client that interacts with the API

        PhotosLibrarySettings settings =
                PhotosLibrarySettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create( getUserCredentials("C:\\Users\\AlxAlx\\Desktop\\credentials-for-photo.json", selectedScopes)))
                        .build();

        try (PhotosLibraryClient photosLibraryClient =
                     PhotosLibraryClient.initialize(settings)) {

            // Create a new Album  with at title
            Album createdAlbum = photosLibraryClient.createAlbum("My Test Album");

            // Get some properties from the album, such as its ID and product URL
            String id = createdAlbum.getId();
            String url = createdAlbum.getProductUrl();
            System.out.println(id + " url: " + url);

        } catch (ApiException e) {
            // Error during album creation
            System.out.printf("ERROR DURING ALBUM CREATION");
        }
    }

    public static File convertToBytes(File b2, String filepath, String filename) throws IOException {
        BufferedImage b1 = ImageIO.read(b2);
        File file = new File(filepath + filename);
        System.out.println(b2.getName());
        int bytesToDo = Integer.parseInt(b2.getName().split("\\.")[b2.getName().split("\\.").length-2]);

        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(file);

        int[] pixels = b1.getRGB(0,0,b1.getWidth(),b1.getHeight(),null,0,b1.getWidth());
        for (int i : pixels) {
            if (i != 0 && bytesToDo > 0) {
                int newPixel = i >> 16 & 0xff; // getting the red value
                outputStream.write(newPixel);
                bytesToDo--;
                if(bytesToDo > 0){
                    newPixel = (i & 0x0000FF00) >> 8; // getting the green value
                    outputStream.write(newPixel);
                    bytesToDo--;
                }
                if(bytesToDo > 0){
                    newPixel = (i & 0x000000FF); // getting the blue value
                    outputStream.write(newPixel);
                    bytesToDo--;
                }
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
                if((byteRead = inputStream.read()) != -1){
                    bytesConverted++;
                    green = byteRead;
                }
                if ((byteRead = inputStream.read()) != -1) {
                    bytesConverted++;
                    blue = byteRead;
                }
                imagePixels.add((alpha & 0xFF) << 24
                        | (red & 0xFF) << 16
                        | (green & 0xFF) << 8
                        | (blue & 0xFF));
                bytesConverted++;
            }

            int pixels = imagePixels.size();
            maxHeight = 1000;
            maxWidth = (int) Math.ceil(imagePixels.size()/maxHeight) + 1;
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
            convertedBytes = bytesConverted;
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
            File outputfile = new File(path + filename + "." + convertedBytes + ".png");
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
