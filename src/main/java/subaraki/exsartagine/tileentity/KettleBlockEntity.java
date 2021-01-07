package subaraki.exsartagine.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.block.KettleBlock;
import subaraki.exsartagine.gui.common.KettleFSH;
import subaraki.exsartagine.gui.common.KettleISH;
import subaraki.exsartagine.recipe.KettleRecipe;
import subaraki.exsartagine.recipe.Recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class KettleBlockEntity extends TileEntity implements ITickable, Cooker {

    private static final int OUTPUT_START = 10;

    public KettleRecipe cached;
    public int progress;
    public boolean running;
    public int cookTime = -1;

    public final ItemStackHandler handler = new KettleISH(this, 1 + 9 + 9);

    public final FluidTank fluidTank = new KettleFSH(this,10000);

    @Override
    public void update() {
        if (!world.isRemote) {
            if (isHeated()) {
                if (canStart()) {
                    KettleRecipe recipe = getOrCreateRecipe();
                    if (recipe != null) {
                        if (cookTime == progress) {
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
                }
            } else {
                decreaseProgress();
                running = false;
            }
        }
    }

    public void decreaseProgress() {
        if (progress > 0) {
            progress--;
        }
    }

    public boolean canStart() {
        KettleRecipe recipe = getOrCreateRecipe();
        if (recipe == null)
            return false;

        List<ItemStack> results = recipe.getResults(handler);

        for (ItemStack stack : results) {
            ItemStack remainder = stack.copy();
            for (int i = OUTPUT_START; i < 19; i++) {
                remainder = handler.insertItem(i, remainder, true);
                if (remainder.isEmpty()) break;
            }
            if (!remainder.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void start() {
        running = true;
        cookTime = cached.getCookTime();
    }

    public KettleRecipe getOrCreateRecipe() {
        if (cached != null && cached.itemMatch(handler)) {
            return cached;
        }
        return cached = Recipes.findKettleRecipe(handler, fluidTank);
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
            for (int i = OUTPUT_START; i < 19; i++) {
                remainder = handler.insertItem(i, remainder, false);
                if (remainder.isEmpty()) {
                    break;
                }
            }
        }

        if (cached.getFluid() != null) {
            fluidTank.drainInternal(cached.getFluid().amount,true);
        }
    }

    public boolean isHeated() {
        return world.getBlockState(pos).getValue(KettleBlock.HEATED);
    }

    public void setHeated(boolean heated) {
        world.setBlockState(pos, blockType.getDefaultState().withProperty(KettleBlock.HEATED, heated));
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return super.getCapability(capability, facing);
    }

    @Override
    public void setCooking() {
        setHeated(true);
    }

    @Override
    public void stopCooking() {
        setHeated(false);
        progress = 0;
    }

    @Override
    public IItemHandler getInventory() {
        return handler;
    }

    public int addFluids(FluidStack fluid) {
        if (fluid != null) {
            return fluidTank.fill(fluid,true);
        }
        return 0;
    }

    @Override
    public int getCookTime() {
        return cookTime;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("cookTime", cookTime);
        compound.setBoolean("running", running);
        compound.setInteger("progress", progress);
        compound.setTag("inv", handler.serializeNBT());
        fluidTank.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.running = compound.getBoolean("running");
        this.cookTime = compound.getInteger("cookTime");
        this.progress = compound.getInteger("progress");
        if (compound.hasKey("inv"))
            handler.deserializeNBT(compound.getCompoundTag("inv"));
        fluidTank.readFromNBT(compound);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(pos,blockType.getDefaultState(),blockType.getDefaultState(),3);
    }

    /////////////////3 METHODS ABSOLUTELY NEEDED FOR CLIENT/SERVER SYNCING/////////////////////
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new SPacketUpdateTileEntity(getPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() == newSate.getBlock();
    }

    //calls readFromNbt by default. no need to add anything in here
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
    }
    ////////////////////////////////////////////////////////////////////

}
