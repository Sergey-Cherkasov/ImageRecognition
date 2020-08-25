/*
* Copyright by Sergey Cherkasov (c) 2020
* */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

   public static void main(String[] args) throws IOException {
      start(args);
   }

   private static void start(String[] args) throws IOException {
      String sourceImagesPath = "";
      if (args.length != 0){
         sourceImagesPath = args[0];
      } else {
         System.out.printf("Не указан путь до размещения файлов!%n");
         System.exit(0);
      }
      long timeStart = System.currentTimeMillis();
      File dirTemplatesRanks = new File(Constants.TEMPLATES_IMAGES_PATH_RANKS);
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
      System.out.println(sourceImagesPath);

      int numberAllFiles = 0;
      int notRecognizedFiles = 0;
      File sourceDir = new File(sourceImagesPath);
      File[] sourceFiles = sourceDir.listFiles();

      if (sourceFiles == null) {
         System.out.println("Отсутствуют изображения для распознавания в " + sourceDir.getPath());
         return;
      }

      for (File sourceFile : sourceFiles) {
         if (!sourceFile.isFile()) {
            continue;
         }
         numberAllFiles++;
         BufferedImage image = ImageIO.read(sourceFile);
         ImagesUtils.convertGrayscaleToBinary(image);
         String result = recognitionCards(sourceFile, mapTemplatesRanks, mapTemplatesSuits);
         if (result.matches(".*[?].*")) {
            notRecognizedFiles++;
         }
         System.out.printf("%s - %s%n", sourceFile.getName(), result);
      }

      long timeEnd = System.currentTimeMillis();
      int seconds = (int) (timeEnd - timeStart) / 1000;
      int minutes = seconds / 60;
      int hours = minutes / 60;
      System.out.println("Общее количество карт: " + numberAllFiles);
      System.out.println("Количество не распознанных карт: " + notRecognizedFiles);
      System.out.printf("Коэффициент не распознавания карт: %f%n", notRecognizedFiles / (double) numberAllFiles);
      System.out.printf("Время затраченное на распознавание всех файлов: %d:%d:%d%n", hours, minutes, seconds);
      System.out.printf("Усредненное время, затраченное на распознавание одной карты: %d:%d:%d%n",
              hours / numberAllFiles, minutes / numberAllFiles, seconds / numberAllFiles);
   }

   private static String recognitionCards(File sourceFile,
                                          Map<String, BufferedImage> mapTemplatesRanks,
                                          Map<String, BufferedImage> mapTemplatesSuits) throws IOException {
      StringBuilder resultString = new StringBuilder();
      BufferedImage sourceImage = ImageIO.read(sourceFile);
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
         resultString.append(String.format("%s%s", rank, suit));
      }
      return resultString.toString().replaceFirst("[?][?]+$","");
   }

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

   /* Метод по пиксельно сравнивает изображение и шаблон и возвращает true или false */
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
      Map<String, BufferedImage> mapTemplates = new HashMap<String, BufferedImage>();
      for (File file : filesTemplateRanks) {
         mapTemplates.put(file.getName().replaceFirst("[.][^.]+$", ""), ImageIO.read(file));
      }
      return mapTemplates;
   }
}