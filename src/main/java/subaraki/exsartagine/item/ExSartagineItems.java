package subaraki.exsartagine.item;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import subaraki.exsartagine.block.ExSartagineBlock;

import static subaraki.exsartagine.util.Reference.MODID;

public class ExSartagineItems {

	public static Item pan;
	public static Item smelter;
	public static Item pot;
	public static Item range;

	public static ItemFood boiled_egg;
	public static ItemFood boiled_beans;
	public static ItemFood boiled_potato;

	public static ItemFood pizza_plain;

	public static ItemFood pizza_meat;
	public static ItemFood pizza_chicken;
	public static ItemFood pizza_fish;
	public static ItemFood pizza_sweet;

	public static ItemFood pizza_meat_raw;
	public static ItemFood pizza_chicken_raw;
	public static ItemFood pizza_fish_raw;
	public static ItemFood pizza_sweet_raw;

	public static ItemFood bread_fine;
	public static ItemFood bread_meat;
	public static ItemFood bread_veggie;

	public static ItemFood bread_meat_raw;
	public static ItemFood bread_veggie_raw;

	public static Item dry_strings;

	public static Item spaghetti_raw;
	public static ItemNoodle spaghetti_cooked;
	public static ItemNoodle spaghetti_sauced;
	public static ItemNoodle spaghetti_bolognaise;
	public static ItemNoodle spaghetti_cheese;
	public static ItemNoodle spaghetti_veggie;

	public static Item noodles_chicken;
	public static Item noodles_fish;
	public static Item noodles_meat;
	public static Item noodles_veggie;
	public static ItemNoodle noodles_chicken_cooked;
	public static ItemNoodle noodles_fish_cooked;
	public static ItemNoodle noodles_meat_cooked;
	public static ItemNoodle noodles_veggie_cooked ;

	public static Item pizza_dough;
	public static Item bread_dough;

	public static Item dough;
	public static Item salt;
	public static Item yeast;
	public static Item curd;
	public static Item flour;

