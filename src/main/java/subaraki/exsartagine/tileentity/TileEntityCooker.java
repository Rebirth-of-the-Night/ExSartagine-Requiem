package subaraki.exsartagine.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;

public abstract class TileEntityCooker extends KitchenwareBlockEntity implements ITickable {

	protected boolean heated = false;
	protected static final int RESULT = 1;
	protected static final int INPUT = 0;

	private ISHCooker inventory;
	private RangedWrapper input;
	private RangedWrapper output;
	
	/**init inventory with more slots, where 0 is input, and x>0 is output*/
	protected void initInventory(int slots) {
		inventory = new ISHCooker(slots);
		
		input = new RangedWrapper(inventory, 0, 1);
		output = new RangedWrapper(inventory, 1, slots);
	}

	public IItemHandler getEntireItemInventory() {
		return inventory;
	}


	public void setResult(ItemStack stack){
		setResult(RESULT, stack);
	}
	
	public void setResult(int slot, ItemStack stack){
		getInventory().insertItem(slot, stack, false);
	}
	
	public ItemStack getInput(){
		return getInventory().getStackInSlot(INPUT);
	}

	public ItemStack getOutput() {
		return this.getOutput(RESULT);
	}

	public ItemStack getOutput(int slot){
		return getInventory().getStackInSlot(slot);
	}
	
	public ItemStack getEntryStackOne(){
		ItemStack stack = getInventory().getStackInSlot(INPUT);
		return stack.copy(); 
	}

	abstract public boolean isValid(ItemStack stack);
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {

			return facing != EnumFacing.UP;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {

			if(facing == null)
				return (T) inventory;
			
			if(EnumFacing.DOWN == facing) //to prevent npe. facing can be null
				return (T) output;
			else
				return (T) input;

		}
		return super.getCapability(capability, facing);
	}

	public IItemHandler getInventory() {
		return inventory;
	}

	@Override
	public void setHeated(boolean hot){
		heated = hot;
		if (!heated) {
			progress = 0;
		}
	}

	public boolean isHeated(){
		return heated;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("heated", heated);
		compound.setTag("inv", inventory.serializeNBT());
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(compound.hasKey("cooktime") && compound.hasKey("cooking")){
			this.heated = compound.getBoolean("heated");
		}
		if(compound.hasKey("inv"))
			inventory.deserializeNBT(compound.getCompoundTag("inv"));
	}

	public class ISHCooker extends ItemStackHandler {

		public ISHCooker(int slots) {
			super(slots);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot == INPUT && TileEntityCooker.this.isValid(stack) || slot > INPUT)
				return super.insertItem(slot, stack, simulate);
			else
				return stack;
		}

		@Override
		protected void onContentsChanged(int slot) {
			TileEntityCooker.this.markDirty();
		}
	}
}
