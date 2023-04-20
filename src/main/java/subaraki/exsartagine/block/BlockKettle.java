package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.TileEntityKettle;
import subaraki.exsartagine.util.Reference;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockKettle extends HeatableGuiBlock {

    public BlockKettle(Material materialIn) {
        super(materialIn, Reference.KETTLE);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityKettle();
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    }

    public static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, .875, 1);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);

        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            if (!worldIn.isRemote) {
                TileEntityKettle kettle = (TileEntityKettle) worldIn.getTileEntity(pos);
                IFluidHandler iFluidHandler = kettle.fluidInputTank;
                if (FluidUtil.interactWithFluidHandler(playerIn,hand,iFluidHandler)) {
                    return true;
                }
            }
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (world.getTileEntity(pos) instanceof TileEntityKettle) {
            if (fromPos.up().equals(pos)) { //if the block is beneath us
                IBlockState down = world.getBlockState(fromPos);
                if (!ModRecipes.isPlaceable(down)) {
                    dropBlockAsItem(world, pos, getDefaultState(), 0);
                    world.setBlockToAir(pos);
                }
            }
        }
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if (tileEntity instanceof TileEntityKettle) {
            TileEntityKettle tileEntityKettle = (TileEntityKettle) tileEntity;
            if (tileEntityKettle.running) {
                worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ModSounds.BUBBLING, SoundCategory.BLOCKS, 1, 1, false);
            }
        }
    }
}
