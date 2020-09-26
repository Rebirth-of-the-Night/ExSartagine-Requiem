package subaraki.exsartagine.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.item.ExSartagineItems;

import java.util.ArrayList;
import java.util.List;

public class Recipes {

	private static final List<Block> heatSources = new ArrayList<>();

	private static final List<Block> placeable = new ArrayList<>();

	public static void addHeatSource(Block block) {
		heatSources.add(block);
		addPlaceable(block);
	}

	public static void addPlaceable(Block block) {
		placeable.add(block);
	}

	public static boolean isHeatSource(Block block) {
		return heatSources.contains(block);
	}

	public static boolean isPlaceable(Block block) {
		return placeable.contains(block);
	}

	public static void init() {
		addPlaceable(Blocks.FURNACE);
		addHeatSource(Blocks.LIT_FURNACE);
		addPlaceable(ExSartagineBlocks.range_extension);
		addHeatSource(ExSartagineBlocks.range_extension_lit);

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

		SmelterRecipes.getInstance().addRecipe(new ItemStack(Blocks.IRON_ORE));
		SmelterRecipes.getInstance().addRecipe(new ItemStack(Blocks.GOLD_ORE));
		SmelterRecipes.getInstance().addRecipe(new ItemStack(Items.CLAY_BALL));
		SmelterRecipes.getInstance().addRecipe(new ItemStack(Blocks.NETHERRACK));
	}
}
