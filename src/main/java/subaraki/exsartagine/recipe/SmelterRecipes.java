package subaraki.exsartagine.recipe;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class SmelterRecipes {

	private static final SmelterRecipes INSTANCE = new SmelterRecipes();
	private final ArrayList<SmelterRecipe> smelterRecipeList = new ArrayList<SmelterRecipe>();

	public static SmelterRecipes getInstance() {
		return INSTANCE;
	}

	public void addEntry(ItemStack entry){
		SmelterRecipe smelterRecipe = new SmelterRecipe(entry);
		smelterRecipeList.add(smelterRecipe);
	}

	public void removeEntry(ItemStack entry){
		SmelterRecipe smelterRecipe = new SmelterRecipe(entry);
		for(SmelterRecipe se : smelterRecipeList)
		{
			if(se.equals(smelterRecipe))
			{
				smelterRecipeList.remove(se);
				break;
			}
		}
	}

	public ItemStack getResult(ItemStack stack)
	{
		SmelterRecipe entry = new SmelterRecipe(stack);

		if(smelterRecipeList.contains(entry))
		{
			if(!FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty())
				return FurnaceRecipes.instance().getSmeltingResult(stack);
		}

		return ItemStack.EMPTY;
	}

	private static boolean areEntriesEqual(SmelterRecipe a, SmelterRecipe b){

		return a.meta == b.meta && a.item == b.item;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	public static class SmelterRecipe {

		int amount;
		Item item;
		int meta;

		public SmelterRecipe(ItemStack stack) {
			amount = stack.getCount();
			item = stack.getItem();
			meta = stack.getMetadata();
		}

		public ItemStack getStack(){
			return new ItemStack(item,amount,meta);
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof SmelterRecipe))
				return false;

			return areEntriesEqual((SmelterRecipe) obj, this);
		}
	}
}
