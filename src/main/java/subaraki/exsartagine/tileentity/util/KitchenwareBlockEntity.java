package subaraki.exsartagine.tileentity.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import subaraki.exsartagine.Oredict;
import subaraki.exsartagine.recipe.CustomRecipe;
import subaraki.exsartagine.recipe.DirtyingRecipe;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.util.ConfigHandler;

import javax.annotation.Nullable;

public abstract class KitchenwareBlockEntity<R extends CustomRecipe<?>> extends TileEntity implements ITickable, RecipeHost<R> {

    private final RecipeHandler<R> recipeHandler = new RecipeHandler<>(this);

    protected int soiledTime = 0;

    //check if the block below is hot
    public final boolean activeHeatSourceBelow() {
        return ModRecipes.isHeatSource(world.getBlockState(pos.down()).getActualState(world, pos.down()));
    }

    public abstract IItemHandler getEntireItemInventory();

    @Override
    public boolean isRemote() {
        return world.isRemote;
    }

    @Nullable
    public R getRunningRecipe() {
        return recipeHandler.getRunningRecipe();
    }

    public boolean isWorking() {
        return recipeHandler.isWorking();
    }

    public float getProgressFraction() {
        return recipeHandler.getProgressFraction();
    }

    public int getSoiledTime() {
        return soiledTime;
    }

    public void setSoiledTime(int soiledTime) {
        if (soiledTime != this.soiledTime) {
            this.soiledTime = soiledTime;
            markDirty();
        }
    }

    public boolean isSoiled() {
        return soiledTime > 0;
    }

    public boolean tryClean(EntityPlayer player, EnumHand hand) {
        if (soiledTime <= 0) {
            return false;
        }

        ItemStack stack = player.getHeldItem(hand);
        if (Oredict.checkMatch(Oredict.CLEANER, stack)) {
            if (!world.isRemote) {
                player.world.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.BLOCKS, 0.75F, 0.9F);
                stack.damageItem(1, player);
                soiledTime = 0;
                markDirty();
            }
            return true;
        } else if (Oredict.checkMatch(Oredict.WASHER, stack)) {
            if (!world.isRemote && drainWasherFluid(player, hand, stack)) {
                player.world.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.75F, 0.9F);
                soiledTime = 0;
                markDirty();
            }
            return true;
        }

        return false;
    }

    private static boolean drainWasherFluid(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (ConfigHandler.washer_fluid_amount <= 0) {
            return true;
        }
        FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(stack, new WashingTank(), new InvWrapper(player.inventory),
                ConfigHandler.washer_fluid_amount, player, !player.capabilities.isCreativeMode);
        if (!result.isSuccess()) {
            return false;
        }
        player.setHeldItem(hand, result.getResult());
        return true;
    }

    @Override
    public boolean canDoWork(R recipe) {
        if (!activeHeatSourceBelow()) {
            return false;
        }
        if (!isSoiled()) {
            return true;
        }
        return recipe instanceof DirtyingRecipe && ((DirtyingRecipe) recipe).getDirtyTime() < 0;
    }

    @Override
    public void processRecipe(R recipe) {
        if (recipe instanceof DirtyingRecipe) {
            int recipeDirtyTime = ((DirtyingRecipe) recipe).getDirtyTime();
            if (recipeDirtyTime < 0) {
                soiledTime = 0;
            } else if (recipeDirtyTime > 0) {
                soiledTime += recipeDirtyTime;
            }
        }
    }

    @Override
    public void update() {
        if (world.isRemote) {
            recipeHandler.tick();
            return;
        }

        boolean didWork = false;
        if (soiledTime > 0) {
            --soiledTime;
            didWork = true;
        }
        if (recipeHandler.tick()) {
            didWork = true;
        }
        if (didWork) {
            markDirty();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        recipeHandler.writeToNBT(compound);
        compound.setInteger("soiledtime", soiledTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        recipeHandler.readFromNBT(compound);
        this.soiledTime = compound.getInteger("soiledtime");
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
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        writeToNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        int prevSoiledTime = soiledTime;
        super.handleUpdateTag(tag);
        if (prevSoiledTime <= 0) {
            if (soiledTime > 0) {
                world.markBlockRangeForRenderUpdate(pos, pos);
            }
        } else if (soiledTime <= 0) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    ////////////////////////////////////////////////////////////////////

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(pos, blockType.getDefaultState(), blockType.getDefaultState(), 3);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    private static class WashingTank implements IFluidHandler {

        boolean full = false;

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return new IFluidTankProperties[] { new FluidTankProperties(null, ConfigHandler.washer_fluid_amount) };
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (full || resource == null || !resource.getFluid().getName().equals(ConfigHandler.washer_fluid)
                    || resource.amount < ConfigHandler.washer_fluid_amount) {
                return 0;
            }
            if (doFill) {
                full = true;
            }
            return ConfigHandler.washer_fluid_amount;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return null;
        }

    }

}
