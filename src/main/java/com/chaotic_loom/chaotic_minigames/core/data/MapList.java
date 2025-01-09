package com.chaotic_loom.chaotic_minigames.core.data;

import java.util.ArrayList;
import java.util.Random;

public class MapList<E> extends ArrayList<E> {
    private final Random random = new Random();

    public MapList<E> insert(E t) {
        super.add(t);
        return this;
    }

    public E getRandom() {
        if (this.isEmpty()) {
            throw new IllegalStateException("The list is empty. Cannot retrieve a random element.");
        }
        int index = random.nextInt(this.size());
        return this.get(index);
    }
}
