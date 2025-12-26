package GDK.engine;

import java.util.concurrent.ThreadLocalRandom;

public class Tools {
    public static int randomValue(int start, int end) {
        return ThreadLocalRandom.current().nextInt(start, end);
    }
}
