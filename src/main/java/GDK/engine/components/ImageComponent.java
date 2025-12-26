package GDK.engine.components;

import GDK.engine.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.File;

public class ImageComponent extends Component {
    public String pathImage;
    public double opacity;
    public ImageView imageView;
    Image imageDefault;
    private boolean pixelPerfect = true;
    private Vector2 padding = new Vector2(0, 0); // Отступы
    private boolean preserveAspectRatio = false; // Сохранение пропорций

    public ImageComponent(GameObject gameObject, String pathImage, String opacity){
        super(gameObject);
        this.pathImage = Config.PATH_PROJECT + pathImage;
        this.opacity = Double.parseDouble(opacity);
        imageDefault = createImage(this.pathImage);
        imageView = new ImageView(imageDefault);

        applyPixelPerfectSettings();
        applyAspectRatioSettings();

        imageView.setOpacity(this.opacity);
        imageView.setCache(true);

        gameObject.nodes.add(imageView);
    }

    // Конструктор с дополнительными параметрами
    public ImageComponent(GameObject gameObject, String[] args) {
        super(gameObject);
        this.pathImage = Config.PATH_PROJECT + args[0];
        this.opacity = Double.parseDouble(args[1]);

        // Парсим дополнительные параметры если есть
        if (args.length > 2) {
            this.pixelPerfect = Boolean.parseBoolean(args[2]);
        }
        if (args.length > 3) {
            this.preserveAspectRatio = Boolean.parseBoolean(args[3]);
        }
        if (args.length > 4) {
            String[] paddingValues = args[4].split(",");
            if (paddingValues.length == 2) {
                this.padding = new Vector2(
                        Double.parseDouble(paddingValues[0]),
                        Double.parseDouble(paddingValues[1])
                );
            }
        }

        imageDefault = createImage(this.pathImage);
        imageView = new ImageView(imageDefault);

        applyPixelPerfectSettings();
        applyAspectRatioSettings();

        imageView.setOpacity(this.opacity);
        imageView.setCache(true);

        gameObject.nodes.add(imageView);
    }

    public static Image createImage(String pathImage){
        return new Image(new File(pathImage).toURI().toString());
    }

    /**
     * Применяет настройки для четкого пиксельного отображения
     */
    private void applyPixelPerfectSettings() {
        if (pixelPerfect) {
            imageView.setSmooth(false);
            imageView.setCache(false);
            imageView.setViewOrder(gameObject.getViewOrder());
        } else {
            imageView.setSmooth(true);
            imageView.setCache(true);
        }
    }

    /**
     * Применяет настройки для сохранения пропорций
     */
    private void applyAspectRatioSettings() {
        imageView.setPreserveRatio(preserveAspectRatio);
    }

    /**
     * Включает/выключает пиксель-перфект режим
     */
    public void setPixelPerfect(boolean pixelPerfect) {
        this.pixelPerfect = pixelPerfect;
        applyPixelPerfectSettings();
    }

    /**
     * Включает/выключает сохранение пропорций
     */
    public void setPreserveAspectRatio(boolean preserveAspectRatio) {
        this.preserveAspectRatio = preserveAspectRatio;
        applyAspectRatioSettings();
    }

    /**
     * Устанавливает отступы
     */
    public void setPadding(Vector2 padding) {
        this.padding = padding;
    }

    /**
     * Устанавливает отступы из числовых значений
     */
    public void setPadding(double paddingX, double paddingY) {
        this.padding = new Vector2(paddingX, paddingY);
    }

    /**
     * Возвращает текущие отступы
     */
    public Vector2 getPadding() {
        return padding;
    }

    public static Image createPixelArtImage(String pathImage) {
        Image image = new Image(
                new File(pathImage).toURI().toString(),
                false
        );
        return image;
    }

    public void setImageView(String pathImage){
        this.pathImage = Config.PATH_PROJECT + pathImage;
        if (pixelPerfect) {
            setImageView(createPixelArtImage(this.pathImage));
        } else {
            setImageView(createImage(this.pathImage));
        }
    }

