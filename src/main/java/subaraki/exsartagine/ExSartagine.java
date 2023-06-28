package subaraki.exsartagine;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import subaraki.exsartagine.gui.GuiHandler;
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.init.ModBlockEntities;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.integration.Integration;
import subaraki.exsartagine.network.PacketHandler;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.recipe.WokRecipe;
import subaraki.exsartagine.tileentity.TileEntityPot;
import subaraki.exsartagine.tileentity.WokBlockEntity;
import subaraki.exsartagine.tileentity.render.CookerRenderer;
import subaraki.exsartagine.tileentity.render.WokRenderer;
import subaraki.exsartagine.util.Reference;

@Mod.EventBusSubscriber
@Mod(name = Reference.NAME, modid = ExSartagine.MODID, version = Reference.VERSION)
public class ExSartagine {

    public static final String MODID = "exsartagine";
    public static ExSartagine instance;

    public static final boolean DEBUG = Launch.blackboard.get("fml.deobfuscatedEnvironment") != null;
    public ExSartagine() {
        instance = this;
    }

    public static class DebugStuff {
        public static void run() {
            ModRecipes.addRecipe(new ResourceLocation("test:recipe"),new WokRecipe(Lists.newArrayList(Ingredient.fromItem(Items.IRON_INGOT)),
                    new FluidStack(FluidRegistry.LAVA,100),Lists.newArrayList(new ItemStack(Items.GOLD_INGOT)),3));
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void models(ModelRegistryEvent e) {
            ExSartagineItems.registerRenders();
            ClientRegistry.bindTileEntitySpecialRenderer(WokBlockEntity.class, new WokRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPot.class, new CookerRenderer());
        }
    }


    @SubscribeEvent
    public static void blocks(RegistryEvent.Register<Block> e) {
        ExSartagineBlocks.load(e.getRegistry());
        ModBlockEntities.register();
    }

    @SubscribeEvent
    public static void sounds(RegistryEvent.Register<SoundEvent> e) {
        e.getRegistry().registerAll(ModSounds.BUBBLING,ModSounds.FRYING,ModSounds.METAL_SLIDE);
    }

    @SubscribeEvent
    public static void items(RegistryEvent.Register<Item> e) {
        ExSartagineItems.load(e.getRegistry());
    }

    //the recipes must be loaded before CraftTweaker handles them!
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> ev) {
        ModRecipes.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        PacketHandler.registerMessages(MODID);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        Oredict.addToOreDict();
        ModRecipes.cacheWokInputs();
        Integration.postInit();
    }
}
