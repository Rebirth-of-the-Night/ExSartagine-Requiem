package subaraki.exsartagine.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.exsartagine.block.BlockSmelter;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.util.ConfigHandler;

public class TileEntitySmelter extends TileEntityCooker {

	private static final int BONUSSLOT = 2;
	private final int bonusChance = ConfigHandler.percent; // in percentage

	public TileEntitySmelter() {
		initInventory(3);
	}

	@Override
	public void update() {

		if(progress == 199)
		{
			if(!world.isRemote)
			{
				if(getInput().getCount() > 0 &&
						(getOutput().isEmpty() || getOutput().getCount() < getOutput().getMaxStackSize()))
				{
					if(world.rand.nextInt(100)+1 <= bonusChance){ //+1, so 0% is no chance [1-100]
						if(getBonus().isEmpty())
						{
							ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(getEntryStackOne()).copy();
							setResult(BONUSSLOT, itemstack);
						}
						else
							getBonus().grow(1);
					}

					if(getOutput().isEmpty())
					{
						ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(getEntryStackOne()).copy();
						setResult(itemstack);
						getInput().shrink(1);
					}
					else
					{
						getOutput().grow(1);
						getInput().shrink(1);
					}
				}
			}
			progress = 0;
			world.notifyBlockUpdate(getPos(), world.getBlockState(getPos()), ExSartagineBlocks.smelter.getDefaultState(), 3);
		}

		if(isCooking)
		{
			if(getInput().getCount() > 0 &&
					(getOutput().getItem().equals(FurnaceRecipes.instance().getSmeltingResult(getEntryStackOne()).getItem()) || getOutput().isEmpty()))
				progress++;
			else if (progress > 0)
				progress--;
		}
		
		if(!world.isRemote)
		{
			//set lava block rendering
			if(!world.getBlockState(pos).getValue(BlockSmelter.FULL) && (!getOutput().isEmpty() || !getBonus().isEmpty()))
				world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockSmelter.FULL, true), 3);
			//set lava block gone
			if(world.getBlockState(pos).getValue(BlockSmelter.FULL) && (getOutput().isEmpty() && getBonus().isEmpty()))
				world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockSmelter.FULL, false), 3);
	
		}
	}

	@Override
	public int getCookTime() {
		return 199;
	}

	private ItemStack getBonus() {
		return getOutput(BONUSSLOT);
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return Recipes.hasResult(stack, RecipeTypes.SMELTER);
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
