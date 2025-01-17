package com.chaotic_loom.chaotic_minigames.core.minigames.quick_drop.crusher;

import com.chaotic_loom.chaotic_minigames.Util;
import com.chaotic_loom.chaotic_minigames.core.GameManager;
import com.chaotic_loom.chaotic_minigames.core.minigames.sweeper.spining_bar.ServerSpinningBar;
import com.chaotic_loom.under_control.util.MathHelper;
import com.chaotic_loom.under_control.util.pooling.PoolManager;
import com.chaotic_loom.under_control.util.pooling.Poolable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

import java.util.List;

public class ServerCrusher extends Crusher implements Poolable {
    public ServerCrusher() {
        super(new Vector3f(), 160f, 0, 1);
    }

    public ServerCrusher(Vector3f position, float startHeight, long startTime, long endTime) {
        super(position, startHeight, startTime, endTime);
    }

    public ServerCrusher initialize(Vector3f position, float startHeight, long startTime, long endTime) {
        super.position = position;
        super.startHeight = startHeight;
        super.startTime = startTime;
        super.endTime = endTime;

        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if (isFinished()) {
            return;
        }

        List<ServerPlayer> players = GameManager.getInstance().getPartyManager().getInGamePlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            ServerPlayer serverPlayer = players.get(i);

            if (serverPlayer.getEyeY() >= this.position.y) {
                GameManager.getInstance().getPartyManager().disqualifyPlayer(serverPlayer);
            }
        }
    }

    @Override
    public void markFinish() {
        super.markFinish();
        PoolManager.release(this);
    }
}
