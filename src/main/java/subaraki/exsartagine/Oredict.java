package subaraki.exsartagine;

import net.minecraftforge.oredict.OreDictionary;
import subaraki.exsartagine.init.ExSartagineItems;

public class Oredict {


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

        OreDictionary.registerOre("itemNoodles", ExSartagineItems.dry_strings);
    }
}
