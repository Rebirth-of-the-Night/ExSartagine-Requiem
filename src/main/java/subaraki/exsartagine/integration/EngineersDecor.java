package subaraki.exsartagine.integration;

import net.minecraft.block.Block;
import subaraki.exsartagine.recipe.ModRecipes;
import wile.engineersdecor.ModContent;
import wile.engineersdecor.blocks.BlockDecorFurnace;
import wile.engineersdecor.detail.ModConfig;

public class EngineersDecor {
    public static void addPlaceables(){
        Block labFurnace = ModContent.SMALL_LAB_FURNACE;
        if(!ModConfig.isOptedOut(labFurnace)) {
            ModRecipes.addPlaceable(labFurnace, iBlockState -> (iBlockState.getValue(BlockDecorFurnace.LIT)),
                    true, false);
            ModRecipes.addPlaceable(labFurnace, iBlockState -> (!iBlockState.getValue(BlockDecorFurnace.LIT)),
                    false, false);
        }
    }
}
