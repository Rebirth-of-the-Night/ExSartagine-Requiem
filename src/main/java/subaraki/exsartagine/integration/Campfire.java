package subaraki.exsartagine.integration;

import git.jbredwards.campfire.common.block.AbstractCampfire;
import git.jbredwards.campfire.common.init.CampfireBlocks;
import subaraki.exsartagine.recipe.ModRecipes;

public class Campfire {
    public static void addPlaceables(){
        ModRecipes.addPlaceable(CampfireBlocks.CAMPFIRE, iBlockState -> (iBlockState.getValue(AbstractCampfire.LIT)), true, true);
        ModRecipes.addPlaceable(CampfireBlocks.CAMPFIRE, iBlockState -> (!iBlockState.getValue(AbstractCampfire.LIT)), false, true);
        ModRecipes.addPlaceable(CampfireBlocks.CAMPFIRE_ASH, false, true);

        ModRecipes.addPlaceable(CampfireBlocks.BRAZIER, iBlockState -> (iBlockState.getValue(AbstractCampfire.LIT)), true, true);
        ModRecipes.addPlaceable(CampfireBlocks.BRAZIER, iBlockState -> (!iBlockState.getValue(AbstractCampfire.LIT)), false, true);
    }
}
