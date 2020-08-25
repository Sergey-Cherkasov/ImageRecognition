public class Constants {
   // Пути до места расположения шаблонов изображений
   public static final String TEMPLATES_IMAGES_PATH_RANKS = "D:/ImageRecognition/image_recognition/templates/ranks";
   public static final String TEMPLATES_IMAGES_PATH_SUITS = "D:/ImageRecognition/image_recognition/templates/suits";
   // Координаты начала расположения карт
   public static final int[] ARRAY_CARD_START_POINTS_X = {143, 215, 287, 359, 431};
   public static final int CARD_START_POINT_Y = 586;
   // Ширина зоны распознавания достоинства
   public static final int ZONE_RANK_WIDTH = 32;
   // Высота зоны распознавания достоинства
   public static final int ZONE_RANK_HEIGHT = 25;
   // Ширина зоны распознавания масти
   public static final int ZONE_SUIT_WIDTH = 32;
   // Высота зоны распознавания масти
   public static final int ZONE_SUIT_HEIGHT = 36;
   // Смещение от начала границ карты
   public static final int OFFSET_RANK_X = 4;
   public static final int OFFSET_RANK_Y = 5;
   public static final int OFFSET_SUIT_X = 26;
   public static final int OFFSET_SUIT_Y = 46;
   // Количество уровней интенсивности у изображения.
   // Стандартно для серого изображения - это 256 (от 0 до 255).
   public static final int INTENSITY_LAYER_NUMBER = 256;
   //Процент совпадения
   public static final double MATCH_PERCENTAGE = 0.82;
   // Цвета пикселей
   public static final int BLACK_COLOR = 0x000000;
   public static final int WHITE_COLOR = 0xFFFFFF;
}