package subaraki.exsartagine.block;

import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import subaraki.exsartagine.init.ExSartagineBlocks;
import subaraki.exsartagine.tileentity.TileEntityRange;
import subaraki.exsartagine.tileentity.TileEntityRangeExtension;

public class BlockRangeExtension extends Block {

    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private final boolean lit;
    private final Supplier<Block> cold;
    private final Supplier<Block> hot;
    private final Supplier<Set<Block>> valid;

    public BlockRangeExtension(boolean lit, Supplier<Block> cold, Supplier<Block> hot, Supplier<Set<Block>> valid) {
        super(Material.ROCK);
        this.lit = lit;
        this.cold = cold;
        this.hot = hot;
        this.valid = valid;
        setHardness(8f);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.TOOLS);
        setHarvestLevel("pickaxe", 0);
        setHardness(3.5f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));
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

    //check to see if this extension should drop itself when a nearby block is broken
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityRangeExtension) {
            TileEntityRangeExtension rangeExtension = (TileEntityRangeExtension) te;
            BlockPos contPos = rangeExtension.getParentRange();
            if (contPos == null || !canStay(world, pos)) {
                world.destroyBlock(pos,true);
            }
        }
    }

    public boolean canStay(World level,BlockPos pos) {
        for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
            BlockPos offset = pos.offset(dir);
            Block block = level.getBlockState(offset).getBlock();
            if (valid.get().contains(block)) {
                return true;
            }
        }
        return false;
    }

    //this also gets called when setting extensions to cooking/not cooking
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState newState = worldIn.getBlockState(pos);

        if (!(newState.getBlock() instanceof BlockRangeExtension)) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntityRangeExtension) {
                BlockPos parentRange = ((TileEntityRangeExtension) tile).getParentRange();
                if (parentRange != null) {
                    TileEntity range = worldIn.getTileEntity(parentRange);
                    if (range instanceof TileEntityRange)
                        ((TileEntityRange) range).disconnect(pos);
                }
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    /////////////////rendering//////////////
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
            BlockPos offset = pos.offset(dir);
            TileEntity te = worldIn.getTileEntity(offset);

            if (te instanceof TileEntityRangeExtension) {
                TileEntityRangeExtension rangeExtension = (TileEntityRangeExtension) te;
                BlockPos rangePos = rangeExtension.getParentRange();

                if (rangePos != null) {
                    TileEntity te1 = worldIn.getTileEntity(rangePos);
                    if (te1 instanceof TileEntityRange) {
                        TileEntityRange range = (TileEntityRange) te1;
                        return range.canConnect() && super.canPlaceBlockAt(worldIn, pos);
                    }
                }

            } else if (te instanceof TileEntityRange) {
                TileEntityRange range = (TileEntityRange) te;
                return range.canConnect() && super.canPlaceBlockAt(worldIn, pos);
            }
        }
        return false;
    }

    //see trough !
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
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

        if (this == ExSartagineBlocks.range_extended_lit) {

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

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(getColdBlock());
    }

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(getColdBlock());
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
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityRangeExtension && !worldIn.isRemote) {


            for (EnumFacing dir : EnumFacing.Plane.HORIZONTAL) {
                BlockPos offset = pos.offset(dir);
                TileEntity te = worldIn.getTileEntity(offset);

                if (te instanceof TileEntityRangeExtension) {
                    TileEntityRangeExtension rangeExtension = (TileEntityRangeExtension) te;
                    BlockPos rangePos = rangeExtension.getParentRange();

                    if (rangePos != null) {
                        TileEntity te1 = worldIn.getTileEntity(rangePos);
                        if (te1 instanceof TileEntityRange) {
                            TileEntityRange range = (TileEntityRange) te1;
                            if (range.canConnect()) {
                                range.connect((TileEntityRangeExtension) tile);
                                return;
                            }
                        }
                    }

                } else if (te instanceof TileEntityRange) {
                    TileEntityRange range = (TileEntityRange) te;
                    if (range.canConnect()) {
                        range.connect((TileEntityRangeExtension) tile);
                        return;
                    }
                }
            }
        }

        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }


		public Block getColdBlock() {
			return cold.get();
		}

    public Block getHotBlock() {
        return hot.get();
    }
}
