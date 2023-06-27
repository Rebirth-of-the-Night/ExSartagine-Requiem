package subaraki.exsartagine.integration;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import blusunrize.immersiveengineering.common.Config;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDevice1;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.TileEntityRange;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;

public class ImmersiveEngineering {
    public static void registerHandlers(){
        ExternalHeaterHandler.registerHeatableAdapter(KitchenwareBlockEntity.class, new KitchenwareAdapter());
        ExternalHeaterHandler.registerHeatableAdapter(TileEntityRange.class, new RangeAdapter());
        ModRecipes.addPlaceable(IEContent.blockMetalDevice1, iBlockState -> (
                iBlockState.getValue(PropertyEnum.create("type", BlockTypes_MetalDevice1.class))==BlockTypes_MetalDevice1.FURNACE_HEATER &&
                        !iBlockState.getValue(IEProperties.MULTIBLOCKSLAVE) &&
                        iBlockState.getValue(IEProperties.FACING_ALL) != EnumFacing.UP &&
                        iBlockState.getValue(IEProperties.BOOLEANS[0])
        ),true, false);
        ModRecipes.addPlaceable(IEContent.blockMetalDevice1, iBlockState -> (
                iBlockState.getValue(PropertyEnum.create("type", BlockTypes_MetalDevice1.class))==BlockTypes_MetalDevice1.FURNACE_HEATER &&
                        !iBlockState.getValue(IEProperties.MULTIBLOCKSLAVE) &&
                        iBlockState.getValue(IEProperties.FACING_ALL) != EnumFacing.UP &&
                        !iBlockState.getValue(IEProperties.BOOLEANS[0])
        ),false, false);
    }
    public static class KitchenwareAdapter extends ExternalHeaterHandler.HeatableAdapter<KitchenwareBlockEntity>{
        @Override
        public int doHeatTick(KitchenwareBlockEntity tileEntity, int energyAvailable, boolean redstone) {
            int consumption = Config.IEConfig.Machines.heater_consumption;
            if(energyAvailable >= consumption){
                return consumption;
            }
            return 0;
        }
    }

    public static class RangeAdapter extends ExternalHeaterHandler.HeatableAdapter<TileEntityRange>{
        @Override
        public int doHeatTick(TileEntityRange tileEntity, int energyAvailable, boolean redstone) {
            int consumption = Config.IEConfig.Machines.heater_consumption;
            if(!redstone && energyAvailable >= consumption){
                tileEntity.setFuelTimer(-1);
                tileEntity.setCooking(true);
                return consumption;
            }
            if(tileEntity.getFuelTimer()==-1){tileEntity.setFuelTimer(0);}
            return 0;
        }
    }
}
