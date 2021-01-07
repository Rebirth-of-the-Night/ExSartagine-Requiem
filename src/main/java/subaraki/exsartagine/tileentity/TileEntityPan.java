package subaraki.exsartagine.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.recipe.Recipes;

public class TileEntityPan extends TileEntityCooker {

	public TileEntityPan() {
		initInventory();
	}
	
	@Override
	public void update() {

		if(progress == 125)
		{
			if(!world.isRemote)
			{
				if(getInput().getCount() > 0 && (getOutput().isEmpty() || getOutput().getCount() < getOutput().getMaxStackSize()))
				{
					if(getOutput().isEmpty())
					{
						ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(getInput()).copy();
						
						setResult(itemstack.copy());
					}
					else
					{
						getOutput().grow(1);
					}
					getInput().shrink(1);
				}
			}
			progress = 0;
			world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), ExSartagineBlocks.pan.getDefaultState(), 3);
		}

		if(isCooking)
		{
			if(getInput().getCount() > 0 &&
					(getOutput().getItem().equals(FurnaceRecipes.instance().getSmeltingResult(getEntryStackOne()).getItem()) || getOutput().isEmpty()))
				progress++;
			else if (progress > 0)
				progress--;
		}
	}

	@Override
	public int getCookTime() {
		return 125;
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return Recipes.hasResult(stack,"pan");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}
}
