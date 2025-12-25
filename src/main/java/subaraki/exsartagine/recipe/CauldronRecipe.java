package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import subaraki.exsartagine.init.RecipeTypes;

public class CauldronRecipe extends PotRecipe {

    public CauldronRecipe(Ingredient input, FluidStack inputFluid, ItemStack output, int time, int dirtyTime) {
        super(input, inputFluid, output, time, dirtyTime);
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.CAULDRON;
    }

}
