package subaraki.exsartagine.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.init.RecipeTypes;

import java.util.Collections;
import java.util.List;

public class PotRecipe implements CustomFluidRecipe<IItemHandler, IFluidHandler> {

    private final Ingredient ingredient;
    private final FluidStack inputFluid;
    private final ItemStack output;
    private final int time;

    private static final int INPUT = 0;

    public PotRecipe(Ingredient input, FluidStack inputFluid, ItemStack output, int time) {
        this.ingredient = input;
        this.inputFluid = inputFluid;
        this.output = output;
        this.time = time;
    }

    public PotRecipe(Ingredient input, ItemStack output) {
        this(input, new FluidStack(FluidRegistry.WATER, 50), output, 125);
    }

    @Override
    public boolean itemMatch(IItemHandler handler) {
        return ingredient.test(handler.getStackInSlot(INPUT));
    }

    @Override
    public boolean fluidMatch(IFluidHandler handler) {
        if (inputFluid == null) {
            return true;
        }

        FluidStack fluid = handler.drain(inputFluid, false);
        return fluid != null && fluid.amount >= inputFluid.amount;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return Collections.singletonList(ingredient);
    }

    @Override
    public FluidStack getInputFluid() {
        return inputFluid;
    }

    @Override
    public ItemStack getDisplay() {
        return output;
    }

    @Override
    public int getCookTime() {
        return time;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.POT;
    }
}
