package subaraki.exsartagine.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.WokRecipe;
import subaraki.exsartagine.tileentity.util.FluidRecipeBlockEntity;

import java.util.List;

public class WokBlockEntity extends FluidRecipeBlockEntity<ItemStackHandler, FluidTank, WokRecipe> implements ITickable {

	public WokBlockEntity() {
		initInventory();
	}

	private int flips;

	public double rotation;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (canStart() && activeHeatSourceBelow()) {
				WokRecipe recipe = getOrCreateRecipe();
				if (recipe.getCookTime() <= progress && canProcess(recipe)) {
					process();
				} else {
					if (cooking) {
						if (world.getTotalWorldTime() % 20 == 0)
							world.playSound(null,pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ModSounds.FRYING, SoundCategory.BLOCKS, 1, 1);
					} else {
						start();
					}
					progress++;
					markDirty();
				}
			} else {
				decreaseProgress();
			}
		}
	}

	public boolean canProcess(WokRecipe recipe) {
		return recipe.getFlips() <= flips;
	}

	public ItemStack addSingleItem(ItemStack stack) {
		ItemStack stack1 = stack.copy();
		for (int i = 0; i < inventoryInput.getSlots();i++) {
			stack1 = inventoryInput.insertItem(i,stack1,false);
			if (stack1.isEmpty()) return ItemStack.EMPTY;
		}
		return stack1;
	}

	public void flip(EntityPlayer player,ItemStack stack) {
		WokRecipe recipe = getOrCreateRecipe();
		if (recipe == null) return;
		flips++;

		if (flips == recipe.getFlips()) {
			stack.damageItem(1,player);
		}

		rotation = world.rand.nextDouble() * 360;
		markDirty();
	}

	@Override
	public void processItems() {
		List<ItemStack> results = cached.getResults(inventoryInput);
		int count = 0;

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

		soiledTime = Math.max(cached.getDirtyTime(), 0);
	}

	@Override
	public void process() {
		super.process();
		flips = 0;
	}

	@Override
	public boolean checkFluidInv(WokRecipe recipe) {
		return true;
	}

	/**inits inventory with 9 slots. input and output*/
	protected void initInventory(){
		inventoryInput = new WokStackHandler();
		inventoryOutput = new WokStackHandler();
		fluidInventoryInput = new WokTank(10000);
		fluidInventoryInput.setTileEntity(this);
		recipeType = RecipeTypes.WOK;
	}

	public void clearInput() {
		for (int i = 0; i < inventoryInput.getSlots();i++) {
			inventoryInput.setStackInSlot(i,ItemStack.EMPTY);
		}
		cooking = false;
		cached = null;
		progress = 0;
		markDirty();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
				return (T) inventoryInput;
		}
		return super.getCapability(capability, facing);
	}

	public boolean isCooking(){
		return cooking;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("inv", inventoryInput.serializeNBT());
		compound.setTag("invO", inventoryOutput.serializeNBT());
		compound.setTag("fluidinv",fluidInventoryInput.writeToNBT(new NBTTagCompound()));
		compound.setInteger("flips",flips);
		compound.setDouble("rotation",rotation);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inventoryInput.deserializeNBT(compound.getCompoundTag("inv"));
		inventoryOutput.deserializeNBT(compound.getCompoundTag("invO"));
		fluidInventoryInput.readFromNBT(compound.getCompoundTag("fluidinv"));
		flips = compound.getInteger("flips");
		rotation = compound.getDouble("rotation");
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
