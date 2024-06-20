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

	@Config.Name("pot_rain_fill_chance")
	@Config.Comment("The chance for the pot or cauldron to be filled with rainwater on each random tick. Expressed in percent. [0% - 100%]")
	@Config.RangeInt(min = 0, max = 100)
	public static int pot_rain_fill_chance = 5;

	@Config.Name("pot_rain_fill_amount")
	@Config.Comment("The amount of rainwater collected by the pot or cauldron on each successful random tick")
	@Config.RangeInt(min = 0)
	public static int pot_rain_fill_amount = 300;

}
