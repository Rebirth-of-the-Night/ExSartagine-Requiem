package subaraki.exsartagine.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.block.BlockRange;
import subaraki.exsartagine.block.BlockRangeExtension;
import subaraki.exsartagine.block.ExSartagineBlocks;

public class TileEntityRange extends TileEntity implements ITickable {

    private final ItemStackHandler inventory = new ItemStackHandler(9);

    private final List<BlockPos> connected = new ArrayList<>();

    /**
     * how much 'cooktime' from the item inserted is left
     */
    private int fuelTimer = 0;
    /**
     * used in gui for image drawing
     */
    private int maxFuelTimer = 0;

    @Override
    public void update() {
        //Decrease
        if (fuelTimer > 0)
            fuelTimer--;


        if (!world.isRemote) {
            //look for fuel if we ran out
            if (fuelTimer == 0) {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty() && TileEntityFurnace.isItemFuel(stack)) {
                        maxFuelTimer = fuelTimer = TileEntityFurnace.getItemBurnTime(stack);
                        setCooking(true);
                        //shrink after getting fuel timer, or when stack was 1, fueltimer cannot get timer from stack 0
                        inventory.getStackInSlot(i).shrink(1);
                        markDirty();
                        break;
                    }
                }
            }

            // if no fuel was set and the tile is cooking
            if (fuelTimer == 0 && isCooking()) {
                setCooking(false);
                markDirty();
            }
        }
    }

    private void setCooking(boolean cooking) {
        setRangeConnectionsCooking(cooking);
        IBlockState state = world.getBlockState(pos);
        IBlockState newState = state.withProperty(BlockRange.HEATED,cooking);
        world.setBlockState(pos,newState);
    }

    public boolean isCooking() {
        return world.getBlockState(pos).getValue(BlockRange.HEATED);
    }


    public boolean isFueled() {
        return fuelTimer > 0;
    }

    public int getFuelTimer() {
        return fuelTimer;
    }

    public int getMaxFuelTimer() {
        return maxFuelTimer;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inv", inventory.serializeNBT());

        compound.setInteger("fuel", fuelTimer);
        compound.setInteger("max", maxFuelTimer);

        NBTTagCompound connections = new NBTTagCompound();
        int slot = 0;
        for (BlockPos pos : connected) {
            connections.setLong(Integer.toString(slot), pos.toLong());
            slot++;
        }
        compound.setTag("connections", connections);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inventory.deserializeNBT(compound.getCompoundTag("inv"));
        fuelTimer = compound.getInteger("fuel");
        maxFuelTimer = compound.getInteger("max");

        connected.clear();
        NBTTagCompound connections = compound.getCompoundTag("connections");
        for (int i = 0; i < 4; i++)
            if (connections.hasKey(String.valueOf(i))) {
                BlockPos pos = BlockPos.fromLong(connections.getLong(String.valueOf(i)));
                connected.add(pos);
            }
    }

    public boolean canConnect() {
        //if the last one is filled, the rest is too.
        return connected.size() < 4;
    }

    public void connect(TileEntityRangeExtension tere) {
        if (canConnect()) {
            connected.add(tere.getPos());
            tere.setParentRange(getPos());
            setRangeConnectionCooking(tere.getPos(),isCooking());
        }
        markDirty();
    }

    public void disconnect(BlockPos entry) {
        connected.remove(entry);
        markDirty();
    }

    public void setRangeConnectionsCooking(boolean setCooking) {
        if (!connected.isEmpty()) {

            for (BlockPos extPos : connected) {
                setRangeConnectionCooking(extPos,setCooking);
            }
        }
    }

    public void setRangeConnectionCooking(BlockPos extPos,boolean setCooking) {
        IBlockState state = world.getBlockState(extPos);
        if (state.getBlock() instanceof BlockRangeExtension) {
            Block blockNew = setCooking ? ExSartagineBlocks.range_extended_lit : ExSartagineBlocks.range_extended;
            IBlockState state1 = blockNew.getDefaultState().
                    withProperty(BlockRangeExtension.FACING, state.getValue(BlockRangeExtension.FACING));

            world.setBlockState(extPos,state1);

            //setting blockstates generates a new blockentity, make sure it's connected
            TileEntity te = world.getTileEntity(extPos);

            if (te instanceof TileEntityRangeExtension) {
                TileEntityRangeExtension rangeExtension = (TileEntityRangeExtension) te;
                rangeExtension.setParentRange(pos);
            }
        }
    }

        @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
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
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
