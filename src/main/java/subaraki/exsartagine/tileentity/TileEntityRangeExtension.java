package subaraki.exsartagine.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import subaraki.exsartagine.block.BlockRange;
import subaraki.exsartagine.block.BlockRangeExtension;

public class TileEntityRangeExtension extends TileEntityCooktop implements ITickable {

	private BlockPos parentRange;
	
	public void setParentRange(BlockPos parentRange) {
		this.parentRange = parentRange;
		markDirty();
	}

	public BlockPos getParentRange() {
		return parentRange;
	}
	
	private TileEntity getHostCooker()
	{
		if(this.getWorld().getTileEntity(this.getPos().up()) != null)
		{
			return this.getWorld().getTileEntity(this.getPos().up());
		}
		
		return null;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		
		if(EnumFacing.DOWN.equals(facing))
		{
			if(getHostCooker() instanceof TileEntityCooker)
			{
				return getHostCooker().getCapability(capability, facing);
			}
		}
		
		return super.getCapability(capability, facing);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(EnumFacing.DOWN.equals(facing))
		{
			if(getHostCooker() instanceof TileEntityCooker)
			{
				return getHostCooker().hasCapability(capability, facing);
			}
		}
		return super.hasCapability(capability, facing);
	}

    @Override
    public BlockRange.Tier getEffectiveTier() {
        if (parentRange != null) {
            TileEntity te = getWorld().getTileEntity(parentRange);
            if (te instanceof TileEntityRange) {
                return ((TileEntityRange) te).getEffectiveTier();
            }
        }
        return BlockRange.Tier.HEARTH;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            Block block = getWorld().getBlockState(getPos()).getBlock();
            if (block instanceof BlockRangeExtension && ((BlockRangeExtension) block).isLit()) {
                getCooktopInventory().tick();
            }
        }
    }

    @Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (parentRange != null)
			compound.setLong("parent", parentRange.toLong());
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("parent"))
			parentRange = BlockPos.fromLong(compound.getLong("parent"));
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
		NBTTagCompound nbt =  super.getUpdateTag();
		writeToNBT(nbt);
		return nbt;
	}

	////////////////////////////////////////////////////////////////////

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState.getBlock() != newSate.getBlock();
	}
}
