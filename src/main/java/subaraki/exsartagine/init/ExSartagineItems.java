package subaraki.exsartagine.init;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import subaraki.exsartagine.item.ItemNoodle;

import static subaraki.exsartagine.ExSartagine.MODID;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExSartagineItems {

	static List<Item> items;

	public static Item wok;
	public static Item smelter;
	public static Item pot;
	public static Item cauldron;
	public static Item range;
	public static Item range_extended;
	public static Item hearth;
	public static Item hearth_extended;

	public static Item kettle;

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

	public static Item dry_noodles;

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
		wok = new ItemBlock(ExSartagineBlocks.wok).setRegistryName(ExSartagineBlocks.wok.getRegistryName()).setCreativeTab(pots);
		smelter = new ItemBlock(ExSartagineBlocks.smelter).setRegistryName(ExSartagineBlocks.smelter.getRegistryName()).setCreativeTab(pots);
		pot = new ItemBlock(ExSartagineBlocks.pot).setRegistryName(ExSartagineBlocks.pot.getRegistryName()).setCreativeTab(pots);
		if (ExSartagineBlocks.cauldron != null) {
			cauldron = new ItemBlock(ExSartagineBlocks.cauldron).setRegistryName(ExSartagineBlocks.cauldron.getRegistryName()).setCreativeTab(pots);
		}
		range = new ItemBlock(ExSartagineBlocks.range).setRegistryName(ExSartagineBlocks.range.getRegistryName()).setCreativeTab(pots);
		range_extended = new ItemBlock(ExSartagineBlocks.range_extended).setRegistryName(ExSartagineBlocks.range_extended.getRegistryName()).setCreativeTab(pots);

		hearth = new ItemBlock(ExSartagineBlocks.hearth).setRegistryName(ExSartagineBlocks.hearth.getRegistryName()).setCreativeTab(pots);
		hearth_extended = new ItemBlock(ExSartagineBlocks.hearth_extended).setRegistryName(ExSartagineBlocks.hearth_extended.getRegistryName()).setCreativeTab(pots);

		kettle = new ItemBlock(ExSartagineBlocks.kettle).setRegistryName(ExSartagineBlocks.kettle.getRegistryName()).setCreativeTab(pots);


		boiled_egg = (ItemFood) new ItemFood(4, 0.5f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("boiled_egg");
		boiled_beans = (ItemFood) new ItemFood(20, 10.0f,false).setRegistryName("boiled_beans");
		boiled_potato = (ItemFood) new ItemFood(6, 0.5f,false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("boiled_potato");

		flour = new Item().setCreativeTab(CreativeTabs.FOOD).setRegistryName("flour");
		salt = new Item().setCreativeTab(CreativeTabs.FOOD).setRegistryName("salt");
		yeast = new Item().setCreativeTab(CreativeTabs.FOOD).setRegistryName("yeast");
		curd = new Item(){
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, World world, List<String> tooltip, 
					net.minecraft.client.util.ITooltipFlag flag) {
				String text = ChatFormatting.ITALIC + "Simple Cheese";
					tooltip.add(text);
			};
		}.setCreativeTab(CreativeTabs.FOOD).setRegistryName("curd").setCreativeTab(foods);

		dough = new Item().setCreativeTab(CreativeTabs.FOOD).setRegistryName("dough").setCreativeTab(foods);
		bread_dough = new Item().setCreativeTab(CreativeTabs.FOOD).setRegistryName("bread_dough").setCreativeTab(foods);

		pizza_dough = new Item().setCreativeTab(CreativeTabs.FOOD).setRegistryName("pizza_dough").setCreativeTab(foods);
		pizza_plain = (ItemFood)new ItemFood(6, 0.6f, false).setRegistryName("pizza_plain").setCreativeTab(foods);
		pizza_meat = (ItemFood)new ItemFood(12, 0.9f, false).setRegistryName("pizza_meat").setCreativeTab(foods);
		pizza_chicken = (ItemFood)new ItemFood(10, 0.9f, false).setRegistryName("pizza_chicken").setCreativeTab(foods);
		pizza_sweet = (ItemFood)new ItemFood(10, 0.7f, false).setRegistryName("pizza_sweet").setCreativeTab(foods);
		pizza_fish = (ItemFood)new ItemFood(9, 1.0f, false).setRegistryName("pizza_fish").setCreativeTab(foods);

		pizza_meat_raw = (ItemFood)new ItemFood(4, 0.3f, false).setRegistryName("pizza_meat_raw").setCreativeTab(foods);
		pizza_chicken_raw = (ItemFood)new ItemFood(3, 0.3f, false).setRegistryName("pizza_chicken_raw").setCreativeTab(foods);
		pizza_sweet_raw = (ItemFood)new ItemFood(3, 0.2f, false).setRegistryName("pizza_sweet_raw").setCreativeTab(foods);
		pizza_fish_raw = (ItemFood)new ItemFood(2, 0.4f, false).setRegistryName("pizza_fish_raw").setCreativeTab(foods);

		bread_fine = (ItemFood)new ItemFood(5, 0.8f, false).setRegistryName("fine_bread").setCreativeTab(foods);
		bread_meat = (ItemFood)new ItemFood(4, 0.45f, false).setRegistryName("bread_meat").setCreativeTab(foods);
		bread_veggie = (ItemFood)new ItemFood(8, 0.9f, false).setRegistryName("veggie_bread").setCreativeTab(foods);

		bread_meat_raw = (ItemFood)new ItemFood(2, 0.3f, false).setRegistryName("bread_meat_raw").setCreativeTab(foods);
		bread_veggie_raw = (ItemFood)new ItemFood(3, 0.3f, false).setRegistryName("veggie_bread_raw").setCreativeTab(foods);

		dry_noodles = new Item().setRegistryName("dry_noodles").setCreativeTab(foods);

		spaghetti_raw = new Item().setRegistryName("spaghetti_raw").setCreativeTab(foods);
		spaghetti_cooked = (ItemNoodle)new ItemNoodle(3, 0.7f, false).setRegistryName("spaghetti_cooked").setCreativeTab(foods);
		spaghetti_sauced = (ItemNoodle)new ItemNoodle(5, 0.5f, false).setRegistryName("spaghetti_sauced").setCreativeTab(foods);
		spaghetti_bolognaise = (ItemNoodle)new ItemNoodle(5, 0.7f, false).setRegistryName("spaghetti_bolognaise").setCreativeTab(foods);
		spaghetti_cheese = (ItemNoodle)new ItemNoodle(5, 0.8f, false).setRegistryName("spaghetti_cheese").setCreativeTab(foods);
		spaghetti_veggie = (ItemNoodle)new ItemNoodle(8, 0.5f, false).setRegistryName("spaghetti_veggie").setCreativeTab(foods);

		noodles_chicken_cooked = (ItemNoodle)new ItemNoodle(6, 0.7f, false).setRegistryName("noodles_chicken_cooked").setCreativeTab(foods);
		noodles_fish_cooked = (ItemNoodle)new ItemNoodle(6, 0.7f, false).setRegistryName("noodles_fish_cooked").setCreativeTab(foods);
		noodles_meat_cooked = (ItemNoodle)new ItemNoodle(5, 0.8f, false).setRegistryName("noodles_meat_cooked").setCreativeTab(foods);
		noodles_veggie_cooked = (ItemNoodle)new ItemNoodle(7, 0.7f, false).setRegistryName("noodles_veggie_cooked").setCreativeTab(foods);

		noodles_chicken = new Item().setRegistryName("noodles_chicken").setCreativeTab(foods);
		noodles_fish = new Item().setRegistryName("noodles_fish").setCreativeTab(foods);
		noodles_meat= new Item().setRegistryName("noodles_meat").setCreativeTab(foods);
		noodles_veggie = new Item().setRegistryName("noodles_veggie").setCreativeTab(foods);

		items = Arrays.stream(ExSartagineItems.class.getFields()).map(field -> {
			try {
				return field.get(null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}).filter(Item.class::isInstance).map(Item.class::cast).collect(Collectors.toList());

		register(registry);
	}

	private static void register(IForgeRegistry<Item> registry) {
		for (Item item : items) {
			item.setTranslationKey(MODID + "." + item.getRegistryName().getPath());
			registry.register(item);
		}
	}

	public static void registerRenders(){
		for (Item item : items) {
			registerRender(item,MODID,item.getRegistryName().getPath());
		}
	}

	public static void registerRender(Item item, String modid, String name) {
		ModelLoader.setCustomModelResourceLocation(item,0,new ModelResourceLocation(new ResourceLocation(modid,name),"inventory"));
	}
}
