package subaraki.exsartagine.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.tileentity.TileEntityRange;
import subaraki.exsartagine.tileentity.TileEntityRangeExtension;

public class BlockRangeExtension extends Block {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public BlockRangeExtension(String name) {
        super(Material.ROCK);

        setLightLevel(0.0f);
        setHardness(8f);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.TOOLS);
        setHarvestLevel("pickaxe", 0);
        setTranslationKey(ExSartagine.MODID + "." + name);
        setRegistryName(name);
        setHardness(3.5f);
        this.setLightOpacity(0);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));

    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing enumfacing = state.getValue(FACING);
        BlockPos nextTo = pos.offset(enumfacing.rotateYCCW());

        //the previous extension is broken : break this
        BlockPos prev = pos.offset(enumfacing.rotateY());
        if (world.getBlockState(prev).getBlock() == Blocks.AIR) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return;
        } else if (world.getTileEntity(pos) instanceof TileEntityRangeExtension) {
            TileEntityRangeExtension currentRange = (TileEntityRangeExtension) world.getTileEntity(pos);

            //placed (should exclude being broken from the left and having a furnace right, because the code for popping the block is above)
            if (world.getBlockState(nextTo).getBlock() == Blocks.FURNACE) {
                BlockPos parentRangePos = currentRange.getParentRange();
                if (parentRangePos != null && world.getTileEntity(parentRangePos) instanceof TileEntityRange) {
                    TileEntityRange parentRange = (TileEntityRange) world.getTileEntity(parentRangePos);
                    if (parentRange.canConnect()) {
                        world.setBlockToAir(nextTo);

                        boolean isLit = parentRange.isFueled();

                        IBlockState newState = null;

                        if (isLit)
                            newState = ExSartagineBlocks.range_extension_lit.getDefaultState().
                                    withProperty(FACING, state.getValue(BlockRangeExtension.FACING));

                        else
                            newState = ExSartagineBlocks.range_extension.getDefaultState().
                                    withProperty(FACING, state.getValue(BlockRangeExtension.FACING));

                        world.setBlockState(nextTo, newState, 3);

                        TileEntity newRange = world.getTileEntity(nextTo);

                        if (newRange instanceof TileEntityRangeExtension) {
                            TileEntityRangeExtension extension = ((TileEntityRangeExtension) newRange);
                            extension.setParentRange(parentRange.getPos());
                            extension.setCooking(isLit);
                            parentRange.connect(extension);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityRangeExtension) {
            BlockPos parentRange = ((TileEntityRangeExtension) tile).getParentRange();
            if (parentRange != null) {
                TileEntity range = worldIn.getTileEntity(parentRange);
                if (range instanceof TileEntityRange)
                    ((TileEntityRange) range).disconnect(pos);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    /////////////////rendering//////////////
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    //see trough !
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    ///////////////TE Stuff//////////////////////
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRangeExtension();
    }

    /////////////// MISC //////////////////////

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

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = (double) pos.getX() + 0.5D;
        double d1 = (double) pos.getY() + 0.3D;
        double d2 = (double) pos.getZ() + 0.5D;

        if (worldIn.getTileEntity(pos) instanceof TileEntityRangeExtension) {
            TileEntityRangeExtension tere = ((TileEntityRangeExtension) worldIn.getTileEntity(pos));
            if (tere.isCooking()) {

                EnumFacing enumfacing = stateIn.getValue(FACING);
                switch (enumfacing) {
                    case NORTH:
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2 - 0.6, 0.0D, 0.0D, 0.0D);
                        break;
                    case WEST:
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.6, d1, d2, 0.0D, 0.0D, 0.0D);
                        break;
                    case SOUTH:
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2 + 0.6, 0.0D, 0.0D, 0.0D);
                        break;
                    case EAST:
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.6, d1, d2, 0.0D, 0.0D, 0.0D);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(getUsedBlock());
    }

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(getUsedBlock());
	}

    /////// TURNING STUFF ////////////////

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();
        return i;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta & 3); //untill third bit ? so facing only

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }


		public static Block getUsedBlock() {
			return Blocks.IRON_BLOCK;//ExSartagineBlocks.range_extension;
		}
}
