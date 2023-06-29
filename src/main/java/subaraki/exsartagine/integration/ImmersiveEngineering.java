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
    public static void registerHeatableAdapters(){
        ExternalHeaterHandler.registerHeatableAdapter(KitchenwareBlockEntity.class, new KitchenwareAdapter());
        addPlaceableForHeater();
        ExternalHeaterHandler.registerHeatableAdapter(TileEntityRange.class, new RangeAdapter());
    }

    private static void addPlaceableForHeater(){
        ModRecipes.addPlaceable(IEContent.blockMetalDevice1, iBlockState -> (
                iBlockState.getValue(PropertyEnum.create("type", BlockTypes_MetalDevice1.class))==BlockTypes_MetalDevice1.FURNACE_HEATER
                        && !iBlockState.getValue(IEProperties.MULTIBLOCKSLAVE) //not part of multiblock
                        && iBlockState.getValue(IEProperties.FACING_ALL) != EnumFacing.UP
                        && iBlockState.getValue(IEProperties.BOOLEANS[0]) //active
        ),true, false);
        ModRecipes.addPlaceable(IEContent.blockMetalDevice1, iBlockState -> (
                iBlockState.getValue(PropertyEnum.create("type", BlockTypes_MetalDevice1.class))==BlockTypes_MetalDevice1.FURNACE_HEATER
                        && !iBlockState.getValue(IEProperties.MULTIBLOCKSLAVE) //not part of multiblock
                        && iBlockState.getValue(IEProperties.FACING_ALL) != EnumFacing.UP
                        && !iBlockState.getValue(IEProperties.BOOLEANS[0]) //inactive
        ),false, false);
    }

    public static class KitchenwareAdapter extends ExternalHeaterHandler.HeatableAdapter<KitchenwareBlockEntity>{
        @Override
        public int doHeatTick(KitchenwareBlockEntity tileEntity, int energyAvailable, boolean redstone) {
            int consumption = Config.IEConfig.Machines.heater_consumption;
            if(redstone && energyAvailable >= consumption){
                return consumption;
            }
            return 0;
        }
    }

    public static class RangeAdapter extends ExternalHeaterHandler.HeatableAdapter<TileEntityRange>{
        @Override
        public int doHeatTick(TileEntityRange tileEntity, int energyAvailable, boolean redstone) {
            int energyConsumed = 0;
            if(redstone){
                int burnTime = tileEntity.getFuelTimer();
                if(burnTime < 200){
                    int heatAttempt = 4;
                    int heatEnergyRatio = Math.max(1, Config.IEConfig.Machines.heater_consumption);
                    int energyToUse = Math.min(energyAvailable, heatAttempt*heatEnergyRatio);
                    int heat = energyToUse/heatEnergyRatio;
                    if(heat>0){
                        tileEntity.setFuelTimer(burnTime+heat);
                        energyConsumed += heat*heatEnergyRatio;
                    }
                }
            }
            return energyConsumed;
        }
    }
}
