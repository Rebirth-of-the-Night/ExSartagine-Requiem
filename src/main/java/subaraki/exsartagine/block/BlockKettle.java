package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.tileentity.TileEntityKettle;
import subaraki.exsartagine.util.Reference;

import javax.annotation.Nullable;

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

    public static final AxisAlignedBB AABB = new AxisAlignedBB(.0625, 0, .0625, .9375, .5, .9375);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityKettle) {
            TileEntityKettle kettle = (TileEntityKettle) tile;

            if (kettle.tryClean(playerIn, hand)) {
                return true;
            }

            ItemStack stack = playerIn.getHeldItem(hand);
            if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                if (!worldIn.isRemote) {
                    IFluidHandler iFluidHandler = kettle.fluidInputTank;
                    if (FluidUtil.interactWithFluidHandler(playerIn, hand, iFluidHandler)) {
                        return true;
                    }
                }
                return true;
            }
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
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(LEGS,(meta & LEGS_BIT) != 0);
    }
}
