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
    public static SoundEvent ROLLERDISCO_RUMBLE;
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

    // Argofox
    public static SoundEvent ghost_and_ghost_spooky_thoughts;
    public static SoundEvent ghost_and_ghost_lighthouse;
    public static SoundEvent ghost_and_ghost_coconut_mystery;
    public static SoundEvent ghost_and_ghost_sir_ghostington;
    public static SoundEvent ghost_and_ghost_melancholy;
    public static SoundEvent ghost_and_ghost_the_adventure;
    public static SoundEvent ghost_and_ghost_red_lights;

    public static void register() {
        MUSIC_MAIN_MENU_1 = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "music_main_menu_1"));
        ROLLERDISCO_RUMBLE = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "rollerdisco_rumble"));
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
        ghost_and_ghost_spooky_thoughts = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_spooky_thoughts"));
        ghost_and_ghost_lighthouse = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_lighthouse"));
        ghost_and_ghost_coconut_mystery = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_coconut_mystery"));
        ghost_and_ghost_sir_ghostington = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_sir_ghostington"));
        ghost_and_ghost_melancholy = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_melancholy"));
        ghost_and_ghost_the_adventure = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_the_adventure"));
        ghost_and_ghost_red_lights = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_red_lights"));
    }

    public static SoundEvent registerSoundEvent(ResourceLocation location) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
    }
}
