package GDK.engine.components;

import GDK.engine.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem extends Component {
    public enum ParticleType {
        SQUARE, CIRCLE, IMAGE, TRIANGLE
    }

    public enum MovementType {
        LINEAR, EXPLOSION, GRAVITY, FOUNTAIN
    }

    public static class Particle {
        public Vector2 position;
        public Vector2 velocity;
        public Vector2 acceleration;
        public double size;
        public double lifeTime;
        public double maxLifeTime;
        public Color color;
        public double rotation;
        public double rotationSpeed;
        public double alpha;
        public ParticleType type;
        public String imagePath;
        public Node node;

        public Particle(Vector2 position, Vector2 velocity, double size, double lifeTime,
                        Color color, ParticleType type, String imagePath) {
            this.position = position;
            this.velocity = velocity;
            this.acceleration = new Vector2(0, 0);
            this.size = size;
            this.lifeTime = lifeTime;
            this.maxLifeTime = lifeTime;
            this.color = color;
            this.rotation = 0;
            this.rotationSpeed = 0;
            this.alpha = 1.0;
            this.type = type;
            this.imagePath = imagePath;
        }
    }

    // Настройки системы частиц
    public boolean isPlaying = true;
    public boolean destroyOnEnd = false;
    public boolean loop = true;
    public double duration = 5.0;
    public double startDelay = 0.0;

    // Настройки эмиттера
    public Vector2 emitterPosition = new Vector2(0, 0);
    public double emissionRate = 10.0; // частиц в секунду
    public int maxParticles = 100;

    // Настройки частиц
    public ParticleType particleType = ParticleType.SQUARE;
    public String particleImagePath = "";
    public MovementType movementType = MovementType.LINEAR;
    public double startSpeed = 50.0;
    public double startSpeedVariation = 10.0;
    public double startSize = 10.0;
    public double startSizeVariation = 2.0;
    public double lifeTime = 2.0;
    public double lifeTimeVariation = 0.5;
    public Color startColor = Color.WHITE;
    public boolean fadeOut = true;

    // Настройки направления
    public double angle = 0.0; // 0 - вверх, 90 - вправо, 180 - вниз, 270 - влево
    public double angleSpread = 45.0; // разброс угла

    // Настройки гравитации
    public Vector2 gravity = new Vector2(0, 98.0); // стандартная гравитация
    public boolean useGravity = false;

    // Настройки вращения
    public double startRotation = 0.0;
    public double rotationSpeed = 0.0;
    public double rotationSpeedVariation = 0.0;

    // Внутренние переменные
    private ArrayList<Particle> particles = new ArrayList<>();
    private double emissionTimer = 0.0;
    private double systemTimer = 0.0;
    private boolean hasStarted = false;
    private Random random = new Random();

    public ParticleSystem(GameObject gameObject) {
        super(gameObject);
    }

    public ParticleSystem(GameObject gameObject, String[] args) {
        super(gameObject);
        // Парсинг параметров из строки
        if (args.length > 0) isPlaying = Boolean.parseBoolean(args[0]);
        if (args.length > 1) destroyOnEnd = Boolean.parseBoolean(args[1]);
        if (args.length > 2) loop = Boolean.parseBoolean(args[2]);
        if (args.length > 3) duration = Double.parseDouble(args[3]);
        if (args.length > 4) maxParticles = Integer.parseInt(args[4]);
        if (args.length > 5) emissionRate = Double.parseDouble(args[5]);
        if (args.length > 6) particleType = ParticleType.valueOf(args[6]);
        if (args.length > 7) movementType = MovementType.valueOf(args[7]);
        if (args.length > 8) startSpeed = Double.parseDouble(args[8]);
        if (args.length > 9) lifeTime = Double.parseDouble(args[9]);
        if (args.length > 10) startSize = Double.parseDouble(args[10]);
        if (args.length > 11) startColor = Color.web(args[11]);
        if (args.length > 12) angle = Double.parseDouble(args[12]);
        if (args.length > 13) angleSpread = Double.parseDouble(args[13]);
        if (args.length > 14) useGravity = Boolean.parseBoolean(args[14]);
        if (args.length > 15) fadeOut = Boolean.parseBoolean(args[15]);
        if (args.length > 16) particleImagePath = args[16];
    }

    @Override
    public void update() {
        if (!hasStarted) {
            systemTimer += Time.deltaTime();
            if (systemTimer >= startDelay) {
                hasStarted = true;
                systemTimer = 0.0;
            }
            return;
        }

        if (!isPlaying) return;

        // Обновление таймера системы
        systemTimer += Time.deltaTime();

        // Проверка окончания системы частиц
        if (!loop && systemTimer >= duration) {
            isPlaying = false;
            if (destroyOnEnd && particles.isEmpty()) {
                gameObject.destroy();
            }
            return;
        }

        // Сброс системы если loop включен
        if (loop && systemTimer >= duration) {
            systemTimer = 0.0;
            clearParticles();
        }

        // Эмиссия новых частиц
        emissionTimer += Time.deltaTime();
        double emissionInterval = 1.0 / emissionRate;

        while (emissionTimer >= emissionInterval && particles.size() < maxParticles && isPlaying) {
            emitParticle();
            emissionTimer -= emissionInterval;
        }

        // Обновление существующих частиц
        updateParticles();

        // Удаление мертвых частиц
        removeDeadParticles();
    }

    private void emitParticle() {
        // Случайные вариации
        double speed = startSpeed + (random.nextDouble() - 0.5) * startSpeedVariation;
        double particleLifeTime = lifeTime + (random.nextDouble() - 0.5) * lifeTimeVariation;
        double size = startSize + (random.nextDouble() - 0.5) * startSizeVariation;
        double particleAngle = angle + (random.nextDouble() - 0.5) * angleSpread;
        double rotSpeed = rotationSpeed + (random.nextDouble() - 0.5) * rotationSpeedVariation;

        // Направление скорости
        double radAngle = Math.toRadians(particleAngle);
        Vector2 direction = new Vector2(Math.sin(radAngle), -Math.cos(radAngle));
        Vector2 velocity = direction.increaseVector(speed);

        // Создание частицы
        Particle particle = new Particle(
                new Vector2(emitterPosition.x, emitterPosition.y),
                velocity,
                size,
                particleLifeTime,
                startColor,
                particleType,
                particleImagePath
        );

        particle.rotation = startRotation;
        particle.rotationSpeed = rotSpeed;

        // Настройка движения в зависимости от типа
        setupParticleMovement(particle);

        // Создание визуального представления
        createParticleNode(particle);

        particles.add(particle);
    }

    private void setupParticleMovement(Particle particle) {
        switch (movementType) {
            case LINEAR:
                // Линейное движение - ничего дополнительного не нужно
                break;

            case EXPLOSION:
                // Взрыв - случайное направление от центра
                double explosionAngle = random.nextDouble() * 360.0;
                double explosionSpeed = startSpeed + (random.nextDouble() - 0.5) * startSpeedVariation;
                double radExplosion = Math.toRadians(explosionAngle);
                particle.velocity = new Vector2(
                        Math.sin(radExplosion) * explosionSpeed,
                        -Math.cos(radExplosion) * explosionSpeed
                );
                break;

            case GRAVITY:
                // Гравитация - устанавливаем ускорение
                particle.acceleration = new Vector2(gravity.x, gravity.y);
                useGravity = true;
                break;

            case FOUNTAIN:
                // Фонтан - гравитация с начальным направлением вверх
                particle.acceleration = new Vector2(0, 98.0);
                useGravity = true;
                break;
        }

        // Применяем гравитацию если включена
        if (useGravity && movementType != MovementType.GRAVITY && movementType != MovementType.FOUNTAIN) {
            particle.acceleration = new Vector2(gravity.x, gravity.y);
        }
    }

    private void createParticleNode(Particle particle) {
        switch (particle.type) {
            case SQUARE:
                Rectangle square = new Rectangle(particle.size, particle.size);
                square.setFill(particle.color);
                particle.node = square;
                break;

            case CIRCLE:
                javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle(particle.size / 2);
                circle.setFill(particle.color);
                particle.node = circle;
                break;

            case TRIANGLE:
                javafx.scene.shape.Polygon triangle = new javafx.scene.shape.Polygon();
                triangle.getPoints().addAll(
                        0.0, -particle.size / 2,
                        particle.size / 2, particle.size / 2,
                        -particle.size / 2, particle.size / 2
                );
                triangle.setFill(particle.color);
                particle.node = triangle;
                break;

            case IMAGE:
                if (!particle.imagePath.isEmpty()) {
                    Image image = ImageComponent.createImage(Config.PATH_PROJECT + particle.imagePath);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(particle.size);
                    imageView.setFitHeight(particle.size);
                    particle.node = imageView;
                } else {
                    // Fallback to square if no image path
                    Rectangle fallback = new Rectangle(particle.size, particle.size);
                    fallback.setFill(particle.color);
                    particle.node = fallback;
                }
                break;
        }

        if (particle.node != null) {
            particle.node.setViewOrder(gameObject.getViewOrder());
            gameObject.nodes.add(particle.node);
            Main.addWidget(particle.node);
        }
    }

    private void updateParticles() {
        double deltaTime = Time.deltaTime();

        for (Particle particle : particles) {
            // Обновление времени жизни
            particle.lifeTime -= deltaTime;

            // Обновление позиции
            particle.velocity = particle.velocity.addVector(particle.acceleration.increaseVector(deltaTime));
            particle.position = particle.position.addVector(particle.velocity.increaseVector(deltaTime));

            // Обновление вращения
            particle.rotation += particle.rotationSpeed * deltaTime;

            // Обновление прозрачности
            if (fadeOut) {
                particle.alpha = particle.lifeTime / particle.maxLifeTime;
            }

            // Обновление визуального представления
            updateParticleNode(particle);
        }
    }

    private void updateParticleNode(Particle particle) {
        if (particle.node == null) return;

        // Позиция с учетом transform gameObject и масштаба экрана
        double worldX = (gameObject.transform.position.x + particle.position.x) * Screen.scale.x;
        double worldY = (gameObject.transform.position.y + particle.position.y) * Screen.scale.y;

        particle.node.setLayoutX(worldX);
        particle.node.setLayoutY(worldY);
        particle.node.setRotate(particle.rotation);
        particle.node.setOpacity(particle.alpha);

        // Обновление цвета если нужно
        if (particle.node instanceof Shape) {
            Shape shape = (Shape) particle.node;
            Color currentColor = particle.color.deriveColor(0, 1, 1, particle.alpha);
            shape.setFill(currentColor);
        }
    }

    private void removeDeadParticles() {
        ArrayList<Particle> deadParticles = new ArrayList<>();

        for (Particle particle : particles) {
            if (particle.lifeTime <= 0) {
                deadParticles.add(particle);
            }
        }

        for (Particle deadParticle : deadParticles) {
            if (deadParticle.node != null) {
                gameObject.nodes.remove(deadParticle.node);
                Main.removeWidget(deadParticle.node);
            }
            particles.remove(deadParticle);
        }
    }

    public void play() {
        isPlaying = true;
        hasStarted = false;
        systemTimer = 0.0;
    }

    public void stop() {
        isPlaying = false;
    }

    public void clearParticles() {
        for (Particle particle : particles) {
            if (particle.node != null) {
                gameObject.nodes.remove(particle.node);
                Main.removeWidget(particle.node);
            }
        }
        particles.clear();
    }

    public void setEmitterPosition(double x, double y) {
        emitterPosition = new Vector2(x, y);
    }

    public void setEmitterPosition(Vector2 position) {
        emitterPosition = position;
    }

    public void setGravity(double x, double y) {
        gravity = new Vector2(x, y);
    }

    public int getParticleCount() {
        return particles.size();
    }

    public boolean isAlive() {
        return isPlaying || !particles.isEmpty();
    }

    // Метод для создания системы частиц через GameObject.getComponents
    public static ParticleSystem create(GameObject gameObject, String[] args) {
        return new ParticleSystem(gameObject, args);
    }
}