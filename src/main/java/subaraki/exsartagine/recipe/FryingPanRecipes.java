package subaraki.exsartagine.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class FryingPanRecipes {

	private static final List<FryingPanRecipe> recipes = new ArrayList<>();

	public static void addRecipe(Ingredient input, ItemStack result) {
		recipes.add(new FryingPanRecipe(input,result));
	}

	public static ItemStack getCookingResult(IItemHandler handler) {
		for (FryingPanRecipe recipe : recipes) {
			if (recipe.match(handler))
				return recipe.getResult(handler);
		}
		return ItemStack.EMPTY;
	}

	public static boolean hasResult(ItemStack stack) {
		return !getCookingResult(new ItemStackHandler(NonNullList.withSize(1,stack))).isEmpty();
	}
	
	public static class FryingPanRecipe implements CustomRecipe<IItemHandler> {

		private final Ingredient ingredient;
		private final ItemStack output;

		private static final int INPUT = 0;

		public FryingPanRecipe(Ingredient input, ItemStack output) {
			ingredient = input;
			this.output = output;
		}

		@Override
		public boolean match(IItemHandler handler) {
			return ingredient.test(handler.getStackInSlot(INPUT));
		}

		@Override
		public ItemStack getResult(IItemHandler handler) {
			return output.copy();
		}
	}
}
