package subaraki.exsartagine.tileentity.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.recipe.CustomRecipe;
import subaraki.exsartagine.recipe.DirtyingRecipe;
import subaraki.exsartagine.recipe.ModRecipes;

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

    @Override
    public boolean canDoWork(R recipe) {
        if (!activeHeatSourceBelow()) {
            return false;
        }
        if (soiledTime <= 0) {
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
}
