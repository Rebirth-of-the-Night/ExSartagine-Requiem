package subaraki.exsartagine.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.*;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.recipe.WokRecipe;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;
import subaraki.exsartagine.util.Helpers;

import javax.annotation.Nullable;
import java.util.List;

public class WokBlockEntity extends KitchenwareBlockEntity<WokRecipe> {

	private final ItemStackHandler inventoryInput = new WokStackHandler();
	private final WokStackHandler inventoryOutput = new WokStackHandler();
	private final FluidTank fluidInventoryInput = new WokTank(10000);

	private int flips;

	public double rotation;

	public IItemHandlerModifiable getInventoryInput() {
		return inventoryInput;
	}

	public IItemHandlerModifiable getInventoryOutput() {
		return inventoryOutput;
	}

	@Override
	public IItemHandler getEntireItemInventory() {
		return new CombinedInvWrapper(inventoryInput, inventoryOutput);
	}

	public FluidTank getFluidInventoryInput() {
		return fluidInventoryInput;
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
		WokRecipe recipe = getRunningRecipe();
		if (recipe == null) return;
		flips++;

		if (flips == recipe.getFlips()) {
            Helpers.damageOrConsumeItem(player, stack);
		}

		rotation = world.rand.nextDouble() * 360;
		markDirty();
	}

	@Nullable
	@Override
	public WokRecipe findRecipe() {
		return ModRecipes.findFluidRecipe(inventoryInput, fluidInventoryInput, WokRecipe.class, RecipeTypes.WOK);
	}

	@Override
	public boolean doesRecipeMatch(WokRecipe recipe) {
		return recipe.match(inventoryInput, fluidInventoryInput);
	}

	@Override
	public boolean canFitOutputs(WokRecipe recipe) {
		List<ItemStack> results = recipe.getResults(inventoryInput);
		IItemHandlerModifiable tempOutput = Helpers.copyInventory(inventoryOutput);
		for (ItemStack stack : results) {
			if (!ItemHandlerHelper.insertItemStacked(tempOutput, stack, false).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canFinishRecipe(WokRecipe recipe) {
		return recipe.getFlips() <= flips;
	}

	@Override
	public void processRecipe(WokRecipe recipe) {
		// produce outputs
		for (ItemStack stack : recipe.getResults(inventoryInput)) {
			ItemHandlerHelper.insertItemStacked(inventoryOutput, stack.copy(), false);
		}

		// consume inputs
		for (int i = 0; i < inventoryInput.getSlots(); ++i) {
			this.inventoryInput.extractItem(i, 1, false);
		}
		if (recipe.getInputFluid() != null) {
			fluidInventoryInput.drain(recipe.getInputFluid().amount, true);
		}

		flips = 0;
		super.processRecipe(recipe);
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
