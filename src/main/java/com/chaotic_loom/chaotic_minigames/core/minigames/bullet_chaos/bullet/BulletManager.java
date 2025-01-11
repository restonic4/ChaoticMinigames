package com.chaotic_loom.chaotic_minigames.core.minigames.bullet_chaos.bullet;

import java.util.ArrayList;
import java.util.List;

public class BulletManager<T extends Bullet> {
    protected final List<T> bullets = new ArrayList<>();

    public void addBullet(T bullet) {
        bullets.add(bullet);
    }

    public void tick() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            T bullet = bullets.get(i);
            bullet.tick();

            if (bullet.isFinished()) {
                bullets.remove(i);
            }
        }
    }
}
