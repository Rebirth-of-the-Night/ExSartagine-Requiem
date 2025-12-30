package subaraki.exsartagine.init;

import com.google.common.collect.Lists;
import subaraki.exsartagine.recipe.*;

import java.util.List;

public class RecipeTypes {

    public static final IRecipeType<WokRecipe> WOK = IRecipeType.create("wok");
    public static final IRecipeType<KettleRecipe> KETTLE = IRecipeType.create("kettle");
    public static final IRecipeType<SmelterRecipe> SMELTER = IRecipeType.create("smelter");
    public static final IRecipeType<PotRecipe> POT = IRecipeType.create("pot");
    public static final IRecipeType<CauldronRecipe> CAULDRON = IRecipeType.inherit("cauldron", POT);
    public static final IRecipeType<CooktopRecipe> COOKTOP = IRecipeType.create("cooktop");

    public static final List<IRecipeType<?>> TYPES = Lists.newArrayList(WOK,KETTLE,SMELTER,POT,CAULDRON);

}
