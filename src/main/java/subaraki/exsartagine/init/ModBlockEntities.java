package subaraki.exsartagine.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.tileentity.*;

public class ModBlockEntities {
    public static void register() {
        GameRegistry.registerTileEntity(WokBlockEntity.class, new ResourceLocation(ExSartagine.MODID, "pan"));
        GameRegistry.registerTileEntity(TileEntitySmelter.class, new ResourceLocation(ExSartagine.MODID, "smelter"));
        GameRegistry.registerTileEntity(TileEntityPot.class, new ResourceLocation(ExSartagine.MODID, "pot"));
        GameRegistry.registerTileEntity(TileEntityRange.class, new ResourceLocation(ExSartagine.MODID, "range"));
        GameRegistry.registerTileEntity(TileEntityRangeExtension.class, new ResourceLocation(ExSartagine.MODID, "range_extension"));
        GameRegistry.registerTileEntity(TileEntityKettle.class, new ResourceLocation(ExSartagine.MODID, "kettle"));
        GameRegistry.registerTileEntity(TileEntityCuttingBoard.class, new ResourceLocation(ExSartagine.MODID, "cutting_board"));
    }
}
