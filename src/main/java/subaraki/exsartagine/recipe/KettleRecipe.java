package subaraki.exsartagine.recipe;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.init.RecipeTypes;

import javax.annotation.Nullable;
import java.util.List;


public class KettleRecipe implements CustomFluidRecipe<IItemHandler, IFluidHandler> {

    private final List<Ingredient> inputs;
    @Nullable
    private final Ingredient catalyst;
    private final FluidStack inputFluid;
    private final FluidStack outputFluid;
    private final List<ItemStack> outputs;
    private final int time, dirtyTime;

    public KettleRecipe(List<Ingredient> inputs, @Nullable Ingredient catalyst, FluidStack inputFluid, FluidStack outputFluid, List<ItemStack> outputs, int time, int dirtyTime) {
        this.inputs = inputs;
        this.catalyst = catalyst;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.outputs = outputs;
        this.time = time;
        this.dirtyTime = dirtyTime;
    }

    public Ingredient getCatalyst() {
        return catalyst;
    }

    public boolean fluidMatch(IFluidHandler handler) {
        if (inputFluid == null) {
            return true;
        }
        FluidStack fluidStack = handler.getTankProperties()[0].getContents();
        return fluidStack != null && fluidStack.containsFluid(inputFluid);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(IItemHandler handler) {
        return kettleRecipeGetRemainingItems(handler);
    }

    /**
     * Default implementation of IRecipe.getRemainingItems {getRemainingItems} because
     * this is just copy pasted over a lot of recipes.
     *
     * @param inv Crafting inventory
     * @return Crafting inventory contents after the recipe.
     */
    public NonNullList<ItemStack> kettleRecipeGetRemainingItems(IItemHandler inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(10, ItemStack.EMPTY);

        if (catalyst instanceof IIngredientWrapper) {

            IIngredient iIngredient = ((IIngredientWrapper) catalyst).getiIngredient();

            if (iIngredient != null && iIngredient.hasNewTransformers()) {

                try {
                    IItemStack remainingItem = iIngredient.applyNewTransform(CraftTweakerMC.getIItemStack(inv.getStackInSlot(0)));
                    ret.set(0, CraftTweakerMC.getItemStack(remainingItem));

                } catch (Throwable e) {
                    CraftTweakerAPI.logError("Could not execute NewRecipeTransformer on " + iIngredient.toCommandString(), e);
                }
            }
        } else {
            ret.set(0, inv.getStackInSlot(0).copy());
        }

        for (int i = 1; i < 10; i++) {
            ret.set(i, inv.getStackInSlot(i).copy());
        }
        return ret;
    }

    @Override
    public boolean itemMatch(IItemHandler handler) {

        if (catalyst != null && !catalyst.test(handler.getStackInSlot(0))) {
            return false;
        }

        int ingredientCount = 0;
        IItemHandlerRecipeItemHelper recipeItemHelper = new IItemHandlerRecipeItemHelper();

        for (int i = 1; i < 10; ++i) {
            ItemStack itemstack = handler.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                ++ingredientCount;
                recipeItemHelper.accountStack(itemstack, 1);
            }
        }

        if (ingredientCount != this.inputs.size())
            return false;
        return recipeItemHelper.canCraft(this, null);
    }

    @Override
    public List<ItemStack> getResults(IItemHandler handler) {
        return outputs;
    }

    @Override
    public ItemStack getDisplay() {
        return ItemStack.EMPTY;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return inputs;
    }

    @Override
    public FluidStack getInputFluid() {
        return inputFluid;
    }

    @Override
    public int getCookTime() {
        return time;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public int getDirtyTime() {
        return dirtyTime;
    }

    //needed to force recipes using fluid to come first
    @Override
    public int compareTo(CustomRecipe<IItemHandler> o) {
        if (!(o instanceof KettleRecipe)) {
            return 0;
        }
        boolean thisFluidRequirement = getInputFluid() != null;
        boolean otherFluidRequirement = ((KettleRecipe) o).getInputFluid() != null;

        if (thisFluidRequirement && otherFluidRequirement) {
            return 0;
        }

        //this recipe goes first
        if (thisFluidRequirement) {
            return -1;
        }

        //other recipe goes first
        if (otherFluidRequirement) {
            return 1;
        }
        return 0;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.KETTLE;
    }
}
