package subaraki.exsartagine.block;

import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import subaraki.exsartagine.item.ExSartagineItems;
import subaraki.exsartagine.tileentity.TileEntityPan;
import subaraki.exsartagine.util.Reference;

public class BlockPan extends BlockHeatable {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockPan() {
		super(Material.IRON, Reference.PAN);

		setLightLevel(0.0f);
		setSoundType(SoundType.METAL);
		setCreativeTab(ExSartagineItems.pots);
		setHarvestLevel("pickaxe", 1);
		setTranslationKey(Reference.MODID+".pan");
		setRegistryName("pan");
		setHardness(3.5f);
		this.setLightOpacity(0);
	}

	/////////////////rendering//////////////
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	///////////////TE Stuff//////////////////////

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPan();
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
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 0.15D;
		double d2 = (double)pos.getZ() + 0.5D;

		if(worldIn.getTileEntity(pos) instanceof TileEntityPan)
		{
			if(((TileEntityPan)worldIn.getTileEntity(pos)).isCooking())
			{
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0+(RANDOM.nextDouble()/1.5 - 0.35), d1, d2+(RANDOM.nextDouble()/1.5 - 0.35), 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0+(RANDOM.nextDouble()/1.5 - 0.35), d1, d2+(RANDOM.nextDouble()/1.5 - 0.35), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	/////// TURNING STUFF ////////////////

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public Class<?> getTileEntity() {
		return TileEntityPan.class;
	}
}
