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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.recipe.IIngredientWrapper;
import subaraki.exsartagine.recipe.ModRecipes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

@ZenRegister
@ZenClass("mods." + ExSartagine.MODID + ".ExSartagine")
public class CraftTweakerSupport {

    /**
     * Adds a pot recipe that consumes 50 mB of water
     * @param input the input item
     * @param output the output item
     * @param time the processing time in ticks, which defaults to 200
     * @param dirtyTime the amount of time in ticks the pot should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addPotRecipe(IIngredient input, IItemStack output, @Optional(valueLong = 200L) int time, @Optional(valueLong = 0L) int dirtyTime) {
        CraftTweakerAPI.apply(new AddPotAction(CraftTweakerMC.getIngredient(input), new FluidStack(FluidRegistry.WATER, 50), CraftTweakerMC.getItemStack(output), time, dirtyTime));
    }

    /**
     * Adds a pot recipe
     * @param input the input item
     * @param inputFluid the input fluid
     * @param output the output item
     * @param time the processing time in ticks, which defaults to 200
     * @param dirtyTime the amount of time in ticks the pot should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addPotRecipe(IIngredient input, ILiquidStack inputFluid, IItemStack output, @Optional(valueLong = 200L) int time, @Optional(valueLong = 0L) int dirtyTime) {
        CraftTweakerAPI.apply(new AddPotAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getLiquidStack(inputFluid), CraftTweakerMC.getItemStack(output), time, dirtyTime));
    }

    /**
     * Adds a cauldron recipe
     * @param input the input item
     * @param inputFluid the input fluid
     * @param output the output item
     * @param time the processing time in ticks, which defaults to 200
     * @param dirtyTime the amount of time in ticks the cauldron should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addCauldronRecipe(IIngredient input, ILiquidStack inputFluid, IItemStack output, @Optional(valueLong = 200L) int time, @Optional(valueLong = 0L) int dirtyTime) {
        CraftTweakerAPI.apply(new AddCauldronAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getLiquidStack(inputFluid), CraftTweakerMC.getItemStack(output), time, dirtyTime));
    }

    /**
     * Adds a cauldron recipe that consumes 50 mB of water
     * @param input the input item
     * @param output the output item
     * @param time the processing time in ticks, which defaults to 200
     * @param dirtyTime the amount of time in ticks the cauldron should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addCauldronRecipe(IIngredient input, IItemStack output, @Optional(valueLong = 200L) int time, @Optional(valueLong = 0L) int dirtyTime) {
        CraftTweakerAPI.apply(new AddCauldronAction(CraftTweakerMC.getIngredient(input), new FluidStack(FluidRegistry.WATER, 50), CraftTweakerMC.getItemStack(output), time, dirtyTime));
    }

    /**
     * Removes all pot recipes producing the given output
     * @param output the output to remove
     */
    @ZenMethod
    public static void removePotRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new RemovePotAction(CraftTweakerMC.getItemStack(output)));
    }

    /**
     * Removes all pot recipes that specify the given output AND matching the input
     * @param input the input to check
     * @param output the output to remove
     */
    @ZenMethod
    public static void removePotRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new RemovePotAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }

    /**
     * Removes all cauldron recipes producing the given output
     * @param output the output to remove
     */
    @ZenMethod
    public static void removeCauldronRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new RemoveCauldronAction(CraftTweakerMC.getItemStack(output)));
    }

    /**
     * Removes all cauldron recipes that specify the given output AND matching the input
     * @param input the input to check
     * @param output the output to remove
     */
    @ZenMethod
    public static void removeCauldronRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new RemoveCauldronAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }

    ///////////////////////////////

    /**
     * Adds wok recipe
     * @param inputs up to 9 ingredients can be specified
     * @param outputs up to 9 outputs can be specified
     * @param flips flips required with `ore:spatula`, defaults to 0
     * @param dirtyTime the amount of time in ticks the wok should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addWokRecipe(IIngredient[] inputs, IItemStack[] outputs,@Optional int flips, @Optional(valueLong = 0L) int dirtyTime) {
        addWokRecipe(inputs, null, outputs, flips, dirtyTime);
    }

    /**
     * Adds wok recipe with liquid requirement
     * @param inputs up to 9 ingredients can be specified
     * @param liquid input liquid required for recipe
     * @param outputs up to 9 outputs can be specified
     * @param flips flips required with `ore:spatula`, defaults to 0
     * @param dirtyTime the amount of time in ticks the wok should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addWokRecipe(IIngredient[] inputs,ILiquidStack liquid, IItemStack[] outputs,@Optional int flips, @Optional(valueLong = 0L) int dirtyTime) {

        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        FluidStack fluidStack = CraftTweakerMC.getLiquidStack(liquid);

        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());

        CraftTweakerAPI.apply(new AddWokAction(iinputs, fluidStack, iOutputs, flips, dirtyTime));
    }

    /**
     * Removes wok recipe by name
     * @param name the name of the recipe specified in tooltip
     */
    @ZenMethod
    public static void removeWokRecipe(String name) {
        CraftTweakerAPI.apply(new RemoveWokAction(new ResourceLocation(name)));
    }

    ////////////////////////////////////////////////////

    /**
     * Adds smelter recipe
     * @param input the input item matcher
     * @param output the output item
     * @param dirtyTime the amount of time in ticks the smelter should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addSmelterRecipe(IIngredient input, IItemStack output, @Optional(valueLong = 0L) int dirtyTime) {
        CraftTweakerAPI.apply(new AddSmelterAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output), dirtyTime));
    }

    /**
     * Removed all smelter recipes producing the given output
     * @param output
     */
    @ZenMethod
    public static void removeSmelterRecipe(IItemStack output) {
        CraftTweakerAPI.apply(new RemoveSmelterAction(CraftTweakerMC.getItemStack(output)));
    }

    /**
     * Removes all smelter recipes producing the given output AND matching the input
     * @param input
     * @param output
     */
    @ZenMethod
    public static void removeSmelterRecipe(IIngredient input, IItemStack output) {
        CraftTweakerAPI.apply(new RemoveSmelterAction(CraftTweakerMC.getIngredient(input), CraftTweakerMC.getItemStack(output)));
    }


    /**
     * Adds kettle recipe
     * @param inputs up to 9 ingredients can be specified
     * @param outputs up to 9 outputs can be specified
     * @param liquid input liquid required for recipe
     * @param time cook time in ticks, defaults to 200 (10 seconds)
     * @param dirtyTime the amount of time in ticks the kettle should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, ILiquidStack liquid,IItemStack[] outputs, @Optional("200") int time, @Optional(valueLong = 0L) int dirtyTime) {
        addKettleRecipe(inputs, null, liquid, null, outputs, time, dirtyTime);
    }

    /**
     * Adds kettle recipe with liquid requirement
     * @param inputs up to 9 ingredients can be specified
     * @param liquid input liquid required for recipe
     * @param outputs up to 9 outputs can be specified
     * @param time cook time in ticks, defaults to 200 (10 seconds)
     * @param dirtyTime the amount of time in ticks the kettle should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, IIngredient catalyst, ILiquidStack liquid,IItemStack[] outputs, @Optional("200") int time, @Optional(valueLong = 0L) int dirtyTime) {
        addKettleRecipe(inputs, catalyst, liquid, null, outputs, time, dirtyTime);
    }

    /**
     * Adds kettle recipe with liquid requirement and liquid output
     * @param inputs up to 9 ingredients can be specified
     * @param catalyst catalyst item, not consumed by recipe
     * @param liquidInput input liquid required for recipe
     * @param outputs up to 9 outputs can be specified
     * @param liquidOutput output liquid for recipe
     * @param time cook time in ticks, defaults to 200 (10 seconds)
     * @param dirtyTime the amount of time in ticks the kettle should become soiled for, which defaults to 0
     */
    @ZenMethod
    public static void addKettleRecipe(IIngredient[] inputs, IIngredient catalyst, ILiquidStack liquidInput,
                                       ILiquidStack liquidOutput,IItemStack[] outputs, @Optional("200") int time, @Optional(valueLong = 0L) int dirtyTime) {
        List<Ingredient> iinputs = Arrays.stream(inputs).map(CraftTweakerMC::getIngredient).collect(Collectors.toList());
        Ingredient iCatalyst = new IIngredientWrapper(catalyst);
        FluidStack iFluidInput = CraftTweakerMC.getLiquidStack(liquidInput);
        FluidStack iFluidOutput = CraftTweakerMC.getLiquidStack(liquidOutput);
        List<ItemStack> iOutputs = Arrays.stream(outputs).map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
        CraftTweakerAPI.apply(new AddKettleAction(iinputs, iCatalyst, iFluidInput,iFluidOutput, iOutputs, time, dirtyTime));
    }

    /**
     * Removes kettle recipe by name
     * @param name the name of the recipe specified in tooltip
     */
    @ZenMethod
    public static void removeKettleRecipe(String name) {
        CraftTweakerAPI.apply(new RemoveKettleAction(new ResourceLocation(name)));
    }

    //////////////////////////////////////////////

    /**
     * Adds a block that kitchenware can be placed on
     * @param state The blockstate to add
     * @param heat whether this block can heat kitchenware
     * @param legs whether legs should be placed below the kitchenware (default: false)
     */
    @ZenMethod
    public static void addPlaceable(IBlockState state,boolean heat,@Optional boolean legs) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlockState(state), heat, true,legs));
    }

    /**
     * Adds a block that kitchenware can be placed on
     * @param block The block to add
     * @param heat whether this block can heat kitchenware
     * @param legs whether legs should be placed below the kitchenware (default: false)
     */
    @ZenMethod
    public static void addPlaceable(IBlock block,boolean heat,@Optional boolean legs) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlock(block).getBlockState().getValidStates(), heat, true,legs));
    }

    /**
     * Removes kitchenware placeable
     * @param state The blockstate to remove
     */
    @ZenMethod
    public static void removePlaceable(IBlockState state) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlockState(state), false, false,false));
    }

    /**
     * Removes kitchenware placeable
     * @param block The block to remove
     */
    @ZenMethod
    public static void removePlaceable(IBlock block) {
        CraftTweakerAPI.apply(new BlockStateAction(CraftTweakerMC.getBlock(block).getBlockState().getValidStates(), false, false,false));
    }

    //////////////////////////////////////////////////////////

    private static class AddPotAction implements IAction {
        private final Ingredient input;
        private final FluidStack inputFluid;
        private final ItemStack output;
        private final int time, dirtyTime;

        public AddPotAction(Ingredient input, FluidStack inputFluid, ItemStack output, int time, int dirtyTime) {
            this.input = input;
            this.inputFluid = inputFluid;
            this.output = output;
            this.time = time;
            this.dirtyTime = dirtyTime;
        }

        @Override
        public String describe() {
            return "Adding pot recipe with input " + input;
        }

        @Override
        public void apply() {
            ModRecipes.addPotRecipe(input, inputFluid, output, time, dirtyTime);
        }
    }

    private static class RemovePotAction implements IAction {
        private final Ingredient input;
        private final ItemStack output;

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
                done = ModRecipes.removePotRecipe(output);
            else
                done = ModRecipes.removePotRecipe(input, output);

            if (!done)
                CraftTweakerAPI.logWarning("No pot recipes removed for input " + input + " and output " + output);
        }
    }

    private static class AddCauldronAction implements IAction {
        private final Ingredient input;
        private final FluidStack inputFluid;
        private final ItemStack output;
        private final int time, dirtyTime;

        public AddCauldronAction(Ingredient input, FluidStack inputFluid, ItemStack output, int time, int dirtyTime) {
            this.input = input;
            this.inputFluid = inputFluid;
            this.output = output;
            this.time = time;
            this.dirtyTime = dirtyTime;
        }

        @Override
        public String describe() {
            return "Adding cauldron recipe with input " + input;
        }

        @Override
        public void apply() {
            ModRecipes.addCauldronRecipe(input, inputFluid, output, time, dirtyTime);
        }
    }

    private static class RemoveCauldronAction implements IAction {
        private final Ingredient input;
        private final ItemStack output;

        public RemoveCauldronAction(ItemStack output) {
            this.input = null;
            this.output = output;
        }

        public RemoveCauldronAction(Ingredient input, ItemStack output) {
            this.input = input;
            this.output = output;
        }

        @Override
        public String describe() {
            if (this.input == null)
                return "Removing cauldron recipe with output " + output;
            else
                return "Removing cauldron recipe with input " + input + " and output " + output;
        }

        @Override
        public void apply() {
            boolean done;
            if (this.input == null)
                done = ModRecipes.removeCauldronRecipe(output);
            else
                done = ModRecipes.removeCauldronRecipe(input, output);

            if (!done)
                CraftTweakerAPI.logWarning("No cauldron recipes removed for input " + input + " and output " + output);
        }
    }

    private static class RemoveSmelterAction implements IAction {
        private final Ingredient input;
        private final ItemStack output;

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
                done = ModRecipes.removeSmelterRecipe(output);
            else
                done = ModRecipes.removeSmelterRecipe(input, output);

            if (!done) {
                if (this.input == null)
                    CraftTweakerAPI.logWarning("No smelter recipes removed for output " + output);
                else
                    CraftTweakerAPI.logWarning("No smelter recipes removed for input " + input + " and output " + output);
            }
        }
    }

    private static class AddKettleAction implements IAction {
        private final List<Ingredient> inputs;
        private final Ingredient catalyst;
        private final FluidStack fluidInput;
        private final FluidStack fluidOutput;
        private final List<ItemStack> outputs;
        private final int time, dirtyTime;

        public AddKettleAction(List<Ingredient> inputs, Ingredient catalyst, @Nullable FluidStack fluidInput, @Nullable FluidStack fluidOutput, List<ItemStack> outputs, int time, int dirtyTime) {
            this.inputs = inputs;
            this.catalyst = catalyst;
            this.fluidInput = fluidInput;
            this.fluidOutput = fluidOutput;
            this.outputs = outputs;
            this.time = time;
            this.dirtyTime = dirtyTime;
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
            ModRecipes.addKettleRecipe(inputs, catalyst, fluidInput, fluidOutput, outputs, time, dirtyTime);
        }
    }

    private static class AddWokAction implements IAction {
        private final List<Ingredient> inputs;
        private final FluidStack fluid;
        private final List<ItemStack> output;
        private final int flips, dirtyTime;

        public AddWokAction(List<Ingredient> inputs, FluidStack fluid, List<ItemStack> output, int flips, int dirtyTime) {
            this.inputs = inputs;
            this.fluid = fluid;
            this.output = output;
            this.flips = flips;
            this.dirtyTime = dirtyTime;
        }

        @Override
        public String describe() {
            return "Adding wok recipe with input " + inputs;
        }

        @Override
        public void apply() {
            ModRecipes.addWokRecipe(inputs, fluid, output, flips, dirtyTime);
        }
    }

    private static class RemoveKettleAction implements IAction {
        private final ResourceLocation name;

        public RemoveKettleAction(ResourceLocation name) {
            this.name = name;
        }

        @Override
        public String describe() {
            return "Removing kettle recipe with name " + name;
        }

        @Override
        public void apply() {
            boolean done = ModRecipes.removeKettleRecipeByName(name);

            if (!done)
                CraftTweakerAPI.logWarning("No kettle recipes removed for name " + name);
        }
    }

    private static class BlockStateAction implements IAction {
        private final Collection<net.minecraft.block.state.IBlockState> states;
        private final boolean isHeatSource;
        private final boolean legs;
        private final boolean add;

        public BlockStateAction(net.minecraft.block.state.IBlockState state, boolean isHeatSource, boolean add,boolean legs) {
            this(Sets.newHashSet(state), isHeatSource, add,legs);
        }

        public BlockStateAction(Collection<net.minecraft.block.state.IBlockState> states, boolean isHeatSource, boolean add,boolean legs) {
            this.states = states;
            this.isHeatSource = isHeatSource;
            this.add = add;
            this.legs = legs;
        }

        @Override
        public String describe() {
            return (this.add ? "Adding " : "Removing ") +
                   (this.isHeatSource ? "heat source " : "placeable ") +
                   "with block states " + this.states.stream()
                            .map(net.minecraft.block.state.IBlockState::toString)
                            .collect(Collectors.joining(",")) + " legs:"+legs;
        }

        @Override
        public void apply() {
            if (this.add) {
                    ModRecipes.addPlaceable(states,isHeatSource,legs);
            } else {
                boolean done = ModRecipes.removePlaceables(states);
                if (!done)
                    CraftTweakerAPI.logWarning("No " + 
                            (this.isHeatSource ? "heat sources " : "placeables ") +
                            "were removed for block state " + this.states);
            }
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
            done = ModRecipes.removeWokRecipeByName(name);

            if (!done) {
                CraftTweakerAPI.logWarning("No pot recipes removed for name " + name);
            }
        }
    }

    private static class AddSmelterAction implements IAction {
        private final Ingredient input;
        private final ItemStack output;
        private final int dirtyTime;

        public AddSmelterAction(Ingredient input, ItemStack output, int dirtyTime) {
            this.input = input;
            this.output = output;
            this.dirtyTime = dirtyTime;
        }

        @Override
        public String describe() {
            return "Adding pot recipe with input " + input;
        }

        @Override
        public void apply() {
            ModRecipes.addSmelterRecipe(input, output, dirtyTime);
        }
    }
}
