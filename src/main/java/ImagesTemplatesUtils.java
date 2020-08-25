import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagesTemplatesUtils {
   // Названия файлов для шаблонов изображений
   private static final String[] NEW_FILES_NAMES_RANKS = {"2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png",
           "9.png", "10.png", "J.png", "Q.png", "K.png", "A.png"};
   private static final String[] NEW_FILES_NAMES_SUITS = {"c.png", "d.png", "h.png", "s.png"};
   // Пути к файлам изображений
   private static final String[] IMAGES_FILES_FOR_SUITS = {
           "D:/ImageRecognition/for_templates/20180821_091716.755_0x0EDA02B0.png",  // clubs
           "D:/ImageRecognition/for_templates/20180821_093119.384_0x0EDA02B0.png",  // diamonds
           "D:/ImageRecognition/for_templates/20180821_091328.300_0x0EDA02B0.png",  // hearts
           "D:/ImageRecognition/for_templates/20180821_091220.884_0x0EDA02B0.png"   // spades
   };
   private static final String[] IMAGES_FILES_FOR_RANKS = {
           "D:/ImageRecognition/for_templates/20180821_101152.948_0x0EDA02B0.png",  // 2
           "D:/ImageRecognition/for_templates/20180821_092500.887_0x0EDA02B0.png",  // 3
           "D:/ImageRecognition/for_templates/20180821_093525.214_0x0EDA02B0.png",  // 4
           "D:/ImageRecognition/for_templates/20180821_091716.755_0x0EDA02B0.png",  // 5
           "D:/ImageRecognition/for_templates/20180821_091842.811_0x0EDA02B0.png",  // 6
           "D:/ImageRecognition/for_templates/20180821_091126.795_0x0EDA02B0.png",  // 7
           "D:/ImageRecognition/for_templates/20180821_092548.239_0x0EDA02B0.png",  // 8
           "D:/ImageRecognition/for_templates/20180821_091328.300_0x0EDA02B0.png",  // 9
           "D:/ImageRecognition/for_templates/20180821_093622.029_0x0EDA02B0.png",  // 10
           "D:/ImageRecognition/for_templates/20180821_092841.907_0x0EDA02B0.png",  // J
           "D:/ImageRecognition/for_templates/20180821_091220.884_0x0EDA02B0.png",  // Q
           "D:/ImageRecognition/for_templates/20180821_092326.632_0x0EDA02B0.png",  // K
           "D:/ImageRecognition/for_templates/20180821_092407.903_0x0EDA02B0.png"   // A
   };
   // Координаты начала расположения карт
   private static final int[][] CARDS_START_POINTS = {{143, 586}};

   public static void createTemplateImages() throws IOException {
      getTemplatesFiles(IMAGES_FILES_FOR_SUITS, NEW_FILES_NAMES_SUITS, Constants.OFFSET_SUIT_X, Constants.OFFSET_SUIT_Y,
              Constants.ZONE_SUIT_WIDTH, Constants.ZONE_SUIT_HEIGHT, Constants.TEMPLATES_IMAGES_PATH_SUITS);
      getTemplatesFiles(IMAGES_FILES_FOR_RANKS, NEW_FILES_NAMES_RANKS, Constants.OFFSET_RANK_X, Constants.OFFSET_RANK_Y,
              Constants.ZONE_RANK_WIDTH, Constants.ZONE_RANK_HEIGHT, Constants.TEMPLATES_IMAGES_PATH_RANKS);
      File dir = new File(Constants.TEMPLATES_IMAGES_PATH_RANKS);
      if (dir.listFiles() != null) {
         imageConversion(dir);
      }
      dir = new File(Constants.TEMPLATES_IMAGES_PATH_SUITS);
      if (dir.listFiles() != null) {
         imageConversion(dir);
      }
   }

   private static void imageConversion(File dir) throws IOException {
      for (File file : dir.listFiles()) {
         BufferedImage image = ImageIO.read(file);
         image = ImagesUtils.convertToGreyscale(image);
         image = ImagesUtils.convertGrayscaleToBinary(image);
         ImageIO.write(image, "png", file);
         image.flush();
      }
   }

   private static void getTemplatesFiles(String[] imagesFiles, String[] newFilesNames, int offsetX,
                                            int offsetY, int zoneWidth, int zoneHeight,
                                            String templatesImagesPath) throws IOException {
      int i = 0;
      for (String path_file : imagesFiles) {
         File file = new File(path_file);
         if (file.exists() && file.isFile()) {
            BufferedImage image = ImageIO.read(file);
            for (int[] cardStartPoints : CARDS_START_POINTS) {
               Point pointXY = new Point(cardStartPoints[0] + offsetX,cardStartPoints[1] + offsetY);
               BufferedImage subImage = image.getSubimage(pointXY.x, pointXY.y, zoneWidth, zoneHeight);
               File tmpFile = new File(templatesImagesPath + "/" + newFilesNames[i]);
               ImageIO.write(subImage, "png", tmpFile);
               subImage.flush();
               image.flush();
               i++;
            }
         }
      }
   }
}