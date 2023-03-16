package subaraki.exsartagine.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.gui.common.KettleFSH;
import subaraki.exsartagine.gui.common.KettleISH;
import subaraki.exsartagine.recipe.KettleRecipe;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class TileEntityKettle extends KitchenwareBlockEntity implements ITickable {

    private static final int OUTPUT_START = 10;

    public KettleRecipe cached;
    public boolean running;

    public final ItemStackHandler handler = new KettleISH(this, 1 + 9 + 9 + 1);

    public final FluidTank fluidInputTank = new KettleFSH(this, 1000);

    public final FluidTank fluidOutputTank = new KettleFSH(this, 1000);

    public final IFluidHandler iFluidHandlerWrapper = new IFluidHandlerWrapper();

    @Override
    public void update() {
        if (!world.isRemote) {
            if (activeHeatSourceBelow() && canStart()) {
                KettleRecipe recipe = getOrCreateRecipe();
                if (recipe != null) {
                    if (clientCookTime == progress) {
                        process();
                    } else {
                        if (running) {

                        } else {
                            start();
                        }
                        progress++;
                        markDirty();
                    }
                }
            } else {
                decreaseProgress();
            }
        }
    }

    public void decreaseProgress() {
        if (progress > 0) {
            progress--;
            markDirty();
        }
        running = false;
    }

    public boolean canStart() {
        KettleRecipe recipe = getOrCreateRecipe();
        if (recipe == null)
            return false;

        if (!checkFluids(recipe)) return false;


        List<ItemStack> results = recipe.getResults(handler);
        for (ItemStack stack : results) {
            ItemStack remainder = stack.copy();
            for (int i = OUTPUT_START; i < KettleISH.CONTAINER_SLOT; i++) {
                remainder = handler.insertItem(i, remainder, true);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                return false;
            }
        }
        return true;
    }

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

    public boolean checkFluids(KettleRecipe recipe) {
        if (!recipe.fluidMatch(fluidInputTank)) {
            return false;
        }
        FluidStack outputStack = recipe.getOutputFluid();
        if (outputStack == null) {
            return true;
        }

        int filled = fluidOutputTank.fill(outputStack, false);
        if (filled < outputStack.amount) {
            return false;
        }
        return true;
    }

    public void start() {
        running = true;
        clientCookTime = cached.getCookTime();
    }

    public KettleRecipe getOrCreateRecipe() {
        if (cached != null && cached.match(handler,fluidInputTank)) {
            return cached;
        }
        return cached = ModRecipes.findKettleRecipe(handler, fluidInputTank);
    }

    public void process() {
        progress = 0;
        NonNullList<ItemStack> nonnulllist = cached.getRemainingItems(this.handler);
        List<ItemStack> results = cached.getResults(handler);

        for (int i = 0; i < OUTPUT_START; ++i) {
            ItemStack itemstack = this.handler.getStackInSlot(i);
            ItemStack remainderItem = nonnulllist.get(i);

            if (!itemstack.isEmpty()) {
                this.handler.extractItem(i, 1, false);
                itemstack = this.handler.getStackInSlot(i);
            }

            if (!remainderItem.isEmpty()) {
                if (itemstack.isEmpty()) {
                    this.handler.setStackInSlot(i, remainderItem);
                } else if (ItemStack.areItemsEqual(itemstack, remainderItem) && ItemStack.areItemStackTagsEqual(itemstack, remainderItem)) {
                    remainderItem.grow(itemstack.getCount());
                    this.handler.setStackInSlot(i, remainderItem);
                }
            }
        }

        for (ItemStack stack : results) {
            ItemStack remainder = stack.copy();
            for (int i = OUTPUT_START; i < KettleISH.CONTAINER_SLOT; i++) {
                remainder = handler.insertItem(i, remainder, false);
                if (remainder.isEmpty()) {
                    break;
                }
            }
        }
        processFluids();
    }

    public void processFluids() {
        if (cached.getInputFluid() != null) {
            fluidInputTank.drainInternal(cached.getInputFluid().amount, true);
        }
        if (cached.getOutputFluid() != null) {
            fluidOutputTank.fillInternal(cached.getOutputFluid(), true);
        }
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
    public int getProgress() {
        return progress;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("running", running);
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
        this.running = compound.getBoolean("running");
        if (compound.hasKey("inv"))
            handler.deserializeNBT(compound.getCompoundTag("inv"));

        NBTTagCompound fluidInputCompound = compound.getCompoundTag("fluidInputTank");
        NBTTagCompound fluidOutputCompound = compound.getCompoundTag("fluidOutputTank");

        fluidInputTank.readFromNBT(fluidInputCompound);
        fluidOutputTank.readFromNBT(fluidOutputCompound);
    }
}
