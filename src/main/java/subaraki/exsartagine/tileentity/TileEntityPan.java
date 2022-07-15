package subaraki.exsartagine.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.recipe.Recipes;

public class TileEntityPan extends TileEntityCooker {

	public TileEntityPan() {
		initInventory();
	}
	
	@Override
	public void update() {

		if(progress == 125) {
			if(!world.isRemote) {
				ItemStack input = getInput();
				ItemStack output = getOutput();
				if(!input.isEmpty() && (output.isEmpty() || output.getCount() < output.getMaxStackSize()))
				{
					if(output.isEmpty())
					{
						ItemStack itemstack = Recipes.getCookingResult(getInventory(),"pan").copy();
						setResult(itemstack.copy());
					}
					else
					{
						output.grow(1);
					}
					input.shrink(1);
				}
			}
			progress = 0;
			world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), ExSartagineBlocks.pan.getDefaultState(), 3);
		}

		if(isCooking)
		{
			if(!getInput().isEmpty() &&
					(getOutput().getItem().equals(Recipes.getCookingResult(getInventory(),"pan").getItem()) || getOutput().isEmpty()))
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
