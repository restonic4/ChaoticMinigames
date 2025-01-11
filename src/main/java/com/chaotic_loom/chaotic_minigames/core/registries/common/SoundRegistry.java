package com.chaotic_loom.chaotic_minigames.core.registries.common;

import com.chaotic_loom.chaotic_minigames.entrypoints.constants.CMSharedConstants;
import com.chaotic_loom.under_control.core.annotations.ExecutionSide;
import com.chaotic_loom.under_control.core.annotations.Registration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@Registration(side = ExecutionSide.COMMON)
public class SoundRegistry {
    public static SoundEvent MUSIC_MAIN_MENU_1;
    public static SoundEvent CHOP_CHOP;
    public static SoundEvent AGILE_ACCELERANDO;
    public static SoundEvent BREAKNECK_BOOGIE;
    public static SoundEvent TIME_IS_OF_THE_ESSENCE;
    public static SoundEvent SWIFT_DESCENT;
    public static SoundEvent PRONTO;
    public static SoundEvent NIMBLY_DOES_IT;
    public static SoundEvent LICKETY_SPLIT;
    public static SoundEvent DOUBLE_TIME;
    public static SoundEvent DASHING_ON_THE_DOUBLE;

    public static void register() {
        MUSIC_MAIN_MENU_1 = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "music_main_menu_1"));
        CHOP_CHOP = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "chop_chop"));
        AGILE_ACCELERANDO = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "agile_accelerando"));
        BREAKNECK_BOOGIE = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "breakneck_boogie"));
        TIME_IS_OF_THE_ESSENCE = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "time_is_of_the_essence"));
        SWIFT_DESCENT = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "swift_descent"));
        PRONTO = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "pronto"));
        NIMBLY_DOES_IT = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "nimbly_does_it"));
        LICKETY_SPLIT = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "lickety_split"));
        DOUBLE_TIME = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "double_time"));
        DASHING_ON_THE_DOUBLE = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "dashing_on_the_double"));
    }

    public static SoundEvent registerSoundEvent(ResourceLocation location) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
    }
}
