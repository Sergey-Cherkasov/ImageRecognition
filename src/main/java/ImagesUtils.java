import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImagesUtils {
   /**
    * Метод преобразует изображение в оттенках серого к бинаризованному изображению
    *
    * @return Бинаризованное изображение
    */
   public static BufferedImage convertGrayscaleToBinary(BufferedImage image) {
      // Получаем гистограмму изображения
      int[] histogram = createHistogramImage(image);
      // Получаем сумму всех интенсивностей изображения (Nt)
      int frequenciesSum = getFrequenciesSum(histogram);
      // Получаем среднее арифметическое изображения (Mt)
      int meanImage = getMeanImage(histogram, frequenciesSum);
      // Получаем глобальный порог бинаризации по методу Оцу
      int globalThreshold = getThresholdOtsu(histogram, frequenciesSum, meanImage);
      return getBinaryImage(image, globalThreshold);
   }

   /**
    * Метод возвращает среднее арифметическое (Mt = sum(i * p(i)) / Nt, i = 0,max(G))
    *
    * @return Среднее арифметическое изображения
    */
   private static int getMeanImage(int[] histogram, int frequenciesSum) {
      int mean = 0;
      for (int i = 0; i < Constants.INTENSITY_LAYER_NUMBER; i++) {
         mean += i * histogram[i];
      }
      return mean / frequenciesSum;
   }

   /**
    * Метод заменяет цвета пикселей изображения в оттенках серого в зависимости
    * от глобального порога бинаризации
    *
    * @return Преобразованное изображение
    */
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

   /**
    * Метод вычисляет и возвращает значение глобального порога бинаризации по методу Оцу
    *
    * @return Глобальный порог бинаризации
    */
   public static int getThresholdOtsu(int[] histogram, int frequenciesSum, int meanImage) {
      int globalThreshold = 0;
      double maxDispersion = 0.0;
      int firstClassCountPixels = 0;
      int firstClassIntensitySum = 0;
      // Для каждого значения t=1,max(G)
      for (int threshold = 0; threshold < Constants.INTENSITY_LAYER_NUMBER - 1; threshold++) {
         // Вычисляем вероятность первого класса (w1(t) = sum(p(i)) / Nt, i = 0,t-1)
         // Для этого вычисляем sum(p(i), i = 0,t-1)
         firstClassCountPixels += histogram[threshold];
         double firstClassProbability = firstClassCountPixels / (double) frequenciesSum;
         // Вычисляем вероятность второго класса (w2 = 1 - w1)
         double secondClassProbability = 1 - firstClassProbability;
         firstClassIntensitySum += threshold * histogram[threshold];
         // Вычисляем средние арифметические первого и второго классов
         double firstClassMean = firstClassIntensitySum / (frequenciesSum * firstClassProbability);
         double secondClassMean = (meanImage - firstClassIntensitySum * firstClassProbability) /
                 secondClassProbability;
         // Вычисляем разность средних арифметических значений первого и второго классов (m1(t) - m2(t))
         double meanDelta = firstClassMean - secondClassMean;
         // Вычисляем дисперсию между классами: w1(t) * w2(t) * [m1(t) - m2(t)]^2
         double dispersion = firstClassProbability * secondClassProbability * (meanDelta * meanDelta);
         // Если полученное значение дисперсии больше имеющейся, запоминаем новое значение дисперсии
         // и значение порога t
         if (dispersion > maxDispersion) {
            maxDispersion = dispersion;
            globalThreshold = threshold;
         }
      }
      return globalThreshold;
   }

   /**
    * Метод возвращает сумму частот интенсивности (Nt = sum(p(i)), i = 0,max(G))
    */
   public static int getFrequenciesSum(int[] histogram) {
      return Arrays.stream(histogram).sum();
   }

   /**
    * Метод вычисления гистограммы изображения
    *
    * @return Гистограмма
    */
   public static int[] createHistogramImage(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      // Инициализируем гистограмму
      int[] histogram = new int[Constants.INTENSITY_LAYER_NUMBER];
      // Заполняем гистограмму 0
      Arrays.fill(histogram, 0);
      // Вычисляем гистограмму изображения и частоту интенсивности в красном цветовом канале
      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int r = redChannel(new Color(image.getRGB(x, y)));
            histogram[r] += 1;
         }
      }
      // Возвращаем гистограмму
      return histogram;
   }

   /** Возвращает значение цвета пиксела в красном цветовом канале
    *
    * @return Значение цвета пиксела в красном цветовом канале
    */
   private static int redChannel(Color c) {
      return c.getRed();
   }

   /** Возвращает значение цвета пиксела в зеленом цветовом канале
    *
    * @return Значение цвета пиксела в зеленом цветовом канале
    */
   private static int greenChannel (Color c) {
      return c.getGreen();
   }

   /** Возвращает значение цвета пиксела в синем цветовом канале
    *
    * @return Значение цвета пиксела в синем цветовом канале
    */
   private static int blueChannel (Color c) {
      return c.getBlue();
   }

   /**
    * Возвращает значение яркости пиксела.
    * Вычисление яркости осуществляется по формуле: Y = 0.3R + 0.59G + 0.11B
    *
    * @return Значение яркости пиксела
    */
   private static int brightnessPixel (Color c) {
      return (int) (c.getRed() * 0.3 + c.getGreen() * 0.59 + c.getBlue() * 0.11);
   }

   /**
    * Преобразование изображения к оттенкам серого выполняется по формуле:
    * y = 0.2126 * r + 0.7152 * g + 0.0722 * b
    *
    * @param image Изображение для преобразования
    * @return Изображение в оттенках серого
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