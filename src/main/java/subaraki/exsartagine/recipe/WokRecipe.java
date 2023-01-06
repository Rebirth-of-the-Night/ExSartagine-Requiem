package subaraki.exsartagine.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.init.RecipeTypes;

import java.util.List;

public class WokRecipe implements CustomFluidRecipe<ItemStackHandler, FluidTank> {

    private final List<Ingredient> ingredients;
    private final FluidStack fluid;
    private final List<ItemStack> outputs;

    public WokRecipe(List<Ingredient> inputs, FluidStack fluid, List<ItemStack> outputs) {
        ingredients = inputs;
        this.fluid = fluid;
        this.outputs = outputs;
    }

    @Override
    public boolean itemMatch(ItemStackHandler handler) {

        for (int i= 0 ; i < ingredients.size();i++) {
            Ingredient ingredient = ingredients.get(i);
            ItemStack stack = handler.getStackInSlot(i);
            if (!ingredient.test(stack)) return false;
        }
        return true;
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
        return null;
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

    @Override
    public IRecipeType<ItemStackHandler> getType() {
        return RecipeTypes.WOK;
    }
}
