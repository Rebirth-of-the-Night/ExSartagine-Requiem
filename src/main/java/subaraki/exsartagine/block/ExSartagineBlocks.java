package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.IForgeRegistry;
import subaraki.exsartagine.util.Reference;

public class ExSartagineBlocks {

	public static Block pan;
	public static Block smelter;
	public static Block pot;
	public static Block range;
	public static Block range_extension;
	public static Block range_extension_lit;
	public static Block kettle;

	public static void load(IForgeRegistry<Block> registry){
		pan = new WokBlock();
		smelter = new BlockSmelter();
		pot = new BlockPot();
		range = new BlockRange();
		range_extension = new BlockRangeExtension("range_extended");
		range_extension_lit = new BlockRangeExtension("range_extended_lit");
		kettle = new BlockKettle(Material.IRON).setRegistryName("kettle").setTranslationKey(Reference.MODID +".kettle").setHardness(5);
		register(registry);
	}
	
	private static void register(IForgeRegistry<Block> registry) {
		registry.register(pan);
		registry.register(smelter);
		registry.register(pot);
		registry.register(range);
		registry.register(range_extension);
		registry.register(range_extension_lit);
		registry.register(kettle);
	}
}