	public static CreativeTabs foods = new CreativeTabs("exsartaginefoods") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(pizza_plain);
		}
	};

	public static CreativeTabs pots = new CreativeTabs("potsnpans") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(range);
		}
	};
	public static void load(IForgeRegistry<Item> registry) {
		pan = new ItemBlock(ExSartagineBlock.pan).setRegistryName(ExSartagineBlock.pan.getRegistryName()).setCreativeTab(pots);
		smelter = new ItemBlock(ExSartagineBlock.smelter).setRegistryName(ExSartagineBlock.smelter.getRegistryName()).setCreativeTab(pots);
		pot = new ItemBlock(ExSartagineBlock.pot).setRegistryName(ExSartagineBlock.pot.getRegistryName()).setCreativeTab(pots);
		range = new ItemBlock(ExSartagineBlock.range).setRegistryName(ExSartagineBlock.range.getRegistryName()).setCreativeTab(pots);

		boiled_egg = (ItemFood) new ItemFood(4, 0.5f, false).setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".egg.boiled").setRegistryName("egg.boiled");
		boiled_beans = (ItemFood) new ItemFood(3, 0.2f,false).setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".beans.boiled").setRegistryName("beans.boiled");
		boiled_potato = (ItemFood) new ItemFood(6, 0.5f,false).setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".potato.boiled").setRegistryName("potato.boiled");

		flour = new Item().setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".flour").setRegistryName("flour");
		salt = new Item().setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".salt").setRegistryName("salt");
		yeast = new Item().setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".yeast").setRegistryName("yeast");
		curd = new Item(){
			public void addInformation(net.minecraft.item.ItemStack stack, net.minecraft.entity.player.EntityPlayer playerIn, java.util.List<String> tooltip, boolean advanced) {
				String text = ChatFormatting.ITALIC + "Simple Cheese";

				if(!tooltip.contains(text))
					tooltip.add(text);
			};
		}.setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".curd").setRegistryName("curd").setCreativeTab(foods);

		dough = new Item().setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".dough").setRegistryName("dough").setCreativeTab(foods);
		bread_dough = new Item().setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".doughBread").setRegistryName("doughBread").setCreativeTab(foods);

		pizza_dough = new Item().setCreativeTab(CreativeTabs.FOOD).setTranslationKey(MODID+".doughPizza").setRegistryName("doughPizza").setCreativeTab(foods);
		pizza_plain = (ItemFood)new ItemFood(6, 0.6f, false).setTranslationKey(MODID+".pizzaPlain").setRegistryName("pizzaPlain").setCreativeTab(foods);
		pizza_meat = (ItemFood)new ItemFood(12, 0.9f, false).setTranslationKey(MODID+".pizzaMeat").setRegistryName("pizzaMeat").setCreativeTab(foods);
		pizza_chicken = (ItemFood)new ItemFood(10, 0.9f, false).setTranslationKey(MODID+".pizzaChicken").setRegistryName("pizzaChicken").setCreativeTab(foods);
		pizza_sweet = (ItemFood)new ItemFood(10, 0.7f, false).setTranslationKey(MODID+".pizzaSweet").setRegistryName("pizzaSweet").setCreativeTab(foods);
		pizza_fish = (ItemFood)new ItemFood(9, 1.0f, false).setTranslationKey(MODID+".pizzaFish").setRegistryName("pizzaFish").setCreativeTab(foods);

		pizza_meat_raw = (ItemFood)new ItemFood(4, 0.3f, false).setTranslationKey(MODID+".pizzaMeatRaw").setRegistryName("pizzaMeatRaw").setCreativeTab(foods);
		pizza_chicken_raw = (ItemFood)new ItemFood(3, 0.3f, false).setTranslationKey(MODID+".pizzaChickenRaw").setRegistryName("pizzaChickenRaw").setCreativeTab(foods);
		pizza_sweet_raw = (ItemFood)new ItemFood(3, 0.2f, false).setTranslationKey(MODID+".pizzaSweetRaw").setRegistryName("pizzaSweetRaw").setCreativeTab(foods);
		pizza_fish_raw = (ItemFood)new ItemFood(2, 0.4f, false).setTranslationKey(MODID+".pizzaFishRaw").setRegistryName("pizzaFishRaw").setCreativeTab(foods);

		bread_fine = (ItemFood)new ItemFood(5, 0.8f, false).setTranslationKey(MODID+".breadFine").setRegistryName("breadFine").setCreativeTab(foods);
		bread_meat = (ItemFood)new ItemFood(4, 0.45f, false).setTranslationKey(MODID+".breadMeat").setRegistryName("breadMeat").setCreativeTab(foods);
		bread_veggie = (ItemFood)new ItemFood(8, 0.9f, false).setTranslationKey(MODID+".breadVeggie").setRegistryName("breadVeggie").setCreativeTab(foods);

		bread_meat_raw = (ItemFood)new ItemFood(2, 0.3f, false).setTranslationKey(MODID+".breadMeatRaw").setRegistryName("breadMeatRaw").setCreativeTab(foods);
		bread_veggie_raw = (ItemFood)new ItemFood(3, 0.3f, false).setTranslationKey(MODID+".breadVeggieRaw").setRegistryName("breadVeggieRaw").setCreativeTab(foods);

		dry_strings = new Item().setTranslationKey(MODID+".dry_noodles").setRegistryName("dry_noodles").setCreativeTab(foods);

		spaghetti_raw = new Item().setTranslationKey(MODID+".spaghetti_raw").setRegistryName("spaghetti_raw").setCreativeTab(foods);
		spaghetti_cooked = (ItemNoodle)new ItemNoodle(3, 0.7f, false).setTranslationKey(MODID+".spaghetti_cooked").setRegistryName("spaghetti_cooked").setCreativeTab(foods);
		spaghetti_sauced = (ItemNoodle)new ItemNoodle(5, 0.5f, false).setTranslationKey(MODID+".spaghetti_sauced").setRegistryName("spaghetti_sauced").setCreativeTab(foods);
		spaghetti_bolognaise = (ItemNoodle)new ItemNoodle(5, 0.7f, false).setTranslationKey(MODID+".spaghetti_bolognaise").setRegistryName("spaghetti_bolognaise").setCreativeTab(foods);
		spaghetti_cheese = (ItemNoodle)new ItemNoodle(5, 0.8f, false).setTranslationKey(MODID+".spaghetti_cheese").setRegistryName("spaghetti_cheese").setCreativeTab(foods);
		spaghetti_veggie = (ItemNoodle)new ItemNoodle(8, 0.5f, false).setTranslationKey(MODID+".spaghetti_veggie").setRegistryName("spaghetti_veggie").setCreativeTab(foods);

		noodles_chicken_cooked = (ItemNoodle)new ItemNoodle(6, 0.7f, false).setTranslationKey(MODID+".noodles_chicken_cooked").setRegistryName("noodles_chicken_cooked").setCreativeTab(foods);
		noodles_fish_cooked = (ItemNoodle)new ItemNoodle(6, 0.7f, false).setTranslationKey(MODID+".noodles_fish_cooked").setRegistryName("noodles_fish_cooked").setCreativeTab(foods);
		noodles_meat_cooked = (ItemNoodle)new ItemNoodle(5, 0.8f, false).setTranslationKey(MODID+".noodles_meat_cooked").setRegistryName("noodles_meat_cooked").setCreativeTab(foods);
		noodles_veggie_cooked = (ItemNoodle)new ItemNoodle(7, 0.7f, false).setTranslationKey(MODID+".noodles_veggie_cooked").setRegistryName("noodles_veggie_cooked").setCreativeTab(foods);

		noodles_chicken = new Item().setTranslationKey(MODID+".noodles_chicken").setRegistryName("noodles_chicken").setCreativeTab(foods);
		noodles_fish = new Item().setTranslationKey(MODID+".noodles_fish").setRegistryName("noodles_fish").setCreativeTab(foods);
		noodles_meat= new Item().setTranslationKey(MODID+".noodles_meat").setRegistryName("noodles_meat").setCreativeTab(foods);
		noodles_veggie = new Item().setTranslationKey(MODID+".noodles_veggie").setRegistryName("noodles_veggie").setCreativeTab(foods);

		register(registry);

		addToOreDict();
	}

	private static void addToOreDict() {
		OreDictionary.registerOre("egg", boiled_egg);
		OreDictionary.registerOre("ingredientEgg", boiled_egg);
		OreDictionary.registerOre("cropVegetable", boiled_potato);
		OreDictionary.registerOre("cropVegetable", boiled_beans);

		OreDictionary.registerOre("foodFlour", flour);
		OreDictionary.registerOre("dustFlour", flour);
		OreDictionary.registerOre("dustSalt", salt);
		OreDictionary.registerOre("itemCheese", curd);
		OreDictionary.registerOre("ingredientCheese", curd);
		OreDictionary.registerOre("itemYeast", yeast);
		OreDictionary.registerOre("foodDough", dough);
		OreDictionary.registerOre("foodDoughFlat", pizza_dough);
		OreDictionary.registerOre("foodDoughBread", bread_dough); //food registry for pan slot

		OreDictionary.registerOre("itemNoodles", dry_strings);
	}

	private static void register(IForgeRegistry<Item> registry) {
		registry.register(pan);
		registry.register(smelter);
		registry.register(pot);
		registry.register(range);

		registry.register(boiled_egg);
		registry.register(boiled_beans);
		registry.register(boiled_potato);

		registry.register(salt);
		registry.register(flour);
		registry.register(curd);
		registry.register(yeast);
		registry.register(dough);

		registry.register(pizza_plain);
		registry.register(pizza_chicken);
		registry.register(pizza_chicken_raw);
		registry.register(pizza_fish);
		registry.register(pizza_fish_raw);
		registry.register(pizza_meat);
		registry.register(pizza_meat_raw);
		registry.register(pizza_sweet);
		registry.register(pizza_sweet_raw);
		registry.register(pizza_dough);

		registry.register(bread_dough);
		registry.register(bread_fine);
		registry.register(bread_meat);
		registry.register(bread_meat_raw);
		registry.register(bread_veggie);
		registry.register(bread_veggie_raw);

		registry.register(dry_strings);

		registry.register(noodles_chicken);
		registry.register(noodles_meat);
		registry.register(noodles_fish);
		registry.register(noodles_veggie);

		registry.register(noodles_chicken_cooked);
		registry.register(noodles_meat_cooked);
		registry.register(noodles_fish_cooked);
		registry.register(noodles_veggie_cooked);

		registry.register(spaghetti_raw);
		registry.register(spaghetti_cooked);
		registry.register(spaghetti_sauced);
		registry.register(spaghetti_bolognaise);
		registry.register(spaghetti_cheese);
		registry.register(spaghetti_veggie);
	}

	public static void registerRenders(){
		registerRender(pan, "pan", MODID);
		registerRender(smelter, "smelter", MODID);
		registerRender(pot, "pot", MODID);
		registerRender(range, "range", MODID);

		registerRender(boiled_egg, "egg", MODID);
		registerRender(boiled_beans, "beans", MODID);
		registerRender(boiled_potato, "potato", MODID);

		registerRender(pizza_plain, "pizza_plain", MODID);

		registerRender(pizza_chicken, "pizza_chicken_cooked", MODID);
		registerRender(pizza_sweet, "pizza_sweet_cooked", MODID);
		registerRender(pizza_meat, "pizza_meat_cooked", MODID);
		registerRender(pizza_fish, "pizza_fish_cooked", MODID);

		registerRender(pizza_chicken_raw, "pizza_chicken_raw", MODID);
		registerRender(pizza_sweet_raw, "pizza_sweet_raw", MODID);
		registerRender(pizza_meat_raw, "pizza_meat_raw", MODID);
		registerRender(pizza_fish_raw, "pizza_fish_raw", MODID);

		registerRender(bread_fine, "fine_bread", MODID);
		registerRender(bread_meat, "meat_minibread", MODID);
		registerRender(bread_veggie, "veggie_bread", MODID);

		registerRender(bread_meat_raw, "meat_minibread_raw", MODID);
		registerRender(bread_veggie_raw, "veggie_bread_raw", MODID);

		registerRender(pizza_dough, "dough", MODID);
		registerRender(bread_dough, "dough", MODID);
		registerRender(dough, "dough", MODID);
		registerRender(salt, "salt", MODID);
		registerRender(flour, "flour", MODID);
		registerRender(yeast, "yeast", MODID);
		registerRender(curd, "curd", MODID);

		registerRender(dry_strings, "dry_strings", MODID);

		registerRender(noodles_chicken, "noodles_chicken", MODID);
		registerRender(noodles_fish, "noodles_fish", MODID);
		registerRender(noodles_meat, "noodles_meat", MODID);
		registerRender(noodles_veggie, "noodles_veggie", MODID);

		registerRender(noodles_chicken_cooked, "noodles_chicken_cooked", MODID);
		registerRender(noodles_fish_cooked, "noodles_fish_cooked", MODID);
		registerRender(noodles_meat_cooked, "noodles_meat_cooked", MODID);
		registerRender(noodles_veggie_cooked, "noodles_veggie_cooked", MODID);

		registerRender(spaghetti_raw, "spaghetti_raw", MODID);
		registerRender(spaghetti_cooked, "spaghetti_cooked", MODID);
		registerRender(spaghetti_sauced, "spaghetti_sauced", MODID);
		registerRender(spaghetti_bolognaise, "spaghetti_bolognaise", MODID);
		registerRender(spaghetti_cheese, "spaghetti_cheese", MODID);
		registerRender(spaghetti_veggie, "spaghetti_veggie", MODID);
	}

	public static void registerRender(Item item, String name, String modid) {
		ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation(new ResourceLocation(modid,name),"inventory"));
	}
}
