package subaraki.exsartagine.util;

import net.minecraftforge.common.config.Config;
import subaraki.exsartagine.ExSartagine;


@Config(modid = ExSartagine.MODID)
public class ConfigHandler {

	@Config.Name("bonus_chance")
	@Config.Comment("Define how often a bonus ingot will smelt in the Smelter. Expressed in percent. [0% - 100%]")
	@Config.RangeInt(min = 0,max = 100)
	public static int percent = 33;

	@Config.Name("range_requires_manual_ignition")
	@Config.Comment("Does the range require manual ignition before it will start")
	public static boolean range_requires_ignition = false;

	@Config.Name("hearth_requires_manual_ignition")
	@Config.Comment("Does the hearth require manual ignition before it will start")
	public static boolean hearth_requires_ignition = true;
}