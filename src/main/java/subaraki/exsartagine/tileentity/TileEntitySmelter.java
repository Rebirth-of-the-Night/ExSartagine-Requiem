package subaraki.exsartagine.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.exsartagine.block.BlockSmelter;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.util.ConfigHandler;

public class TileEntitySmelter extends TileEntityCooker {

	private static final int BONUSSLOT = 2;
	private final int bonusChance = ConfigHandler.percent; // in percentage

	public TileEntitySmelter() {
		initInventory(3);
		clientCookTime = 199;
	}

	@Override
	public void update() {

		if(progress >= clientCookTime)
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

					soiledTime = 0; // TODO set soiled time once recipe logic is fixed
				}
			}
			progress = 0;

			markDirty();
		}

		if(activeHeatSourceBelow())
		{
			if(getInput().getCount() > 0 &&
					(getOutput().getItem().equals(FurnaceRecipes.instance().getSmeltingResult(getEntryStackOne()).getItem()) || getOutput().isEmpty())) {
				progress++;
				markDirty();
			} else if (progress > 0) {
				progress--;
				markDirty();
			}
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

	private ItemStack getBonus() {
		return getOutput(BONUSSLOT);
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return ModRecipes.hasResult(stack, RecipeTypes.SMELTER);
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
