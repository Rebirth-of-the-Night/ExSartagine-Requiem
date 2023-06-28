package subaraki.exsartagine.integration;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.exsartagine.recipe.ModRecipes;
import thedarkcolour.futuremc.block.villagepillage.BlockFurnaceAdvanced;
import thedarkcolour.futuremc.config.FConfig;
import thedarkcolour.futuremc.registry.FBlocks;

public class FutureMC {
    @GameRegistry.ObjectHolder("futuremc:campfire")
    public static Block CAMPFIRE = null;
    public static void addPlaceables(){
        if(FConfig.INSTANCE.getVillageAndPillage().smoker) {
            ModRecipes.addPlaceable(FBlocks.SMOKER, iBlockState ->
                    (iBlockState.getValue(BlockFurnaceAdvanced.Companion.getLIT())), true, false);
            ModRecipes.addPlaceable(FBlocks.SMOKER, iBlockState ->
                    (!iBlockState.getValue(BlockFurnaceAdvanced.Companion.getLIT())), false, false);
        }
        if(FConfig.INSTANCE.getVillageAndPillage().blastFurnace) {
            ModRecipes.addPlaceable(FBlocks.BLAST_FURNACE, iBlockState ->
                    (iBlockState.getValue(BlockFurnaceAdvanced.Companion.getLIT())), true, false);
            ModRecipes.addPlaceable(FBlocks.BLAST_FURNACE, iBlockState ->
                    (!iBlockState.getValue(BlockFurnaceAdvanced.Companion.getLIT())), false, false);
        }
        if(FConfig.INSTANCE.getVillageAndPillage().campfire.enabled)
            ModRecipes.addPlaceable(CAMPFIRE, true, true);
    }
}
