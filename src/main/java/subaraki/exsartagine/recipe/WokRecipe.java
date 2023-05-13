package subaraki.exsartagine.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.init.RecipeTypes;

import java.util.List;

public class WokRecipe implements CustomFluidRecipe<ItemStackHandler, FluidTank> {

    private final List<Ingredient> ingredients;
    private final FluidStack fluid;
    private final List<ItemStack> outputs;
    private final int flips;

    public WokRecipe(List<Ingredient> inputs, FluidStack fluid, List<ItemStack> outputs, int flips) {
        ingredients = inputs;
        this.fluid = fluid;
        this.outputs = outputs;
        this.flips = flips;
    }

    @Override
    public boolean itemMatch(ItemStackHandler handler) {

        int ingredientCount = 0;
        IItemHandlerRecipeItemHelper recipeItemHelper = new IItemHandlerRecipeItemHelper();

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemstack = handler.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                ++ingredientCount;
                recipeItemHelper.accountStack(itemstack, 1);
            }
        }

        if (ingredientCount != this.ingredients.size())
            return false;
        return recipeItemHelper.canCraft(this, null);
    }

    public boolean fluidMatch(FluidTank handler) {
        if (fluid == null) {
            return true;
        }
        FluidStack fluidStack = handler.getTankProperties()[0].getContents();
        return fluidStack != null && fluidStack.containsFluid(fluid);
    }

    @Override
    public FluidStack getInputFluid() {
        return fluid;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Lists.newArrayList(ingredients);
    }

    @Override
    public ItemStack getDisplay() {
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getResults(ItemStackHandler handler) {
        return outputs;
    }

    @Override
    public int getCookTime() {
        return 125;
    }

    public int getFlips() {
        return flips;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.WOK;
    }
}
