package subaraki.exsartagine.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.WokRecipe;
import subaraki.exsartagine.tileentity.util.FluidRecipeBlockEntity;

import java.util.List;

public class WokBlockEntity extends FluidRecipeBlockEntity<ItemStackHandler, FluidTank, WokRecipe> {

	public WokBlockEntity() {
		initInventory();
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			if (canStart()) {
				WokRecipe recipe = getOrCreateRecipe();
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
			} else {
				decreaseProgress();
			}
		}
	}

	public ItemStack addSingleItem(ItemStack stack) {
		ItemStack stack1 = stack.copy();
		for (int i = 0; i < inventoryInput.getSlots();i++) {
			stack1 = inventoryInput.insertItem(i,stack1,false);
			if (stack1.isEmpty()) return ItemStack.EMPTY;
		}
		return stack1;
	}

	@Override
	public void processItems() {
		List<ItemStack> results = cached.getResults(inventoryInput);

		for (int i = 0; i < inventoryInput.getSlots(); ++i) {
			ItemStack itemstack = this.inventoryInput.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				this.inventoryInput.extractItem(i, 1, false);
			}
		}

		for (ItemStack stack : results) {
			ItemStack remainder = stack.copy();
			for (int i = 0; i < inventoryOutput.getSlots(); i++) {
				remainder = inventoryOutput.insertItem(i, remainder, false);
				if (remainder.isEmpty()) {
					break;
				}
			}
		}
	}

	@Override
	public boolean checkFluidInv(WokRecipe recipe) {
		return true;
	}

	protected boolean isCooking = false;

	/**inits inventory with 9 slots. input and output*/
	protected void initInventory(){
		inventoryInput = new WokStackHandler();
		inventoryOutput = new WokStackHandler();
		fluidInventoryInput = new WokTank(10000);
		fluidInventoryInput.setTileEntity(this);
		recipeType = RecipeTypes.WOK;
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
				return (T) inventoryInput;
		}
		return super.getCapability(capability, facing);
	}



	public void setCooking(){
		isCooking = true;
	}

	public void stopCooking(){
		isCooking = false;
		progress = 0;
	}

	public boolean isCooking(){
		return isCooking;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("cooktime", progress);
		compound.setBoolean("cooking", isCooking);
		compound.setTag("inv", inventoryInput.serializeNBT());
		compound.setTag("invO", inventoryOutput.serializeNBT());
		compound.setTag("fluidinv",fluidInventoryInput.writeToNBT(new NBTTagCompound()));
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(compound.hasKey("cooktime") && compound.hasKey("cooking")){
			this.isCooking = compound.getBoolean("cooking");
			this.progress = compound.getInteger("cooktime");
		}
		inventoryInput.deserializeNBT(compound.getCompoundTag("inv"));
		inventoryOutput.deserializeNBT(compound.getCompoundTag("invO"));
		fluidInventoryInput.readFromNBT(compound.getCompoundTag("fluidinv"));
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
	public void markDirty() {
		super.markDirty();
		world.notifyBlockUpdate(pos, blockType.getDefaultState(), blockType.getDefaultState(), 3);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	public void giveItems(EntityPlayer playerIn) {
		for (int i = 0; i < inventoryOutput.getSlots();i++) {
			ItemStack stack = inventoryOutput.extractItem(i,Integer.MAX_VALUE,false);
			ItemHandlerHelper.giveItemToPlayer(playerIn,stack);
		}
	}

	public class WokStackHandler extends ItemStackHandler {

		WokStackHandler() {
			super(9);
		}
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			markDirty();
		}
	}

	public class WokTank extends FluidTank {

		public WokTank(int capacity) {
			super(capacity);
		}

		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			markDirty();
		}
	}
}
