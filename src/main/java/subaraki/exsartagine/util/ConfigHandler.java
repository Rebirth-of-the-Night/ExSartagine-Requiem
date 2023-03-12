package subaraki.exsartagine.util;

import net.minecraftforge.common.config.Config;
import subaraki.exsartagine.ExSartagine;


@Config(modid = ExSartagine.MODID)
public class ConfigHandler {

	@Config.Name("bonus_chance")
	@Config.Comment("Define how often a bonus ingot will smelt in the Smelter. Expressed in percent. [0% - 100%]")
	public static int percent = 33;
}