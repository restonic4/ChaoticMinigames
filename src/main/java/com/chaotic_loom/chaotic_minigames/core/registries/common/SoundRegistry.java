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
    public static SoundEvent ghost_and_ghost_lazy_sunday;
    public static SoundEvent sybranax_rogue;
    public static SoundEvent meizong_rebirth;
    public static SoundEvent dugn1r_45_degrees;
    public static SoundEvent meizong_krasch;
    public static SoundEvent redvox_time;
    public static SoundEvent dugn1r_monster_inside_you;
    public static SoundEvent redvox_the_ruby;
    public static SoundEvent aron_kruk_astral_finale;
    public static SoundEvent aaron_kruk_orion_s_reverie;
    public static SoundEvent dino_rano_not_the_one;
    public static SoundEvent aaron_kruk_zenith;
    public static SoundEvent foxhunt_rapture;

    //Not using

    public static SoundEvent meizong_spark;
    public static SoundEvent meizong_arctic;
    public static SoundEvent noxive_monsters_in_my_head;

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
        ghost_and_ghost_lazy_sunday = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "ghost_and_ghost_lazy_sunday"));
        sybranax_rogue = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "sybranax_rogue"));
        meizong_rebirth = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "meizong_rebirth"));
        dugn1r_45_degrees = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "dugn1r_45_degrees"));
        meizong_krasch = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "meizong_krasch"));
        redvox_time = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "redvox_time"));
        meizong_spark = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "meizong_spark"));
        dugn1r_monster_inside_you = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "dugn1r_monster_inside_you"));
        redvox_the_ruby = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "redvox_the_ruby"));
        aron_kruk_astral_finale = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "aron_kruk_astral_finale"));
        aaron_kruk_orion_s_reverie = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "aaron_kruk_orion_s_reverie"));
        dino_rano_not_the_one = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "dino_rano_not_the_one"));
        aaron_kruk_zenith = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "aaron_kruk_zenith"));
        meizong_arctic = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "meizong_arctic"));
        noxive_monsters_in_my_head = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "noxive_monsters_in_my_head"));
        foxhunt_rapture = registerSoundEvent(new ResourceLocation(CMSharedConstants.ID, "foxhunt_rapture"));
    }

    public static SoundEvent registerSoundEvent(ResourceLocation location) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, location, SoundEvent.createVariableRangeEvent(location));
    }
}
