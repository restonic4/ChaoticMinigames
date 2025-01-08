package com.chaotic_loom.chaotic_minigames.util;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;

import java.util.function.Consumer;

public class ThreadUtils {
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {}
    }

    public static void runCountDown(int desiredSeconds, Consumer<Integer> consumer) {
        for (int i = desiredSeconds; i > 0; i--) {
            consumer.accept(i);
            sleep(1000);
        }
    }
}
