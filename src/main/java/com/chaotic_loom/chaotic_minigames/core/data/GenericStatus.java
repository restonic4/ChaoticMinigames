package com.chaotic_loom.chaotic_minigames.core.data;

public class GenericStatus {
    private final long startedAt;

    public GenericStatus() {
        this.startedAt = System.currentTimeMillis();
    }

    public long getStartedAt() {
        return startedAt;
    }
}
