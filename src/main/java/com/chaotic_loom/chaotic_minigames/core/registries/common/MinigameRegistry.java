package com.chaotic_loom.chaotic_minigames.core.registries.common;

import com.chaotic_loom.chaotic_minigames.annotations.Minigame;
import com.chaotic_loom.chaotic_minigames.core.minigames.GenericMinigame;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.core.annotations.Registration;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Registration(side = ExecutionSide.COMMON)
public class MinigameRegistry {
    public static List<GenericMinigame> MINIGAMES = new ArrayList<>();

    public static void register() {
        Reflections reflections = new Reflections("com.chaotic_loom.chaotic_minigames.core.minigames");
        Set<Class<?>> miniGames = reflections.getTypesAnnotatedWith(Minigame.class);

        for (Class<?> miniGameClass : miniGames) {
            try {
                GenericMinigame miniGame = (GenericMinigame) miniGameClass.getDeclaredConstructor().newInstance();
                System.out.println("Registered: " + miniGame.getClass().getSimpleName());
                MINIGAMES.add(miniGame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
