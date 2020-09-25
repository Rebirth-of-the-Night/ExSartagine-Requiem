package subaraki.exsartagine.recipe;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import subaraki.exsartagine.item.ExSartagineItems;

public class Recipes {

	public Recipes() {

		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_chicken_raw, new ItemStack(ExSartagineItems.pizza_chicken), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_meat_raw, new ItemStack(ExSartagineItems.pizza_meat), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_sweet_raw, new ItemStack(ExSartagineItems.pizza_sweet), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_fish_raw, new ItemStack(ExSartagineItems.pizza_fish), 0.6f);

		FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_dough, new ItemStack(ExSartagineItems.bread_fine), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_meat_raw, new ItemStack(ExSartagineItems.bread_meat), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_veggie_raw, new ItemStack(ExSartagineItems.bread_veggie), 0.6f);

		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(Items.EGG), new ItemStack(ExSartagineItems.boiled_egg,1));
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(Items.BEETROOT_SEEDS), new ItemStack(ExSartagineItems.boiled_beans,1));
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(Items.POTATO), new ItemStack(ExSartagineItems.boiled_potato,1));

		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE)), new ItemStack(ExSartagineItems.salt,1));
		
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(ExSartagineItems.spaghetti_raw), new ItemStack(ExSartagineItems.spaghetti_cooked));
		
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(ExSartagineItems.noodles_chicken), new ItemStack(ExSartagineItems.noodles_chicken_cooked));
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(ExSartagineItems.noodles_fish), new ItemStack(ExSartagineItems.noodles_fish_cooked));
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(ExSartagineItems.noodles_meat), new ItemStack(ExSartagineItems.noodles_meat_cooked));
		PotRecipes.getInstance().addRecipe(Ingredient.fromItem(ExSartagineItems.noodles_veggie), new ItemStack(ExSartagineItems.noodles_veggie_cooked));

		SmelterRecipes.getInstance().addEntry(new ItemStack(Blocks.IRON_ORE));
		SmelterRecipes.getInstance().addEntry(new ItemStack(Blocks.GOLD_ORE));
		SmelterRecipes.getInstance().addEntry(new ItemStack(Items.CLAY_BALL));
		SmelterRecipes.getInstance().addEntry(new ItemStack(Blocks.NETHERRACK));
	}
}
