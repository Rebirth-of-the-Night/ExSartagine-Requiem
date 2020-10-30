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
				if(getEntry().getCount() > 0 && (getResult().isEmpty() || getResult().getCount() < getResult().getMaxStackSize()))
				{
					if(getResult().isEmpty())
					{
						ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(getEntry()).copy();
						
						setResult(itemstack.copy());
					}
					else
					{
						getResult().grow(1);
					}
					getEntry().shrink(1);
				}
			}
			progress = 0;
			world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), ExSartagineBlocks.pan.getDefaultState(), 3);
		}

		if(isCooking)
		{
			if(getEntry().getCount() > 0 && 
					(getResult().getItem().equals(FurnaceRecipes.instance().getSmeltingResult(getEntryStackOne()).getItem()) || getResult().isEmpty()))
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
