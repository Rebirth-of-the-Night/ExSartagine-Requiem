package subaraki.exsartagine.init;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.IForgeRegistry;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.block.*;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.util.ConfigHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ExSartagineBlocks {

	public static Block wok;
	public static Block smelter;
	public static Block pot;
	public static Block range;
	public static Block range_extended;
	public static Block range_extended_lit;

	public static Block hearth;
	public static Block hearth_extended;
	public static Block hearth_extended_lit;

	public static Block kettle;

	//todo replace with a tag in 1.13+
	private static final Supplier<Set<Block>> ranges = () -> Sets.newHashSet(range, range_extended, range_extended_lit);
	private static final Supplier<Set<Block>> hearths = () -> Sets.newHashSet(hearth, hearth_extended, hearth_extended_lit);

	public static void load(IForgeRegistry<Block> registry){
		wok = new WokBlock().setRegistryName("wok").setTranslationKey(ExSartagine.MODID+".wok");
		smelter = new BlockSmelter();
		pot = new BlockPot();
		range = new BlockRange(() -> ConfigHandler.range_requires_ignition,3).setRegistryName("range").setTranslationKey(ExSartagine.MODID + ".range");
		range_extended = new BlockRangeExtension(false,() -> range_extended,() -> range_extended_lit,ranges).setRegistryName("range_extended").setTranslationKey(ExSartagine.MODID+".range_extended");
		range_extended_lit = new BlockRangeExtension(true,() -> range_extended,() -> range_extended_lit,ranges).setRegistryName("range_extended_lit").setTranslationKey(ExSartagine.MODID+".range_extended_lit");

		hearth = new BlockRange(() -> ConfigHandler.hearth_requires_ignition,1).setRegistryName("hearth").setTranslationKey(ExSartagine.MODID + ".hearth");
		hearth_extended = new BlockRangeExtension(false,() -> hearth_extended,() -> hearth_extended_lit,hearths).setRegistryName("hearth_extended").setTranslationKey(ExSartagine.MODID+".hearth_extended");
		hearth_extended_lit = new BlockRangeExtension(true,() -> hearth_extended,() -> hearth_extended_lit,hearths).setRegistryName("hearth_extended_lit").setTranslationKey(ExSartagine.MODID+".hearth_extended_lit");

		kettle = new BlockKettle(Material.IRON).setRegistryName("kettle").setCreativeTab(ExSartagineItems.pots).setTranslationKey(ExSartagine.MODID +".kettle").setHardness(5);
		register(registry);
	}
	
	private static void register(IForgeRegistry<Block> registry) {
		registry.register(wok);
		registry.register(smelter);
		registry.register(pot);
		registry.register(range);
		registry.register(range_extended);
		registry.register(range_extended_lit);
		registry.register(hearth);
		registry.register(hearth_extended);
		registry.register(hearth_extended_lit);
		registry.register(kettle);
	}
}
