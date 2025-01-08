package com.chaotic_loom.chaotic_minigames.mixin.common;

import com.chaotic_loom.chaotic_minigames.mixin_extra.ExtraSoundSourcesHolder;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SoundSource.class)
@Unique
public class SoundSourceMixin {
    @Shadow @Final @Mutable private static SoundSource[] $VALUES;

    //@Unique
    //private static final SoundSource SOUND_EFFECTS = expansion$addVariant("SOUND_EFFECTS", "sound_effects");

    static {
        ExtraSoundSourcesHolder.SOUND_EFFECTS = expansion$addVariant("SOUND_EFFECTS", "sound_effects");
    }

    @Invoker("<init>")
    public static SoundSource expansion$invokeInit(String internalName, int internalId, String name) {
        throw new AssertionError();
    }

    private static SoundSource expansion$addVariant(String internalName, String name) {
        ArrayList<SoundSource> variants = new ArrayList<SoundSource>(Arrays.asList(SoundSourceMixin.$VALUES));
        SoundSource instrument = expansion$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1, name);
        variants.add(instrument);
        SoundSourceMixin.$VALUES = variants.toArray(new SoundSource[0]);

        return instrument;
    }
}
