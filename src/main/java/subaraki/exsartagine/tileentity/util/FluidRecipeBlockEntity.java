package subaraki.exsartagine.tileentity.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import subaraki.exsartagine.recipe.CustomFluidRecipe;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.ModRecipes;

import java.util.List;

public abstract class FluidRecipeBlockEntity<T extends IItemHandler,U extends IFluidHandler, R extends CustomFluidRecipe<T,U>> extends KitchenwareBlockEntity {

    protected T inventoryInput;
    protected T inventoryOutput;
    protected U fluidInventoryInput;
    protected U fluidInventoryOutput;
    protected R cached;
    protected IRecipeType<R> recipeType;
    public boolean cooking;

    protected FluidRecipeBlockEntity() {
        initInventory();
    }

    protected abstract void initInventory();

    public T getInventoryInput() {
        return inventoryInput;
    }

    public T getInventoryOutput() {
        return inventoryOutput;
    }

    public T getEntireItemInventory() {
        return (T) new CombinedInvWrapper((IItemHandlerModifiable) inventoryInput,(IItemHandlerModifiable) inventoryOutput);
    }

    public U getFluidInventoryInput() {
        return fluidInventoryInput;
    }

    public R getOrCreateRecipe() {
        if (cached != null && cached.match(inventoryInput, fluidInventoryInput)) {
            return cached;
        }
        return cached = ModRecipes.findFluidRecipe(inventoryInput, fluidInventoryInput,recipeType);
    }

    public boolean canStart() {
        R recipe = getOrCreateRecipe();
        if (recipe == null)
            return false;

        if (!checkFluids(recipe)) return false;


        List<ItemStack> results = recipe.getResults(inventoryInput);
        for (ItemStack stack : results) {
            ItemStack remainder = stack.copy();
            for (int i = 0; i < inventoryInput.getSlots(); i++) {
                remainder = inventoryInput.insertItem(i, remainder, true);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void decreaseProgress() {
        if (progress > 0) {
            progress--;
            markDirty();
        }
        cooking = false;
    }

    public void process() {
        progress = 0;
        processItems();
        processFluids();
    }

    public void start() {
        cooking = true;
        clientCookTime = cached.getCookTime();
    }

    public abstract void processItems();

    public void processFluids() {
        if (cached.getInputFluid() != null) {
            fluidInventoryInput.drain(cached.getInputFluid().amount, true);
        }
        if (cached.getOutputFluid() != null) {
            fluidInventoryOutput.fill(cached.getOutputFluid(), true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("cooking", cooking);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.cooking = compound.getBoolean("cooking");
    }

    public boolean checkFluids(R recipe) {
        if (!recipe.fluidMatch(fluidInventoryInput)) {
            return false;
        }
        return checkFluidInv(recipe);
    }

    public abstract boolean checkFluidInv(R recipe);

}
