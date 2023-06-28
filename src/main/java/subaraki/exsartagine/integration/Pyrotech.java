package subaraki.exsartagine.integration;

import com.codetaylor.mc.pyrotech.modules.tech.basic.block.BlockCampfire;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.exsartagine.recipe.ModRecipes;

public class Pyrotech {

    @GameRegistry.ObjectHolder("pyrotech:campfire")
    public static Block CAMPFIRE = null;

    public static void addPlaceables(){
        ModRecipes.addPlaceable(CAMPFIRE, iBlockState -> (iBlockState.getValue(BlockCampfire.VARIANT)!=BlockCampfire.EnumType.LIT), false, true);
        ModRecipes.addPlaceable(CAMPFIRE, iBlockState -> (iBlockState.getValue(BlockCampfire.VARIANT)==BlockCampfire.EnumType.LIT), true, true);
    }
}
