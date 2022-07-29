package subaraki.exsartagine.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.block.ExSartagineBlocks;
import subaraki.exsartagine.item.ExSartagineItems;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Recipes {

    private static final Set<IBlockState> heatSources = new HashSet<>();

    private static final Set<IBlockState> placeable = new HashSet<>();
    protected static final Map<String, List<CustomRecipe<?>>> recipes = new HashMap<>();

    static {
        addType("pan");
        addType("pot");
        addType("smelter");
        addType("kettle");
    }


    public static void addType(String type) {
        recipes.put(type, new ArrayList<>());
    }

    public static void addPotRecipe(Ingredient input, ItemStack result) {
        getRecipes("pot").add(new PotRecipe(input, result));
    }

    public static void addPanRecipe(Ingredient ingredient, ItemStack itemStack) {
        getRecipes("pan").add(new FryingPanRecipe(ingredient, itemStack));
    }

    public static void addSmelterRecipe(Ingredient ingredient, ItemStack itemStack) {
        getRecipes("smelter").add(new SmelterRecipe(ingredient, itemStack));
    }

    public static void addKettleRecipe(List<Ingredient> ingredients, Ingredient catalyst, @Nullable FluidStack inputFluid,
                                       @Nullable FluidStack outputFluid, List<ItemStack> results, int cookTime) {
        getRecipes("kettle").add(new KettleRecipe(ingredients, catalyst, inputFluid,outputFluid, results, cookTime));
    }

    public static boolean hasResult(ItemStack stack, String type) {
        return hasResult(new ItemStackHandler(NonNullList.from(ItemStack.EMPTY, stack)), type);
    }
    
    public static boolean removePotRecipe(ItemStack output) {
        return getRecipes("pot").removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePotRecipe(ItemStack input, ItemStack output) {
        return getRecipes("pot").removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePotRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removePotRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }
    
    public static boolean removePanRecipe(ItemStack output) {
        return getRecipes("pan").removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePanRecipe(ItemStack input, ItemStack output) {
        return getRecipes("pan").removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePanRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removePanRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }
    
    public static boolean removeSmelterRecipe(ItemStack output) {
        return getRecipes("smelter").removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removeSmelterRecipe(ItemStack input, ItemStack output) {
        return getRecipes("smelter").removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removeSmelterRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removeSmelterRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }
    
    public static boolean removeKettleRecipe(ItemStack output) {
        return getRecipes("kettle").removeIf(r -> r.getResults(new ItemStackHandler()).contains(output));
    }
    
    public static boolean removeKettleRecipe(List<ItemStack> outputs) {
        return getRecipes("kettle").removeIf(r -> r.getResults(new ItemStackHandler()).containsAll(outputs));
    }
    
    public static boolean removeKettleRecipe(List<Ingredient> inputs, Ingredient catalyst, FluidStack fluid, List<ItemStack> outputs, int cookTime) {
        return getRecipes("kettle").removeIf(r -> r.getResults(new ItemStackHandler()).containsAll(outputs)
                                               && r.getResults(new ItemStackHandler()).size() == outputs.size()
                                               && r.getIngredients().containsAll(inputs)
                                               && r.getIngredients().size() == inputs.size()
                                               && (((KettleRecipe)r).getCatalyst().equals(catalyst) || catalyst == null)
                                               && ((((KettleRecipe)r).getInputFluid() == null && fluid == null) || (((KettleRecipe)r).getInputFluid() != null && ((KettleRecipe)r).getInputFluid().containsFluid(fluid)))
                                               && (cookTime == -1 || ((KettleRecipe)r).getCookTime() == cookTime));
    }
    

    public static <I extends IItemHandler> ItemStack getCookingResult(I handler, String type) {
        for (CustomRecipe<IItemHandler> recipe : getRecipes(type)) {
            if (recipe.itemMatch(handler))
                return recipe.getResult(handler);
        }
        return ItemStack.EMPTY;
    }

    public static <I extends IItemHandler> List<ItemStack> getCookingResults(I handler, String type) {
        for (CustomRecipe<IItemHandler> recipe : getRecipes(type)) {
            if (recipe.itemMatch(handler))
                return recipe.getResults(handler);
        }
        return new ArrayList<>();
    }

    public static <I extends IItemHandler> CustomRecipe<I> findRecipe(I handler, String type) {
        List<CustomRecipe<I>> recipes = getRecipes(type);
        for (CustomRecipe<I> recipe : recipes) {
            if (recipe.itemMatch(handler))
                return recipe;
        }
        return null;
    }

    public static <I extends IItemHandler, F extends IFluidHandler> KettleRecipe findKettleRecipe(I handler,F fluidHandler) {
        List<KettleRecipe> recipes = getRecipes("kettle");
        for (KettleRecipe recipe : recipes) {
            if (recipe.itemMatch(handler) && recipe.fluidMatch(fluidHandler))
                return recipe;
        }
        return null;
    }



    public static <I extends IItemHandler> boolean hasResult(I handler, String type) {
        return getRecipes(type).stream().anyMatch(customRecipe -> customRecipe.itemMatch(handler));
    }

    public static void addHeatSource(IBlockState state) {
        heatSources.add(state);
        addPlaceable(state);
    }

    public static void addHeatSource(Block block) {
        addHeatSources(block.getBlockState().getValidStates());
    }

    public static void addHeatSources(Collection<IBlockState> states) {
        heatSources.addAll(states);
        addPlaceables(states);
    }

    public static void addPlaceable(IBlockState state) {
        placeable.add(state);
    }

    public static void addPlaceable(Block block) {
        addPlaceables(block.getBlockState().getValidStates());
    }

    public static void addPlaceables(Collection<IBlockState> states) {
        placeable.addAll(states);
    }

    public static boolean removeHeatSource(IBlockState state) {
        return heatSources.removeIf(b -> b == state);
    }

    public static void removeHeatSource(Block block) {
        removeHeatSources(block.getBlockState().getValidStates());
    }

    public static boolean removeHeatSources(Collection<IBlockState> states) {
        return heatSources.removeAll(states);
    }

    public static boolean removePlaceable(IBlockState state) {
        return placeable.removeIf(b -> b == state);
    }

    public static void removePlaceable(Block block) {
        removePlaceables(block.getBlockState().getValidStates());
    }

    public static boolean removePlaceables(Collection<IBlockState> states) {
        return placeable.removeAll(states);
    }

    @SuppressWarnings("unchecked")
    public static <I extends IItemHandler, R extends CustomRecipe<I>> List<R> getRecipes(String type) {
        return (List<R>) Recipes.recipes.get(type);
    }

    public static boolean isHeatSource(IBlockState state) {
        return heatSources.contains(state);
    }

    public static boolean isPlaceable(IBlockState state) {
        return placeable.contains(state);
    }

    public static void init() {
        addPlaceable(Blocks.FURNACE);
        addHeatSource(Blocks.LIT_FURNACE);
        addPlaceable(ExSartagineBlocks.range_extension);
        addHeatSource(ExSartagineBlocks.range_extension_lit);
        addHeatSource(Blocks.LAVA);

        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_chicken_raw, new ItemStack(ExSartagineItems.pizza_chicken), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_meat_raw, new ItemStack(ExSartagineItems.pizza_meat), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_sweet_raw, new ItemStack(ExSartagineItems.pizza_sweet), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.pizza_fish_raw, new ItemStack(ExSartagineItems.pizza_fish), 0.6f);

        FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_dough, new ItemStack(ExSartagineItems.bread_fine), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_meat_raw, new ItemStack(ExSartagineItems.bread_meat), 0.6f);
        FurnaceRecipes.instance().addSmelting(ExSartagineItems.bread_veggie_raw, new ItemStack(ExSartagineItems.bread_veggie), 0.6f);

        addPotRecipe(Ingredient.fromItem(Items.EGG), new ItemStack(ExSartagineItems.boiled_egg, 1));
        addPotRecipe(Ingredient.fromItem(Items.BEETROOT_SEEDS), new ItemStack(ExSartagineItems.boiled_beans, 1));
        addPotRecipe(Ingredient.fromItem(Items.POTATO), new ItemStack(ExSartagineItems.boiled_potato, 1));

        addPotRecipe(Ingredient.fromItem(Item.getItemFromBlock(Blocks.STONE)), new ItemStack(ExSartagineItems.salt, 1));

        addPotRecipe(Ingredient.fromItem(ExSartagineItems.spaghetti_raw), new ItemStack(ExSartagineItems.spaghetti_cooked));

        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_chicken), new ItemStack(ExSartagineItems.noodles_chicken_cooked));
        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_fish), new ItemStack(ExSartagineItems.noodles_fish_cooked));
        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_meat), new ItemStack(ExSartagineItems.noodles_meat_cooked));
        addPotRecipe(Ingredient.fromItem(ExSartagineItems.noodles_veggie), new ItemStack(ExSartagineItems.noodles_veggie_cooked));

        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.IRON_ORE)), new ItemStack(Items.IRON_INGOT));
        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.GOLD_ORE)), new ItemStack(Items.GOLD_INGOT));
        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Items.CLAY_BALL)), new ItemStack(Items.BRICK));
        addSmelterRecipe(Ingredient.fromStacks(new ItemStack(Blocks.NETHERRACK)), new ItemStack(Items.NETHERBRICK));

        //testRecipes();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void postInit() {
        List<CustomRecipe<?>> defaultPanRecipes = FurnaceRecipes.instance().getSmeltingList().entrySet().stream()
                .filter(entry -> entry.getKey().getItem() instanceof ItemFood)
                .map(entry -> new FryingPanRecipe(Ingredient.fromStacks(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        defaultPanRecipes.forEach(recipe -> getRecipes("pan").add((CustomRecipe<IItemHandler>) recipe));

        List kettleRecipes = recipes.get("kettle");
        Collections.sort(kettleRecipes);
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> NonNullList<ItemStack> getRemainingItems(I craftMatrix, World worldIn, String type) {

        List<R> recipes = getRecipes(type);

        for (R irecipe : recipes) {
            if (irecipe.itemMatch(craftMatrix)) {
                return irecipe.getRemainingItems(craftMatrix);
            }
        }

        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(9, ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, craftMatrix.getStackInSlot(i));
        }

        return nonnulllist;
    }


}
