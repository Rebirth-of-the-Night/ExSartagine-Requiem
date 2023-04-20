package subaraki.exsartagine.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import subaraki.exsartagine.ExSartagine;

public class ModSounds {

    public static final SoundEvent BUBBLING = make("bubbling");
    public static final SoundEvent FRYING = make("frying");
    public static final SoundEvent METAL_SLIDE = make("metal_slide");

    private static SoundEvent make(String s) {
        return new SoundEvent(new ResourceLocation(ExSartagine.MODID,s)).setRegistryName(new ResourceLocation(ExSartagine.MODID,s));
    }
}