    public void setImageView(Image image){
        imageView.setImage(image);
        applyPixelPerfectSettings();
        applyAspectRatioSettings();
    }

    public void setToDefault(){
        setImageView(imageDefault);
    }

    @Override
    public void update() {
        // Применяем отступы к позиции
        double posX = (gameObject.transform.position.x + padding.x) * Screen.scale.x;
        double posY = (gameObject.transform.position.y + padding.y) * Screen.scale.y;

        imageView.setLayoutX(posX);
        imageView.setLayoutY(posY);
        imageView.setRotate(gameObject.transform.getGlobalAngle());

        // Вычисляем доступный размер с учетом отступов
        double availableWidth = gameObject.transform.size.x - (padding.x * 2);
        double availableHeight = gameObject.transform.size.y - (padding.y * 2);

        if (preserveAspectRatio && imageDefault != null) {
            // Ручное центрирование при сохранении пропорций
            double aspectRatio = getAspectRatio();
            double scaledWidth, scaledHeight;

            // Вычисляем размеры с сохранением пропорций
            if (availableWidth / availableHeight > aspectRatio) {
                // Высота ограничивающая
                scaledHeight = availableHeight;
                scaledWidth = availableHeight * aspectRatio;
            } else {
                // Ширина ограничивающая
                scaledWidth = availableWidth;
                scaledHeight = availableWidth / aspectRatio;
            }

            // Центрируем изображение в доступной области
            double offsetX = (availableWidth - scaledWidth) / 2;
            double offsetY = (availableHeight - scaledHeight) / 2;

            // Обновляем позицию с учетом центрирования
            imageView.setLayoutX(posX + offsetX * Screen.scale.x);
            imageView.setLayoutY(posY + offsetY * Screen.scale.y);

            // Устанавливаем размеры
            if (pixelPerfect) {
                imageView.setFitWidth(Math.round(scaledWidth * Screen.scale.x));
                imageView.setFitHeight(Math.round(scaledHeight * Screen.scale.y));
            } else {
                imageView.setFitWidth(scaledWidth * Screen.scale.x);
                imageView.setFitHeight(scaledHeight * Screen.scale.y);
            }
        } else {
            // Обычная логика без сохранения пропорций
            if (pixelPerfect) {
                imageView.setFitWidth(Math.round(availableWidth * Screen.scale.x));
                imageView.setFitHeight(Math.round(availableHeight * Screen.scale.y));
            } else {
                imageView.setFitWidth(availableWidth * Screen.scale.x);
                imageView.setFitHeight(availableHeight * Screen.scale.y);
            }
        }

        // Для пиксель-перфект режима округляем позиции
        if (pixelPerfect) {
            imageView.setLayoutX(Math.round(imageView.getLayoutX()));
            imageView.setLayoutY(Math.round(imageView.getLayoutY()));
        }
    }
    /**
     * Устанавливает режим фильтрации
     */
    public void setFiltering(boolean enableFiltering) {
        if (enableFiltering) {
            imageView.setSmooth(true);
        } else {
            imageView.setSmooth(false);
        }
    }

    /**
     * Возвращает текущий режим пиксель-перфект
     */
    public boolean isPixelPerfect() {
        return pixelPerfect;
    }

    /**
     * Возвращает сохранение пропорций
     */
    public boolean isPreserveAspectRatio() {
        return preserveAspectRatio;
    }

    /**
     * Возвращает исходную ширину изображения
     */
    public double getOriginalWidth() {
        return imageDefault != null ? imageDefault.getWidth() : 0;
    }

    /**
     * Возвращает исходную высоту изображения
     */
    public double getOriginalHeight() {
        return imageDefault != null ? imageDefault.getHeight() : 0;
    }

    /**
     * Возвращает соотношение сторон исходного изображения
     */
    public double getAspectRatio() {
        if (imageDefault == null || imageDefault.getHeight() == 0) return 1;
        return imageDefault.getWidth() / imageDefault.getHeight();
    }

    public void setVisible(boolean b) {
        imageView.setVisible(b);
    }
}