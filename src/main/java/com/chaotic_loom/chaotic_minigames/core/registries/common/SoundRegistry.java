package com.chaotic_loom.chaotic_minigames.core.registries.common;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static SoundEvent MUSIC_MAIN_MENU_1;

    public static void register() {
        MUSIC_MAIN_MENU_1 = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "music_main_menu_1"));
    }

    public static SoundEvent registerSoundEvent(ResourceLocation location) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
    }
}
