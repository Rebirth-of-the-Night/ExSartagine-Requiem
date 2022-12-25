package subaraki.exsartagine.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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
import subaraki.exsartagine.recipe.Recipes;
import subaraki.exsartagine.tileentity.TileEntityKettle;
import subaraki.exsartagine.util.Reference;

import javax.annotation.Nullable;

public class BlockKettle extends HeatableGuiBlock {

    public BlockKettle(Material materialIn) {
        super(materialIn, Reference.KETTLE);
        setDefaultState(getDefaultState().withProperty(HEATED,false));
    }

    public static final PropertyBool HEATED = PropertyBool.create("heated");

    @Override
    public Class<?> getTileEntity() {
        return TileEntityKettle.class;
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
                if (!Recipes.isPlaceable(down)) {
                    dropBlockAsItem(world, pos, getDefaultState(), 0);
                    world.setBlockToAir(pos);
                } else if (Recipes.isHeatSource(down)) {
                    startHeating(world, state, pos);
                } else {
                    stopHeating(world, state, pos);
                }
            }
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(HEATED,Recipes.isHeatSource(world.getBlockState(pos.down())));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HEATED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HEATED) ? 1 : 0;
    }

    @Override
    public void startHeating(World world, IBlockState state, BlockPos pos) {
        world.setBlockState(pos, state.withProperty(HEATED,true));
    }

    @Override
    public void stopHeating(World world, IBlockState state, BlockPos pos) {
        world.setBlockState(pos, state.withProperty(HEATED,false));
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return super.getRenderLayer();
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, HEATED);
    }

}
