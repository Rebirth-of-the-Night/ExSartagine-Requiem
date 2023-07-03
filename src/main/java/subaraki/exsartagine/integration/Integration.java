package subaraki.exsartagine.integration;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Integration {

    @GameRegistry.ObjectHolder("futuremc:campfire")
    public static Block FUTUREMC_CAMPFIRE = null;

    public static void postInit(){
        if(Loader.isModLoaded("betterwithmods")){ BetterWithMods.addPlaceables(); }
        if(Loader.isModLoaded("pyrotech")){Pyrotech.addPlaceables();}
        if(Loader.isModLoaded("futuremc")){FutureMC.addPlaceables();}
        if(Loader.isModLoaded("campfire")){Campfire.addPlaceables();}
    }
}
