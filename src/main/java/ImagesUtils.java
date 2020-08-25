import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImagesUtils {

   public static BufferedImage convertGrayscaleToBinary(BufferedImage image) {

      int width = image.getWidth();
      int height = image.getHeight();
      int numberAllPixels = width * height;
      // Get image histogram
      int[] histogram = ImagesUtils.createHistogramImage(image, width, height);
      // Get sum of all intensities
      int sumBrightnessPixels = ImagesUtils.getSumBrightnessPixels(histogram);
      // Get the threshold of binarization
      int threshold = ImagesUtils.getThresholdOtsu(histogram, sumBrightnessPixels, numberAllPixels);
      return getBinaryImage(image, threshold);
   }

   private static BufferedImage getBinaryImage(BufferedImage image, int threshold) {
      for (int y = 0; y < image.getHeight(); y++) {
         for (int x = 0; x < image.getWidth(); x++) {
            int r = new Color(image.getRGB(x, y)).getRed();
            if (r > threshold) {
               image.setRGB(x, y, Constants.WHITE_COLOR);
            } else {
               image.setRGB(x, y, Constants.BLACK_COLOR);
            }
         }
      }
      return image;
   }

   public static int getThresholdOtsu(int[] histogram, int sumBrightnessPixels, int numberAllPixels) {

      int bestThreshold = 0;
      double bestSigma = 0.0;
      int firstClassPixelsCount = 0;
      int firstClassIntensitySum = 0;

      for (int threshold = 0; threshold < Constants.INTENSITY_LAYER_NUMBER - 1; threshold++) {
         firstClassPixelsCount += histogram[threshold];
         firstClassIntensitySum += threshold * histogram[threshold];

         double firstClassProbability = firstClassPixelsCount / (double) numberAllPixels;
         double secondClassProbability = 1.0 - firstClassProbability;
         double firstClassMean = firstClassPixelsCount == 0 ? 0.0 : firstClassIntensitySum /
                 (double) firstClassPixelsCount;
         double secondClassMean = (sumBrightnessPixels - firstClassIntensitySum) /
                 (double) (numberAllPixels - firstClassPixelsCount);
         double meanDelta = firstClassMean - secondClassMean;
         double sigma = firstClassProbability * secondClassProbability * (meanDelta * meanDelta);

         if (sigma > bestSigma) {
            bestSigma = sigma;
            bestThreshold = threshold;
         }

      }
      return bestThreshold;
   }

   /*
    * Метод возвращает сумму яркостей пикселей
    */
   public static int getSumBrightnessPixels(int[] histogram) {
      int sum = 0;

      for (int i : histogram) {
         sum += i;
      }
      return sum;
   }

   /*
    * Метод создания гистограммы изображения
    */
   public static int[] createHistogramImage(BufferedImage image, int width, int height) {
      int[] histogram = new int[Constants.INTENSITY_LAYER_NUMBER];

      Arrays.fill(histogram, 0);
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int r = new Color(image.getRGB(x, y)).getRed();
            histogram[r] += 1;
         }
      }
      return histogram;
   }

   /*
    * Преобразование изображения к оттенкам серого выполняется по формуле:
    * y = 0.2126 * r + 0.7152 * g + 0.0722 * b
    */
   public static BufferedImage convertToGreyscale(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int r = new Color(image.getRGB(x, y)).getRed();
            int g = new Color(image.getRGB(x, y)).getGreen();
            int b = new Color(image.getRGB(x, y)).getBlue();

            r = (int) (r * 0.2126);
            g = (int) (g * 0.7152);
            b = (int) (b * 0.0722);

            image.setRGB(x, y, new Color(r, g, b).getRGB());
         }
      }
      return image;
   }
}