package subaraki.exsartagine.integration;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.recipe.IIngredientWrapper;
import subaraki.exsartagine.recipe.Recipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

@ZenRegister
@ZenClass("mods." + ExSartagine.MODID + ".ExSartagine")
public class CraftTweakerSupport {

    @ZenMethod
    public static void addPotRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new AddPotAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }
    
    @ZenMethod
    public static void removePotRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new RemovePotAction(CraftTweakerMC.getItemStack(output)));
    }
    
    @ZenMethod
    public static void removePotRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new RemovePotAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }

    private static class AddPotAction implements IAction {
        private Ingredient input;
        private ItemStack output;

        public AddPotAction(Ingredient input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public String describe() {
            return "Adding pot recipe with input " + input;
        }

        @Override
        public void apply() {
            Recipes.addPotRecipe(input, output);
        }
    }

    private static class RemovePotAction implements IAction {
        private Ingredient input;
        private ItemStack output;

        public RemovePotAction(ItemStack output) {
            this.input = null;
            this.output = output;
        }

        public RemovePotAction(Ingredient input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public String describe() {
            if (this.input == null)
                return "Removing pot recipe with output " + output;
            else
                return "Removing pot recipe with input " + input + " and output " + output;
        }

        @Override
        public void apply() {
            boolean done;
            if (this.input == null)
                done = Recipes.removePotRecipe(output);
            else
                done = Recipes.removePotRecipe(input, output);
            
            if (!done)
                CraftTweakerAPI.logWarning("No pot recipes removed for input " + input + " and output " + output);
        }
    }


    @ZenMethod
    public static void addWokRecipe(IIngredient[] inputs, IItemStack[] outputs,@Optional int flips) {
        addWokRecipe(inputs,null,outputs,flips);
    }

    @ZenMethod
    public static void addWokRecipe(IIngredient[] inputs,ILiquidStack liquid, IItemStack[] outputs,@Optional int flips) {

        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        FluidStack fluidStack = CraftTweakerMC.getLiquidStack(liquid);

        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());

        CraftTweakerAPI.apply(new AddWokAction(iinputs,fluidStack, iOutputs,flips));
    }

    @ZenMethod
    public static void removeWokRecipe(String name) {
        CraftTweakerAPI.apply(new RemoveWokAction(new ResourceLocation(name)));
    }

    private static class AddWokAction implements IAction {
        private final List<Ingredient> inputs;
        private final FluidStack fluid;
        private final List<ItemStack> output;
        private final int flips;

        public AddWokAction(List<Ingredient> inputs, FluidStack fluid, List<ItemStack> output, int flips) {
            this.inputs = inputs;
            this.fluid = fluid;
            this.output = output;
            this.flips = flips;
        }

        @Override
        public String describe() {
            return "Adding wok recipe with input " + inputs;
        }

        @Override
        public void apply() {
            Recipes.addWokRecipe(inputs,fluid, output,flips);
        }
    }

    private static class RemoveWokAction implements IAction {
        private final ResourceLocation name;

        public RemoveWokAction(ResourceLocation name) {
            this.name = name;
        }

        @Override
        public String describe() {
                return "Removing wok recipe with name " + name;
        }

        @Override
        public void apply() {
            boolean done;
                done = Recipes.removeWokRecipeByName(name);

            if (!done) {
                    CraftTweakerAPI.logWarning("No pot recipes removed for name " + name);
            }
        }
    }

    @ZenMethod
    public static void addSmelterRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new AddSmelterAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }

    @ZenMethod
    public static void removeSmelterRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new RemoveSmelterAction(CraftTweakerMC.getItemStack(output)));
    }

    @ZenMethod
    public static void removeSmelterRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new RemoveSmelterAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }

    private static class AddSmelterAction implements IAction {
        private Ingredient input;
        private ItemStack output;

        public AddSmelterAction(Ingredient input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public String describe() {
            return "Adding pot recipe with input " + input;
        }

        @Override
        public void apply() {
            Recipes.addSmelterRecipe(input, output);
        }
    }

    private static class RemoveSmelterAction implements IAction {
        private Ingredient input;
        private ItemStack output;

        public RemoveSmelterAction(ItemStack output) {
            this.input = null;
            this.output = output;
        }

        public RemoveSmelterAction(Ingredient input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public String describe() {
            if (this.input == null)
                return "Removing smelter recipe with output " + output;
            else
                return "Removing smelter recipe with input " + input + " and output " + output;
        }

        @Override
        public void apply() {
            boolean done;
            if (this.input == null)
                done = Recipes.removeSmelterRecipe(output);
            else
                done = Recipes.removeSmelterRecipe(input, output);

            if (!done) {
                if (this.input == null)
                    CraftTweakerAPI.logWarning("No smelter recipes removed for output " + output);
                else
                    CraftTweakerAPI.logWarning("No smelter recipes removed for input " + input + " and output " + output);
            }
        }
    }

    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, ILiquidStack fluidInput,IItemStack[] outputs, @Optional("200") int time) {
        addKettleRecipe(inputs, null, fluidInput, null, outputs, time);
    }
    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, IIngredient catalyst, ILiquidStack fluidInput,IItemStack[] outputs, @Optional("200") int time) {
        addKettleRecipe(inputs, catalyst, fluidInput, null, outputs, time);
    }

    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, IIngredient catalyst, ILiquidStack fluidInput,
                                       ILiquidStack fluidOutput,IItemStack[] outputs, @Optional("200") int time) {
        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        Ingredient iCatalyst = new IIngredientWrapper(catalyst);
        FluidStack iFluidInput = CraftTweakerMC.getLiquidStack(fluidInput);
        FluidStack iFluidOutput = CraftTweakerMC.getLiquidStack(fluidOutput);
        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
        CraftTweakerAPI.apply(new AddKettleAction(iinputs, iCatalyst, iFluidInput,iFluidOutput, iOutputs, time));
    }

    private static class AddKettleAction implements IAction {
        private List<Ingredient> inputs;
        private Ingredient catalyst;
        private FluidStack fluidInput;
        private FluidStack fluidOutput;
        private List<ItemStack> outputs;
        private int time;

        public AddKettleAction(List<Ingredient> inputs, Ingredient catalyst, @Nullable FluidStack fluidInput, @Nullable FluidStack fluidOutput, List<ItemStack> outputs, int time) {
            this.inputs = inputs;
            this.catalyst = catalyst;
            this.fluidInput = fluidInput;
            this.fluidOutput = fluidOutput;
            this.outputs = outputs;
            this.time = time;
        }

        @Override
        public String describe() {
            return  "Adding kettle " +
                    this.fluidOutput == null ? "consumption" : "transformation" +
                    " recipe with inputs " + inputs + ", catalyst " +
                    catalyst + ", fluid input " + fluidInput + (this.fluidOutput == null ?
                        "" :
                        ", fluid output " + fluidOutput) +
                    ", outputs " + outputs + ", and duration " + time;
        }

        @Override
        public void apply() {
            Recipes.addKettleRecipe(inputs, catalyst, fluidInput, fluidOutput, outputs, time);
        }
    }
    
    @ZenMethod
    public static void removeKettleRecipe(String name) {
        CraftTweakerAPI.apply(new RemoveKettleAction(new ResourceLocation(name)));
    }

    private static class RemoveKettleAction implements IAction {
        private ResourceLocation name;

        public RemoveKettleAction(ResourceLocation name) {
            this.name = name;
        }

        @Override
        public String describe() {
            return "Removing kettle recipe with name " + name;
        }

        @Override
        public void apply() {
            boolean done = Recipes.removeKettleRecipeByName(name);

            if (!done)
                CraftTweakerAPI.logWarning("No kettle recipes removed for name " + name);
        }
    }

    @ZenMethod
    public static void addHeatSource(IBlockState source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlockState(source), true, true));
    }

    @ZenMethod
    public static void addHeatSource(IBlock source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlock(source).getBlockState().getValidStates(), true, true));
    }
    
    @ZenMethod
    public static void removeHeatSource(IBlockState source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlockState(source), true, false));
    }

    @ZenMethod
    public static void removeHeatSource(IBlock source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlock(source).getBlockState().getValidStates(), true, false));
    }

    @ZenMethod
    public static void addPlaceable(IBlockState source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlockState(source), false, true));
    }

    @ZenMethod
    public static void addPlaceable(IBlock source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlock(source).getBlockState().getValidStates(), false, true));
    }
    
    @ZenMethod
    public static void removePlaceable(IBlockState source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlockState(source), false, false));
    }

    @ZenMethod
    public static void removePlaceable(IBlock source) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlock(source).getBlockState().getValidStates(), false, false));
    }

    private static class BlockStateAction implements IAction {
        private Collection<net.minecraft.block.state.IBlockState> states;
        private boolean isHeatSource;
        private boolean add;

        public BlockStateAction(net.minecraft.block.state.IBlockState state, boolean isHeatSource, boolean add) {
            this(Sets.newHashSet(state), isHeatSource, add);
        }

        public BlockStateAction(Collection<net.minecraft.block.state.IBlockState> states, boolean isHeatSource, boolean add) {
            this.states = states;
            this.isHeatSource = isHeatSource;
            this.add = add;
        }

        @Override
        public String describe() {
            return (this.add ? "Adding " : "Removing ") +
                   (this.isHeatSource ? "heat source " : "placeable ") +
                   "with block states " + this.states.stream()
                            .map(net.minecraft.block.state.IBlockState::toString)
                            .collect(Collectors.joining(","));
        }

        @Override
        public void apply() {
            if (this.add) {
                if (this.isHeatSource)
                    Recipes.addHeatSources(states);
                else
                    Recipes.addPlaceables(states);
            } else {
                boolean done;
                if (this.isHeatSource)
                    done = Recipes.removeHeatSources(states);
                else
                    done = Recipes.removePlaceables(states);
                
                if (!done)
                    CraftTweakerAPI.logWarning("No " + 
                            (this.isHeatSource ? "heat sources " : "placeables ") +
                            "were removed for block state " + this.states);
            }
        }
    }
}
