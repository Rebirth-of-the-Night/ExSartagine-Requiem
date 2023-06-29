package subaraki.exsartagine.integration;

import net.minecraftforge.fml.common.Loader;

public class Integration {
    public static void postInit(){
        if(Loader.isModLoaded("betterwithmods")){ BetterWithMods.addPlaceables(); }
        if(Loader.isModLoaded("pyrotech")){Pyrotech.addPlaceables();}
        if(Loader.isModLoaded("futuremc")){FutureMC.addPlaceables();}
        if(Loader.isModLoaded("campfire")){Campfire.addPlaceables();}
    }
}
