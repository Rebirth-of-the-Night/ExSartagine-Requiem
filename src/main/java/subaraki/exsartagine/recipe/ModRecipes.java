package subaraki.exsartagine.recipe;

import com.google.common.collect.Lists;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreIngredient;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.Oredict;
import subaraki.exsartagine.block.BlockRange;
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.tileentity.util.BlockInfo;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class ModRecipes {

    private static final Map<IBlockState, BlockInfo> placeable = new HashMap<>();
    protected static final Map<IRecipeType<?>, Map<ResourceLocation,CustomRecipe<?>>> recipes = new HashMap<>();

    public static <I extends IItemHandler,T extends CustomRecipe<I>> void addRecipe(T recipe) {
        ResourceLocation name = new ResourceLocation("crafttweaker","autogenerated_"+i);
        i++;
        addRecipe(name,recipe);
    }
        public static <I extends IItemHandler, R extends CustomRecipe<I>> void addRecipe(ResourceLocation name, R recipe) {
        IRecipeType<R> type = (IRecipeType<R>) recipe.getType();
        Map<ResourceLocation,CustomRecipe<?>> recs = recipes.get(type);

        if (recs == null) {
            recipes.put(type, new HashMap<>());
            recs = recipes.get(type);
        }
        if(recs.put(name,recipe) != null) {
            throw new RuntimeException("duplicate recipe:" + type + name);
        }
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> boolean removeRecipeByName(ResourceLocation name, IRecipeType<R> type) {
        Map<ResourceLocation,R> map = getRecipeMap(type);
        return map.remove(name) != null;
    }

    public static void addPotRecipe(Ingredient input, ItemStack result) {
        addRecipe(result.getItem().getRegistryName(),new PotRecipe(input, result));
    }

    static int i = 0;
    public static void addWokRecipe(List<Ingredient> ingredients, FluidStack fluid, List<ItemStack> stacks, int flips) {
        WokRecipe wokRecipe = new WokRecipe(ingredients,fluid, stacks,flips);
        addRecipe(wokRecipe);
    }

    public static void addSmelterRecipe(Ingredient ingredient, ItemStack itemStack) {
        addRecipe(itemStack.getItem().getRegistryName(),new SmelterRecipe(ingredient, itemStack));
    }

    public static void addBuiltinKettleRecipe(List<Ingredient> ingredients, Ingredient catalyst, @Nullable FluidStack inputFluid,
                                       @Nullable FluidStack outputFluid, List<ItemStack> results, int cookTime) {
        addRecipe(new ResourceLocation(ExSartagine.MODID,results.get(0).getItem().getRegistryName().getPath()),
                new KettleRecipe(ingredients, catalyst, inputFluid,outputFluid, results, cookTime));
    }


    public static void addKettleRecipe(List<Ingredient> ingredients, Ingredient catalyst, @Nullable FluidStack inputFluid,
                                       @Nullable FluidStack outputFluid, List<ItemStack> results, int cookTime) {
        addRecipe(new KettleRecipe(ingredients, catalyst, inputFluid,outputFluid, results, cookTime));
    }

    //note, only the pot and smelter should use this method

    private static final ItemStackHandler DUMMY = new ItemStackHandler();
    public static <T extends IItemHandler,U extends CustomRecipe<T>> boolean hasResult(ItemStack stack, IRecipeType<U> type) {
        DUMMY.setStackInSlot(0,stack);
        return hasResult((T)DUMMY, type);
    }
    
    public static boolean removePotRecipe(ItemStack output) {
        return getRecipes(RecipeTypes.POT).removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePotRecipe(ItemStack input, ItemStack output) {
        return getRecipes(RecipeTypes.POT).removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removePotRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removePotRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }

    public static Collection<WokRecipe> getWokRecipes() {
        return getRecipes(RecipeTypes.WOK);
    }

    public static boolean removeWokRecipeByName(ResourceLocation name) {
        return removeRecipeByName(name,RecipeTypes.WOK);
    }

    public static boolean removeSmelterRecipe(ItemStack output) {
        return getRecipes(RecipeTypes.SMELTER).removeIf(r -> ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removeSmelterRecipe(ItemStack input, ItemStack output) {
        return getRecipes(RecipeTypes.SMELTER).removeIf(r -> r.itemMatch(new ItemStackHandler(NonNullList.from(input))) && ItemStack.areItemStacksEqual(r.getResult(new ItemStackHandler()), output));
    }
    
    public static boolean removeSmelterRecipe(Ingredient input, ItemStack output) {
        boolean changed = false;
        for (ItemStack i : input.getMatchingStacks()) {
            if (removeSmelterRecipe(i, output)) 
                changed = true;
        }
        return changed;
    }

    public static Collection<KettleRecipe> getKettleRecipes() {
        return getRecipes(RecipeTypes.KETTLE);
    }
    
    public static boolean removeKettleRecipeByName(ResourceLocation name) {
        return removeRecipeByName(name,RecipeTypes.KETTLE);
    }
    

    public static <I extends IItemHandler, R extends CustomRecipe<I>> ItemStack getCookingResult(I handler, IRecipeType<R> type) {
        for (CustomRecipe<I> recipe : getRecipes(type)) {
            if (recipe.itemMatch(handler))
                return recipe.getResult(handler);
        }
        return ItemStack.EMPTY;
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> List<ItemStack> getCookingResults(I handler, IRecipeType<R> type) {
        for (CustomRecipe<I> recipe : getRecipes(type)) {
            if (recipe.itemMatch(handler))
                return recipe.getResults(handler);
        }
        return new ArrayList<>();
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> CustomRecipe<I> findRecipe(I handler, IRecipeType<R> type) {
        Collection<R> recipes = getRecipes(type);
        for (CustomRecipe<I> recipe : recipes) {
            if (recipe.itemMatch(handler))
                return recipe;
        }
        return null;
    }

    public static <I extends IItemHandler, F extends IFluidHandler, FR extends CustomFluidRecipe<I,F>>
    FR findFluidRecipe(I handler, F fluidHandler, IRecipeType<FR> type) {
        Collection<FR> recipes = getRecipes(type);
        for (FR recipe : recipes) {
            if (recipe.match(handler,fluidHandler))
                return recipe;
        }
        return null;
    }

    public static <I extends IItemHandler, F extends IFluidHandler> KettleRecipe findKettleRecipe(I handler,F fluidHandler) {
        return findFluidRecipe(handler,fluidHandler,RecipeTypes.KETTLE);
    }

    public static <I extends IItemHandler,T extends CustomRecipe<I>> boolean hasResult(I handler, IRecipeType<T> type) {
        return getRecipes(type).stream().anyMatch(customRecipe -> customRecipe.itemMatch(handler));
    }

    public static void addPlaceable(Block block,boolean hot,boolean legs) {
        addPlaceable(block,iBlockState -> true,hot,legs);
    }

    public static void addPlaceable(Block block, Predicate<IBlockState> predicate,boolean hot,boolean legs) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            if (predicate.test(state)) {
                addPlaceable(state,hot,legs);
            }
        }
    }

    public static void addPlaceable(Collection<IBlockState> states,boolean hot,boolean legs) {
        for (IBlockState state : states) {
            addPlaceable(state,hot,legs);
        }
    }

    public static void addPlaceable(IBlockState state,boolean hot,boolean legs) {
        placeable.put(state,new BlockInfo(hot,legs));
    }

    public static boolean removePlaceable(IBlockState state) {
        return placeable.remove(state) != null;
    }

    public static void removePlaceable(Block block) {
        removePlaceables(block.getBlockState().getValidStates());
    }

    public static boolean removePlaceables(Collection<IBlockState> states) {
        states.forEach(ModRecipes::removePlaceable);
        return true;
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> Collection<R> getRecipes(IRecipeType<R> type) {
        Map<ResourceLocation,R> map = getRecipeMap(type);
        if (map != null) {
            return map.values();
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public static <I extends IItemHandler, R extends CustomRecipe<I>,S extends Map<ResourceLocation,R>> S getRecipeMap(IRecipeType<R> type) {
        return (S) ModRecipes.recipes.get(type);
    }

    public static boolean isHeatSource(IBlockState state) {
        return placeable.getOrDefault(state,BlockInfo.INVALID).hot;
    }

    public static boolean isPlaceable(IBlockState state) {
        return placeable.containsKey(state);
    }

    public static boolean hasLegs(IBlockState state) {
        return placeable.getOrDefault(state,BlockInfo.INVALID).legs;
    }

    public static void init() {
        addPlaceable(Blocks.FURNACE,false,false);
        addPlaceable(Blocks.LIT_FURNACE,true,false);
        addPlaceable(ExSartagineBlocks.range_extended,false,false);
        addPlaceable(ExSartagineBlocks.range_extended_lit,true,false);
        addPlaceable(ExSartagineBlocks.hearth_extended,false,false);
        addPlaceable(ExSartagineBlocks.hearth_extended_lit,true,false);
        addPlaceable(Blocks.LAVA,true,true);

        addPlaceable(ExSartagineBlocks.range,iBlockState -> !iBlockState.getValue(BlockRange.HEATED),false,false);
        addPlaceable(ExSartagineBlocks.range,iBlockState -> iBlockState.getValue(BlockRange.HEATED),true,false);

        addPlaceable(ExSartagineBlocks.hearth,iBlockState -> !iBlockState.getValue(BlockRange.HEATED),false,false);
        addPlaceable(ExSartagineBlocks.hearth,iBlockState -> iBlockState.getValue(BlockRange.HEATED),true,false);

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

        FurnaceRecipes.instance().getSmeltingList().entrySet().stream()
                .filter(entry -> entry.getKey().getItem() instanceof ItemFood)
                .forEach(entry -> {
                    List<Ingredient> i = new ArrayList<>();
                    i.add(Ingredient.fromStacks(entry.getKey()));
                    List<ItemStack> o = new ArrayList<>();
                    o.add(entry.getValue());
                    WokRecipe wokRecipe =  new WokRecipe(i,null,o, 0);
                    ResourceLocation name = entry.getKey().getItem().getRegistryName();
                    String path = name.getPath() + (entry.getKey().getItem().getHasSubtypes() ? entry.getKey().getMetadata() : "");
                    addRecipe(new ResourceLocation(name.getNamespace(),path),wokRecipe);
                });

        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.dry_noodles),Ingredient.fromItem(Items.CHICKEN)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.noodles_chicken_cooked)),200);


        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.dry_noodles),Ingredient.fromItems(Items.BEEF,Items.PORKCHOP)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.noodles_meat_cooked)),200);


        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.dry_noodles),
                        Ingredient.fromStacks(new ItemStack(Items.FISH,1,0),new ItemStack(Items.FISH,1,1))
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.noodles_fish_cooked)),200);


        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.dry_noodles),Ingredient.fromItem(Items.CARROT)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.noodles_veggie_cooked)),200);


        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.spaghetti_raw)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.spaghetti_cooked)),200);

        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.spaghetti_raw),Ingredient.fromItem(Items.BEETROOT),
                Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.spaghetti_sauced)),200);

        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.spaghetti_raw),Ingredient.fromItem(Items.BEETROOT),
                        Ingredient.fromItems(Items.BEEF,Items.PORKCHOP,Items.MUTTON)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.spaghetti_bolognaise)),200);

        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.spaghetti_raw),Ingredient.fromItem(Items.BEETROOT),new OreIngredient(Oredict.CHEESE)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.spaghetti_cheese)),200);

        addBuiltinKettleRecipe(Lists.newArrayList(Ingredient.fromItem(ExSartagineItems.spaghetti_raw),Ingredient.fromItem(Items.BEETROOT), Ingredient.fromItem(Items.CARROT)
                ,Ingredient.fromItem(Items.BOWL)),null,new FluidStack(FluidRegistry.WATER,100),
                null,Lists.newArrayList(new ItemStack(ExSartagineItems.spaghetti_veggie)),200);


        if (ExSartagine.DEBUG) {
            ExSartagine.DebugStuff.run();
        }
    }

    public static <I extends IItemHandler, R extends CustomRecipe<I>> NonNullList<ItemStack> getRemainingItems(I craftMatrix, World worldIn, IRecipeType<R> type) {

        Collection<R> recipes = getRecipes(type);

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
