package subaraki.exsartagine.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.item.ExSartagineItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Recipes {

	private static final List<Block> heatSources = new ArrayList<>();

	private static final List<Block> placeable = new ArrayList<>();

	protected static final Map<String,List<CustomRecipe<?>>> recipes = new HashMap<>();

	public static void addType(String type) {
		recipes.put(type,new ArrayList<>());
	}

	public static void addPotRecipe(Ingredient input, ItemStack result) {
		getRecipes("pot").add(new PotRecipe(input,result));
	}

	public static void addPanRecipe(Ingredient ingredient, ItemStack itemStack) {
		getRecipes("pan").add(new FryingPanRecipe(ingredient,itemStack));
	}

	public static void addSmelterRecipe(Ingredient ingredient, ItemStack itemStack) {
		getRecipes("smelter").add(new SmelterRecipe(ingredient,itemStack));
	}

	public static <I extends IItemHandler> ItemStack getCookingResult(I handler,String type) {
		for (CustomRecipe<IItemHandler> recipe : getRecipes(type)) {
			if (recipe.match(handler))
				return recipe.getResult(handler);
		}
		return ItemStack.EMPTY;
	}

	public static <I extends IItemHandler> boolean hasResult(ItemStack stack, String type) {
		return !stack.isEmpty() && getRecipes(type).stream().map(CustomRecipe::getIngredient).anyMatch(ingredient -> ingredient.test(stack));
	}

	public static void addHeatSource(Block block) {
		heatSources.add(block);
		addPlaceable(block);
	}

	public static void addPlaceable(Block block) {
		placeable.add(block);
	}

	public static <I extends IItemHandler, R extends CustomRecipe<I>> List<R> getRecipes(String type) {
		return (List<R>) Recipes.recipes.get(type);
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
		addHeatSource(Blocks.LAVA);

		addType("pan");
		addType("pot");
		addType("smelter");

		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_chicken_raw, new ItemStack(ExSartagineItems.pizza_chicken), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_meat_raw, new ItemStack(ExSartagineItems.pizza_meat), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_sweet_raw, new ItemStack(ExSartagineItems.pizza_sweet), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_fish_raw, new ItemStack(ExSartagineItems.pizza_fish), 0.6f);

		FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_dough, new ItemStack(ExSartagineItems.bread_fine), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_meat_raw, new ItemStack(ExSartagineItems.bread_meat), 0.6f);
		FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_veggie_raw, new ItemStack(ExSartagineItems.bread_veggie), 0.6f);

		List<CustomRecipe<?>> recipes = FurnaceRecipes.instance().getSmeltingList().entrySet().stream()
				.filter(entry -> entry.getKey().getItem() instanceof ItemFood)
				.map(entry -> new FryingPanRecipe(Ingredient.fromStacks(entry.getKey()),entry.getValue()))
				.collect(Collectors.toList());
		Recipes.recipes.put("pan",recipes);

		addPotRecipe(Ingredient.fromItem(Items.EGG), new ItemStack(ExSartagineItems.boiled_egg,1));
		addPotRecipe(Ingredient.fromItem(Items.BEETROOT_SEEDS), new ItemStack(ExSartagineItems.boiled_beans,1));
		addPotRecipe(Ingredient.fromItem(Items.POTATO), new ItemStack(ExSartagineItems.boiled_potato,1));

		addPotRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE)), new ItemStack(ExSartagineItems.salt,1));
		
		addPotRecipe(Ingredient.fromItem(ExSartagineItems.spaghetti_raw), new ItemStack(ExSartagineItems.spaghetti_cooked));
		
		addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_chicken), new ItemStack(ExSartagineItems.noodles_chicken_cooked));
		addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_fish), new ItemStack(ExSartagineItems.noodles_fish_cooked));
		addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_meat), new ItemStack(ExSartagineItems.noodles_meat_cooked));
		addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_veggie), new ItemStack(ExSartagineItems.noodles_veggie_cooked));

		addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.IRON_ORE)),new ItemStack(Items.IRON_INGOT));
		addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.GOLD_ORE)),new ItemStack(Items.GOLD_INGOT));
		addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Items.CLAY_BALL)),new ItemStack(Items.BRICK));
		addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.NETHERRACK)),new ItemStack(Items.NETHERBRICK));
	}
}
