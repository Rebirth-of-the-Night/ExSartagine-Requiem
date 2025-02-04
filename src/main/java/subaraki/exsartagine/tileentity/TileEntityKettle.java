package subaraki.exsartagine.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import subaraki.exsartagine.gui.common.KettleFSH;
import subaraki.exsartagine.gui.common.KettleISH;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.KettleRecipe;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;
import subaraki.exsartagine.util.Helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TileEntityKettle extends KitchenwareBlockEntity<KettleRecipe> {

    private static final int OUTPUT_START = 10;

    public final ItemStackHandler handler = new KettleISH(this, 1 + 9 + 9 + 1);

    public final FluidTank fluidInputTank = new KettleFSH(this, 1000);

    public final FluidTank fluidOutputTank = new KettleFSH(this, 1000);

    public final IFluidHandler iFluidHandlerWrapper = new IFluidHandlerWrapper();

    public void swapTanks() {
        FluidStack oldInput = fluidInputTank.getFluid();
        FluidStack oldOutput = fluidOutputTank.getFluid();

        if (oldInput != null || oldOutput != null) {
            FluidStack oldInputCopy = null;
            FluidStack oldOutputCopy = null;
            if (oldInput != null) {
                oldInputCopy = oldInput.copy();
            }
            if (oldOutput != null) {
                oldOutputCopy = oldOutput.copy();
            }

            fluidInputTank.setFluid(oldOutputCopy);
            fluidOutputTank.setFluid(oldInputCopy);
            markDirty();
        }
    }

    public void tryOutputFluid() {
        ItemStack stack = handler.getStackInSlot(KettleISH.CONTAINER_SLOT);
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,null))  {
            FluidActionResult fluidActionResult = FluidUtil.tryFillContainer(stack,fluidOutputTank,fluidOutputTank.getFluidAmount(),null,true);
            if (fluidActionResult.isSuccess()) {
                handler.setStackInSlot(KettleISH.CONTAINER_SLOT,fluidActionResult.getResult());
            }
        }
    }

    @Nullable
    @Override
    public KettleRecipe findRecipe() {
        return ModRecipes.findFluidRecipe(handler, fluidInputTank, KettleRecipe.class, RecipeTypes.KETTLE);
    }

    @Override
    public boolean doesRecipeMatch(final KettleRecipe recipe) {
        return recipe.match(handler, fluidInputTank);
    }

    @Override
    public boolean canFitOutputs(final KettleRecipe recipe) {
        List<ItemStack> results = recipe.getResults(handler);
        IItemHandlerModifiable tempOutputs = Helpers.copyInventory(handler, OUTPUT_START, KettleISH.CONTAINER_SLOT);
        for (ItemStack stack : results) {
            if (!ItemHandlerHelper.insertItemStacked(tempOutputs, stack, false).isEmpty()) {
                return false;
            }
        }

        FluidStack outputStack = recipe.getOutputFluid();
        if (outputStack == null || outputStack.amount <= 0) {
            return true;
        }

        return fluidOutputTank.fill(outputStack, false) >= outputStack.amount;
    }

    @Override
    public void processRecipe(final KettleRecipe recipe) {
        // produce outputs
        IItemHandler output = new RangedWrapper(handler, OUTPUT_START, KettleISH.CONTAINER_SLOT);
        for (ItemStack stack : recipe.getResults(handler)) {
            ItemHandlerHelper.insertItemStacked(output, stack.copy(), false);
        }
        if (recipe.getOutputFluid() != null) {
            fluidOutputTank.fillInternal(recipe.getOutputFluid().copy(), true);
        }

        // consume inputs
        for (int i = 1; i < OUTPUT_START; ++i) {
            this.handler.extractItem(i, 1, false);
        }
        if (recipe.getInputFluid() != null) {
            fluidInputTank.drainInternal(recipe.getInputFluid().amount, true);
        }

        super.processRecipe(recipe);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) iFluidHandlerWrapper : null;
    }

    public class IFluidHandlerWrapper implements IFluidHandler {

        IFluidTankProperties[] iFluidTankProperties;

        @Override
        public IFluidTankProperties[] getTankProperties() {
            if (iFluidTankProperties == null) {
                List<IFluidTankProperties> tanks = Lists.newArrayList();
                Collections.addAll(tanks, fluidInputTank.getTankProperties());
                Collections.addAll(tanks, fluidOutputTank.getTankProperties());
                return tanks.toArray(new IFluidTankProperties[0]);
            }
            return iFluidTankProperties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            return fluidInputTank.fill(resource, doFill);
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return fluidOutputTank.drain(resource, doDrain);
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return fluidOutputTank.drain(maxDrain, doDrain);
        }
    }

    @Override
    public IItemHandler getEntireItemInventory() {
        return handler;
    }

    public int addFluids(FluidStack fluid) {
        if (fluid != null) {
            return fluidInputTank.fill(fluid, true);
        }return 0;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inv", handler.serializeNBT());
        NBTTagCompound fluidInputCompound = new NBTTagCompound();
        NBTTagCompound fluidOutputCompound = new NBTTagCompound();
        fluidInputTank.writeToNBT(fluidInputCompound);
        fluidOutputTank.writeToNBT(fluidOutputCompound);

        compound.setTag("fluidInputTank", fluidInputCompound);
        compound.setTag("fluidOutputTank", fluidOutputCompound);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("inv"))
            handler.deserializeNBT(compound.getCompoundTag("inv"));

        NBTTagCompound fluidInputCompound = compound.getCompoundTag("fluidInputTank");
        NBTTagCompound fluidOutputCompound = compound.getCompoundTag("fluidOutputTank");

        fluidInputTank.readFromNBT(fluidInputCompound);
        fluidOutputTank.readFromNBT(fluidOutputCompound);
    }
}
