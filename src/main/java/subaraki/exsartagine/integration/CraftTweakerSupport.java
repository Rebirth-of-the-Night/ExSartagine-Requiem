package subaraki.exsartagine.integration;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.util.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods." + Reference.MODID + ".ExSartagine")
public class CraftTweakerSupport {

    @ZenMethod
    public static void addPotRecipe(IIngredient input, IItemStack output) {
        Recipes.addPotRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removePotRecipe(IItemStack output) {
        return Recipes.removePotRecipe(CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removePotRecipe(IIngredient input, IItemStack output) {
        return Recipes.removePotRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void addPanRecipe(IIngredient input, IItemStack output) {
        Recipes.addPanRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removePanRecipe(IItemStack output) {
        return Recipes.removePanRecipe(CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removePanRecipe(IIngredient input, IItemStack output) {
        return Recipes.removePanRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void addSmelterRecipe(IIngredient input, IItemStack output) {
        Recipes.addSmelterRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removeSmelterRecipe(IItemStack output) {
        return Recipes.removeSmelterRecipe(CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removeSmelterRecipe(IIngredient input, IItemStack output) {
        return Recipes.removeSmelterRecipe(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output));
    }

    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, IIngredient catalyst, ILiquidStack fluid, IItemStack[] outputs, @Optional("200") int time) {
        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        Ingredient iCatalyst = CraftTweakerMC.getIngredient(catalyst);
        FluidStack iFluid = CraftTweakerMC.getLiquidStack(fluid);
        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
        Recipes.addKettleRecipe(iinputs, iCatalyst, iFluid, iOutputs, time);
    }
    
    @ZenMethod
    public static boolean removeKettleRecipe(IItemStack output) {
        return Recipes.removeKettleRecipe(CraftTweakerMC.getItemStack(output));
    }
    
    @ZenMethod
    public static boolean removeKettleRecipe(IItemStack[] outputs) {
        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
        return Recipes.removeKettleRecipe(iOutputs);
    }
    
    @ZenMethod
    public static boolean removeKettleRecipe(IIngredient[] inputs, IIngredient catalyst, IItemStack[] outputs, @Optional("-1") int time) {
        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        Ingredient iCatalyst = CraftTweakerMC.getIngredient(catalyst);
        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
        return Recipes.removeKettleRecipe(iinputs, iCatalyst, null, iOutputs, time);
    }
    
    @ZenMethod
    public static boolean removeKettleRecipe(IIngredient[] inputs, IIngredient catalyst, ILiquidStack fluid, IItemStack[] outputs, @Optional("-1") int time) {
        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        Ingredient iCatalyst = CraftTweakerMC.getIngredient(catalyst);
        FluidStack iFluid = CraftTweakerMC.getLiquidStack(fluid);
        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
        return Recipes.removeKettleRecipe(iinputs, iCatalyst, iFluid, iOutputs, time);
    }

    @ZenMethod
    public static void addHeatSource(IBlockState source) {
        Recipes.addHeatSource(CraftTweakerMC.getBlockState(source).getBlock());
    }
    
    @ZenMethod
    public static boolean removeHeatSource(IBlockState source) {
        return Recipes.removeHeatSource(CraftTweakerMC.getBlockState(source).getBlock());
    }

    @ZenMethod
    public static void addPlaceable(IBlockState source) {
        Recipes.addPlaceable(CraftTweakerMC.getBlockState(source).getBlock());
    }
    
    @ZenMethod
    public static boolean removePlaceable(IBlockState source) {
        return Recipes.removePlaceable(CraftTweakerMC.getBlockState(source).getBlock());
    }
}
