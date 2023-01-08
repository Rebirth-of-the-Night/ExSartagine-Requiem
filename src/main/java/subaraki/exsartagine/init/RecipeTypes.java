package subaraki.exsartagine.init;

import com.google.common.collect.Lists;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import subaraki.exsartagine.recipe.*;

import java.util.List;

public class RecipeTypes {

    public static final IRecipeType<WokRecipe> WOK = IRecipeType.create("wok");
    public static final IRecipeType<KettleRecipe> KETTLE = IRecipeType.create("kettle");
    public static final IRecipeType<SmelterRecipe> SMELTER = IRecipeType.create("smelter");
    public static final IRecipeType<PotRecipe> POT = IRecipeType.create("pot");

    public static final List<IRecipeType<?>> TYPES = Lists.newArrayList(WOK,KETTLE,SMELTER,POT);

}
