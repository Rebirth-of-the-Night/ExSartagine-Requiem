package subaraki.exsartagine.integration;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import subaraki.exsartagine.recipe.FryingPanRecipes;
import subaraki.exsartagine.recipe.PotRecipes;
import subaraki.exsartagine.recipe.SmelterRecipes;
import subaraki.exsartagine.util.Reference;

@ZenRegister
@ZenClass("mods."+ Reference.MODID)
public class CraftTweakerSupport {

	@ZenMethod
	public static void addPotRecipe (IIngredient input, IItemStack output) {
		PotRecipes.getInstance().addRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
	}

	@ZenMethod
	public static void addSmelterRecipe (IIngredient entry) {
		SmelterRecipes.getInstance().addEntry(CraftTweakerMC.getItemStack(entry));
	}

	@ZenMethod
	public static void removeSmelterRecipe (IIngredient entry) {
		SmelterRecipes.getInstance().removeEntry(CraftTweakerMC.getItemStack(entry));
	}

	@ZenMethod
	public static void addPanRecipe (IIngredient input, IItemStack output) {
		FryingPanRecipes.getInstance().addRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
	}
}