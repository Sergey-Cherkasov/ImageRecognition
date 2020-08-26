import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Main {

   static int numberAllFiles;
   static int numberNotRecognizedFiles;

   public static void main(String[] args) throws IOException {
      String sourceImagesPath = "";
      if (args.length != 0){
         sourceImagesPath = args[0];
      } else {
         System.out.println("Не указан путь до размещения файлов с изображениями!");
         System.exit(0);
      }
      start(sourceImagesPath);
   }

   private static void start(String path) throws IOException {
      long timeStart = System.currentTimeMillis();
      File dirTemplatesRanks = new File(Constants.TEMPLATES_IMAGES_PATH_RANKS);
      if (!dirTemplatesRanks.exists()) {
         System.out.println("Отсутствует каталог с шаблонами");
         return;
      }
      File[] filesTemplateRanks = dirTemplatesRanks.listFiles();
      if (filesTemplateRanks == null) {
         System.out.println("Отсутствуют шаблоны достоинств карт: " + dirTemplatesRanks.getPath());
         return;
      }
      Map<String, BufferedImage> mapTemplatesRanks = fillTemplatesMap(filesTemplateRanks);

      File dirTemplatesSuits = new File(Constants.TEMPLATES_IMAGES_PATH_SUITS);
      File[] filesTemplatesSuits = dirTemplatesSuits.listFiles();
      if (filesTemplatesSuits == null) {
         System.out.println("Отсутствуют шаблоны мастей карт: " + dirTemplatesSuits.getPath());
         return;
      }
      Map<String, BufferedImage> mapTemplatesSuits = fillTemplatesMap(filesTemplatesSuits);

      File sourceDir = new File(path);
      File[] sourceFiles = sourceDir.listFiles();
      if (sourceFiles == null) {
         System.out.println("Отсутствуют изображения для распознавания в " + sourceDir.getPath());
         return;
      }
      Stream<String> resultStrings = Arrays.stream(sourceFiles).filter(File::isFile)
              .map((sourceFile) -> recognitionCards(sourceFile, mapTemplatesRanks, mapTemplatesSuits));
      resultStrings.forEach((System.out::println));

      long timeEnd = System.currentTimeMillis();
      int seconds = (int) (timeEnd - timeStart) / 1000;
      int minutes = seconds / 60;
      int hours = minutes / 60;
      System.out.println("Общее количество файлов: " + numberAllFiles);
      System.out.println("Количество не распознанных файлов: " + numberNotRecognizedFiles);
      System.out.printf("Коэффициент не распознавания файлов: %f%n", numberNotRecognizedFiles / (double) numberAllFiles);
      System.out.printf("Время затраченное на распознавание всех файлов: %d:%d:%d%n", hours, minutes, seconds);
      System.out.printf("Усредненное время, затраченное на распознавание одного файла: %d:%d:%d%n",
              hours / numberAllFiles, minutes / numberAllFiles, seconds / numberAllFiles);
   }

   /**
    * Метод возращает строку с описанием достоинства и масти распознанных карт на изображении
    * @author Sergey Cherkasov
    */
   private static String recognitionCards(File sourceFile,
                                          Map<String, BufferedImage> mapTemplatesRanks,
                                          Map<String, BufferedImage> mapTemplatesSuits) {
      numberAllFiles++;
      BufferedImage sourceImage = null;
      try {
         sourceImage = ImageIO.read(sourceFile);
         if (sourceImage == null) {
            System.out.printf("Файл %s не содержит изображение%n", sourceFile.getName());
            return "";
         } else if (sourceImage.getHeight() != Constants.IMAGE_HEIGHT || sourceImage.getWidth() != Constants.IMAGE_WIDTH) {
            System.out.println("Не верный размер изображения: " + sourceFile.getName());
            return "";
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      StringBuilder stringBuilder = new StringBuilder();
      String rank;
      String suit;
      for (int cardStartPoint : Constants.ARRAY_CARD_START_POINTS_X) {
         Point startPointRank = new Point(cardStartPoint + Constants.OFFSET_RANK_X,
                 Constants.CARD_START_POINT_Y + Constants.OFFSET_RANK_Y);
         BufferedImage sourceImageRankZone = sourceImage.getSubimage(startPointRank.x, startPointRank.y,
                 Constants.ZONE_RANK_WIDTH, Constants.ZONE_RANK_HEIGHT);
         sourceImageRankZone = ImagesUtils.convertToGreyscale(sourceImageRankZone);
         sourceImageRankZone = ImagesUtils.convertGrayscaleToBinary(sourceImageRankZone);
         Point startPointSuit = new Point(cardStartPoint + Constants.OFFSET_SUIT_X,
                 Constants.CARD_START_POINT_Y + Constants.OFFSET_SUIT_Y);
         BufferedImage sourceImageSuitZone = sourceImage.getSubimage(startPointSuit.x, startPointSuit.y,
                 Constants.ZONE_SUIT_WIDTH, Constants.ZONE_SUIT_HEIGHT);
         sourceImageSuitZone = ImagesUtils.convertToGreyscale(sourceImageSuitZone);
         sourceImageSuitZone = ImagesUtils.convertGrayscaleToBinary(sourceImageSuitZone);
         rank = imageCompare(sourceImageRankZone, mapTemplatesRanks);
         suit = imageCompare(sourceImageSuitZone, mapTemplatesSuits);
         stringBuilder.append(String.format("%s%s", rank, suit));
      }
      String resultString = stringBuilder.toString().replaceFirst("[?][?]+$","");
      if (resultString.matches(".*[?].*")) {
         numberNotRecognizedFiles++;
      }
      return String.format("%s - %s", sourceFile.getName(), resultString);
   }

   /**
    * Метод возвращает строку с указанием на достоинство/масть карты
    * @author Sergey Cherkasov
    */
   private static String imageCompare(BufferedImage sourceImage, Map<String, BufferedImage> mapTemplates) {
      int numberBestPixels = 0;
      String rankSuit = "";
      for (Map.Entry<String, BufferedImage> templateRankSuit : mapTemplates.entrySet()) {
         BufferedImage templateImage = templateRankSuit.getValue();
         int allPixels = templateImage.getWidth() * templateImage.getHeight();
         int numberUsefulPixels = pixelCompare(sourceImage, templateImage);
         templateImage.flush();
         double matchPercentage = numberUsefulPixels / (double) allPixels;
         if (matchPercentage >= Constants.MATCH_PERCENTAGE) {
            if (numberUsefulPixels > numberBestPixels) {
               numberBestPixels = numberUsefulPixels;
               rankSuit = templateRankSuit.getKey();
            }
         }
      }
      return rankSuit.equals("")? "?": rankSuit;
   }

   /**
    * Метод по пиксельно сравнивает изображение и шаблон и возвращает количество
    * полезных пикселей
    * @author Sergey Cherkasov
    */
   private static int pixelCompare(BufferedImage source, BufferedImage template) {
      int width = template.getWidth();
      int height = template.getHeight();
      int numberUsefulPixels = 0;
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            if (source.getRGB(x, y) == template.getRGB(x, y)) {
               numberUsefulPixels++;
            }
         }
      }
      return numberUsefulPixels;
   }

   private static Map<String, BufferedImage> fillTemplatesMap(File[] filesTemplateRanks) throws IOException {
      Map<String, BufferedImage> mapTemplates = new HashMap<>();
      for (File file : filesTemplateRanks) {
         mapTemplates.put(file.getName().replaceFirst("[.][^.]+$", ""), ImageIO.read(file));
      }
      return mapTemplates;
   }
}