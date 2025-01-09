package com.chaotic_loom.chaotic_minigames.core.client;

import com.chaotic_loom.chaotic_minigames.core.data.minigames.bullet_chaos.ClientBullet;

import java.util.ArrayList;
import java.util.List;

public class ClientBulletManager {
    private static final List<ClientBullet> clientBullets = new ArrayList<>();

    public static void tick() {
        for (ClientBullet clientBullet : clientBullets) {
            clientBullet.tick();
        }

        for (int i = clientBullets.size() - 1; i >= 0; i--) {
            ClientBullet clientBullet = clientBullets.get(i);

            if (clientBullet.isFinished()) {
                clientBullets.remove(i);
            } else {
                clientBullet.tick();
            }
        }
    }

    public static void addBullet(ClientBullet clientBullet) {
        clientBullets.add(clientBullet);
    }
}
