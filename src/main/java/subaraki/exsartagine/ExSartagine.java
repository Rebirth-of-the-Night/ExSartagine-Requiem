package subaraki.exsartagine;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import subaraki.exsartagine.block.BlockRange;
import subaraki.exsartagine.block.BlockRangeExtension;
import subaraki.exsartagine.gui.GuiHandler;
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.init.ModBlockEntities;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.integration.Integration;
import subaraki.exsartagine.network.PacketHandler;
import subaraki.exsartagine.network.TransferHeldItemPacket;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.recipe.WokRecipe;
import subaraki.exsartagine.tileentity.*;
import subaraki.exsartagine.tileentity.render.CookerRenderer;
import subaraki.exsartagine.tileentity.render.CooktopRenderer;
import subaraki.exsartagine.tileentity.render.CuttingBoardRenderer;
import subaraki.exsartagine.tileentity.render.WokRenderer;
import subaraki.exsartagine.tileentity.util.HeldItemTransferable;
import subaraki.exsartagine.util.ConfigHandler;
import subaraki.exsartagine.util.Reference;

@Mod.EventBusSubscriber
@Mod(name = Reference.NAME, modid = ExSartagine.MODID, version = Reference.VERSION)
public class ExSartagine {

    public static final String MODID = "exsartagine";
    public static ExSartagine instance;

    private Logger logger;

    public ExSartagine() {
        instance = this;
    }

    public static class DebugStuff {
        public static void run() {
            ModRecipes.addRecipe(new ResourceLocation("test:recipe"),new WokRecipe(Lists.newArrayList(Ingredient.fromItem(Items.IRON_INGOT)),
                    new FluidStack(FluidRegistry.LAVA,100),Lists.newArrayList(new ItemStack(Items.GOLD_INGOT)),3,0));
        }
    }

    public Logger getLogger() {
        return logger;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        logger = event.getModLog();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventBusSubscriber(Side.CLIENT)
    public static class Client {
        @SubscribeEvent
        public static void models(ModelRegistryEvent e) {
            ExSartagineItems.registerRenders();
            ClientRegistry.bindTileEntitySpecialRenderer(WokBlockEntity.class, new WokRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPot.class, new CookerRenderer());
            CooktopRenderer cooktopRenderer = new CooktopRenderer();
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRange.class, cooktopRenderer);
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRangeExtension.class, cooktopRenderer);
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCuttingBoard.class, new CuttingBoardRenderer());
        }

        @SubscribeEvent
        public static void onPlayerInteractBlock(PlayerInteractEvent.RightClickBlock event) {
            if (event.getFace() != EnumFacing.UP) {
                return;
            }
            TileEntity te = event.getWorld().getTileEntity(new BlockPos(event.getHitVec()));
            if (!(te instanceof TileEntityCuttingBoard)) {
                return;
            }
            ItemStack held = event.getEntityPlayer().getHeldItem(event.getHand());
            if (!Oredict.checkMatch(Oredict.KNIFE, held)) {
                return;
            }
            event.setUseBlock(Event.Result.ALLOW);
        }

        @SubscribeEvent
        public static void onMouseEvent(MouseEvent event) {
            int dwheel = event.getDwheel();
            if (dwheel == 0) {
                return;
            }

            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player == null || mc.objectMouseOver == null
                    || mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK || !mc.player.isSneaking()) {
                return;
            }

            BlockPos pos = mc.objectMouseOver.getBlockPos();
            TileEntity te = mc.player.world.getTileEntity(pos);
            if (!(te instanceof HeldItemTransferable)) {
                return;
            }

            HeldItemTransferable target = (HeldItemTransferable) te;
            Vec3d hitVec = mc.objectMouseOver.hitVec;
            int zone = target.getTransferFromHeldItemZone(
                    mc.player, EnumHand.MAIN_HAND, mc.objectMouseOver.sideHit,
                    (float) hitVec.x - pos.getX(), (float) hitVec.y - pos.getY(), (float) hitVec.z - pos.getZ());
            if (zone < 0) {
                return;
            }
            event.setCanceled(true);

            boolean isInsert = dwheel > 0;
            EnumHand hand;
            if (target.transferFromHeldItem(mc.player, EnumHand.MAIN_HAND, isInsert, zone)) {
                hand = EnumHand.MAIN_HAND;
            } else if (target.transferFromHeldItem(mc.player, EnumHand.OFF_HAND, isInsert, zone)) {
                hand = EnumHand.OFF_HAND;
            } else {
                return;
            }

            PacketHandler.INSTANCE.sendToServer(new TransferHeldItemPacket(pos, hand, isInsert, zone));
        }

        @SubscribeEvent
        public static void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
            RayTraceResult trace = event.getTarget();
            if (trace.typeOfHit == RayTraceResult.Type.BLOCK && trace.sideHit == EnumFacing.UP) {
                BlockPos pos = trace.getBlockPos();
                int posX = pos.getX(), posY = pos.getY(), posZ = pos.getZ();
                IBlockState state = event.getPlayer().world.getBlockState(pos);
                Block block = state.getBlock();
                if (block instanceof BlockRange || block instanceof BlockRangeExtension) {
                    double hitX = trace.hitVec.x - posX, hitZ = trace.hitVec.z - posZ;
                    if (hitZ < 0.5) {
                        if (hitX < 0.5) {
                            drawHighlightBox(event.getPlayer(),
                                    posX + 0.03125f, posZ + 0.03125f,
                                    posX + 0.46875f, posZ + 0.46875f,
                                    posY + 1.002f, event.getPartialTicks());
                        } else {
                            drawHighlightBox(event.getPlayer(),
                                    posX + 0.53125f, posZ + 0.03125f,
                                    posX + 0.96875f, posZ + 0.46875f,
                                    posY + 1.002f, event.getPartialTicks());
                        }
                    } else if (hitX < 0.5) {
                        drawHighlightBox(event.getPlayer(),
                                posX + 0.03125f, posZ + 0.53125f,
                                posX + 0.46875f, posZ + 0.96875f,
                                posY + 1.002f, event.getPartialTicks());
                    } else {
                        drawHighlightBox(event.getPlayer(),
                                posX + 0.53125f, posZ + 0.53125f,
                                posX + 0.96875f, posZ + 0.96875f,
                                posY + 1.002f, event.getPartialTicks());
                    }
                }
            }
        }
        
        private static void drawHighlightBox(EntityPlayer player, float minX, float minZ, float maxX, float maxZ, float y, float partialTicks) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    -player.lastTickPosX - (player.posX - player.lastTickPosX) * partialTicks,
                    -player.lastTickPosY - (player.posY - player.lastTickPosY) * partialTicks,
                    -player.lastTickPosZ - (player.posZ - player.lastTickPosZ) * partialTicks);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GlStateManager.color(0f, 0f, 0f, 0.4f);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
            buf.pos(minX, y, minZ).endVertex();
            buf.pos(maxX, y, minZ).endVertex();
            buf.pos(maxX, y, maxZ).endVertex();
            buf.pos(minX, y, maxZ).endVertex();
            tess.draw();

            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
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
        ModRecipes.lateInit();
        Integration.postInit();
        if (!FluidRegistry.isFluidRegistered(ConfigHandler.washer_fluid)) {
            logger.warn("Configured washer fluid is not in the registry: {}", ConfigHandler.washer_fluid);
        }
    }
}
