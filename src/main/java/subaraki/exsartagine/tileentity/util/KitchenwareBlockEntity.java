package subaraki.exsartagine.tileentity.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.recipe.Recipes;

public abstract class KitchenwareBlockEntity extends TileEntity {

    protected int progress = 0;
    protected int cookTime = 0;

    //check if the block below is hot
    public final boolean isHeated() {
        return Recipes.isHeatSource(world.getBlockState(pos.down()));
    }

    public abstract IItemHandler getEntireItemInventory();

    public int getProgress() {
        return progress;
    }

    public int getCookTime() {
        return cookTime;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("progress", progress);
        compound.setInteger("cooktime", cookTime);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.progress = compound.getInteger("progress");
        this.cookTime = compound.getInteger("cooktime");
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

    //calls readFromNbt by default. no need to add anything in here
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
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
