package subaraki.exsartagine.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.block.BlockSmelter;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.recipe.SmelterRecipe;
import subaraki.exsartagine.util.ConfigHandler;
import subaraki.exsartagine.util.Helpers;

import javax.annotation.Nullable;

public class TileEntitySmelter extends TileEntityCooker<SmelterRecipe> {

	private static final int BONUSSLOT = 2;

	public TileEntitySmelter() {
		initInventory(3);
	}

	@Override
	public void update() {
		super.update();
		if (!world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			//set lava block rendering
			if (state.getValue(BlockSmelter.FULL)) {
				if (getOutput().isEmpty() && getBonus().isEmpty()) {
					world.setBlockState(pos, state.withProperty(BlockSmelter.FULL, false), 2);
				}
			} else {
				if (!getOutput().isEmpty() || !getBonus().isEmpty()) {
					world.setBlockState(pos, state.withProperty(BlockSmelter.FULL, true), 2);
				}
			}
		}
	}

	private ItemStack getBonus() {
		return getOutput(BONUSSLOT);
	}

	@Nullable
	@Override
	public SmelterRecipe findRecipe() {
		return ModRecipes.findRecipe(getInventory(), SmelterRecipe.class, RecipeTypes.SMELTER);
	}

	@Override
	public boolean doesRecipeMatch(SmelterRecipe recipe) {
		return recipe.itemMatch(getInventory());
	}

	@Override
	public boolean canFitOutputs(SmelterRecipe recipe) {
		ItemStack result = recipe.getResult(getInventory());
		return getInventory().insertItem(RESULT, result, true).isEmpty() && getInventory().insertItem(BONUSSLOT, result, true).isEmpty();
	}

	@Override
	public void processRecipe(SmelterRecipe recipe) {
		// produce outputs
		ItemStack result = recipe.getResult(getInventory());
		getInventory().insertItem(RESULT, result.copy(), false);
		if (Helpers.bernoulli(world.rand, ConfigHandler.percent)) {
			getInventory().insertItem(BONUSSLOT, result.copy(), false);
		}

		// consume input
		getInput().shrink(1);

		super.processRecipe(recipe);
	}

	@Override
	public boolean isValid(ItemStack stack) {
		return ModRecipes.hasResult(stack, RecipeTypes.SMELTER);
	}

}
