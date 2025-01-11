package com.chaotic_loom.chaotic_minigames.core.registries.common;

import com.chaotic_loom.chaotic_minigames.core.registries.LaserGunItem;
import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.core.annotations.Registration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

@Registration(side = ExecutionSide.COMMON)
public class ItemRegistry {
    public static LaserGunItem LASER_GUN_ITEM;

    public static void register() {
        LASER_GUN_ITEM = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(CMSharedConstants.ID, "laser_gun"),
                new LaserGunItem(new Item.Properties().stacksTo(1))
        );
    }
}
