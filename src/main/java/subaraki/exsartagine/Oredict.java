package subaraki.exsartagine;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import subaraki.exsartagine.init.ExSartagineItems;

import java.util.List;

public class Oredict {

    public static final String SPATULA = "ore:spatula";
    public static final String IGNITER = "ore:igniter";
    public static final String SELF_IGNITER = ExSartagine.MODID+":self_igniter_upgrade";

    public static void addToOreDict() {
        OreDictionary.registerOre("egg", ExSartagineItems.boiled_egg);
        OreDictionary.registerOre("ingredientEgg", ExSartagineItems.boiled_egg);
        OreDictionary.registerOre("cropVegetable", ExSartagineItems.boiled_potato);
        OreDictionary.registerOre("cropVegetable", ExSartagineItems.boiled_beans);

        OreDictionary.registerOre("foodFlour", ExSartagineItems.flour);
        OreDictionary.registerOre("dustFlour", ExSartagineItems.flour);
        OreDictionary.registerOre("dustSalt", ExSartagineItems.salt);
        OreDictionary.registerOre("itemCheese", ExSartagineItems.curd);
        OreDictionary.registerOre("ingredientCheese", ExSartagineItems.curd);
        OreDictionary.registerOre("itemYeast", ExSartagineItems.yeast);
        OreDictionary.registerOre("foodDough", ExSartagineItems.dough);
        OreDictionary.registerOre("foodDoughFlat", ExSartagineItems.pizza_dough);
        OreDictionary.registerOre("foodDoughBread", ExSartagineItems.bread_dough); //food registry for pan slot

        OreDictionary.registerOre("itemNoodles", ExSartagineItems.dry_noodles);

        OreDictionary.registerOre(SPATULA, new ItemStack(Items.WOODEN_SHOVEL,1,OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre(IGNITER,new ItemStack(Items.FLINT_AND_STEEL,1,OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre(SELF_IGNITER,new ItemStack(Items.BLAZE_POWDER));
    }

    public static boolean checkMatch(String name, ItemStack stack) {
        List<ItemStack> ores = OreDictionary.getOres(name);

        for (ItemStack stack1 : ores) {
            if (OreDictionary.itemMatches(stack1,stack,false)) {
                return true;
            }
        }
        return false;
    }
}
