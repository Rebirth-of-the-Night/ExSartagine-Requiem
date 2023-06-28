package subaraki.exsartagine.integration;

import betterwithmods.common.BWMBlocks;
import net.minecraft.init.Blocks;
import subaraki.exsartagine.recipe.ModRecipes;

public class BetterWithMods {
    public static void addPlaceables(){
        ModRecipes.addPlaceable(BWMBlocks.STOKED_FLAME, true, true);
        ModRecipes.addPlaceable(Blocks.FIRE, false, true);
    }
}
