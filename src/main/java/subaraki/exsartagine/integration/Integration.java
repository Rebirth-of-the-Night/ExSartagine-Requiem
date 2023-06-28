package subaraki.exsartagine.integration;

import net.minecraftforge.fml.common.Loader;

public class Integration {
    public static void postInit(){
        if(Loader.isModLoaded("immersiveengineering")){ImmersiveEngineering.registerHeatableAdapters();}
    }
}
