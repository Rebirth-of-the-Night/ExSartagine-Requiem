package subaraki.exsartagine.block;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import subaraki.exsartagine.init.ExSartagineItems;
import subaraki.exsartagine.init.ModSounds;
import subaraki.exsartagine.init.RecipeTypes;
import subaraki.exsartagine.particle.ParticleBoilingBubble;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.tileentity.TileEntityPot;
import subaraki.exsartagine.util.ConfigHandler;
import subaraki.exsartagine.util.Helpers;
import subaraki.exsartagine.util.Reference;

import java.util.Random;

public class BlockPot extends HeatableGuiBlock {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public enum Variant {
		POT(RecipeTypes.POT, Material.ROCK, SoundType.STONE, Reference.POT,
				new AxisAlignedBB(0.15D, 0.0D, 0.15D, 0.85D, 0.6D, 0.85D)),
		CAULDRON(RecipeTypes.CAULDRON, Material.IRON, SoundType.METAL, Reference.CAULDRON,
				new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.525D, 0.9375D));

		public final IRecipeType<? extends PotRecipe> recipeType;
        public final Material material;
        public final SoundType soundType;
		public final int guiId;
        public final AxisAlignedBB boundingBox;

        Variant(IRecipeType<? extends PotRecipe> recipeType, Material material, SoundType soundType, int guiId, AxisAlignedBB boundingBox) {
            this.recipeType = recipeType;
            this.material = material;
            this.soundType = soundType;
			this.guiId = guiId;
            this.boundingBox = boundingBox;
        }
	}

	private final Variant variant;

	public BlockPot(Variant variant) {
		super(variant.material, variant.guiId);
		this.variant = variant;

		setHardness(8f);
		setSoundType(variant.soundType);
		setCreativeTab(ExSartagineItems.pots);
		setHarvestLevel("pickaxe", 1);
		setHardness(3.5f);
		this.setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.NORTH));
	}

	public Variant getVariant() {
		return variant;
	}

	/////////////////rendering//////////////
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return variant.boundingBox;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return variant == Variant.CAULDRON ? BlockRenderLayer.CUTOUT : BlockRenderLayer.SOLID;
	}

///////////////TE Stuff//////////////////////


	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityPot)
		{
			TileEntityPot pot = (TileEntityPot)tile;
			if (pot.isSoiled() && pot.tryClean(player, hand))
				return true;
			if (pot.fillFromItem(player, hand))
				return true;
		}
		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (entity.posY + 0.0625D < pos.getY() + variant.boundingBox.maxY || !(entity instanceof EntityItem))
			return;

		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityPot)
			((TileEntityPot)tile).pickUpItem((EntityItem)entity);
	}

	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if(!Helpers.bernoulli(world.rand, ConfigHandler.pot_rain_fill_chance))
			return;

		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityPot)
			((TileEntityPot)tile).fillWithRain();
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPot();
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
		double d1 = (double)pos.getY() + 0.6D;
		double d2 = (double)pos.getZ() + 0.5D;

		TileEntity tile = worldIn.getTileEntity(pos);
		if(!(tile instanceof TileEntityPot))
			return;

		TileEntityPot pot = (TileEntityPot)tile;
		if(!pot.isWorking())
			return;

		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0+(RANDOM.nextDouble()/5 - 0.1), d1, d2+(RANDOM.nextDouble()/5 - 0.1), 0.0D, 0.0D, 0.0D);

		FluidStack fluid = pot.getStoredFluid();
		if(fluid == null)
		{
			worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ModSounds.FRYING, SoundCategory.BLOCKS, 1, 1, false);
			return;
		}
		worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, ModSounds.BUBBLING, SoundCategory.BLOCKS, 1, 1, false);

		if (fluid.getFluid().getDensity(fluid) >= 1800) { // pretty much arbitrary threshold; should exclude lava and molten metals
			return;
		}

		int col = fluid.getFluid().getColor(fluid);
		float r = ((col >> 16) & 0xFF) / 255F;
		float g = ((col >> 8) & 0xFF) / 255F;
		float b = (col & 0xFF) / 255F;

		double bh;
		switch (variant) {
			case POT:
				bh = d1 - 0.35;
				for(int i = 2 + RANDOM.nextInt(2) ; i > 0 ; i--)
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBoilingBubble(worldIn,
							d0+(RANDOM.nextDouble()*0.3 - 0.15), bh+RANDOM.nextDouble()*0.001, d2+(RANDOM.nextDouble()*0.3 - 0.15), r, g, b));
				break;
			case CAULDRON:
				bh = d1 - 0.2;
				for(int i = 4 + RANDOM.nextInt(4) ; i > 0 ; i--) {
					double ang = RANDOM.nextDouble() * 2.0 * Math.PI, rad = RANDOM.nextDouble() * 0.35;
					Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBoilingBubble(worldIn,
							d0+rad*Math.cos(ang), bh+RANDOM.nextDouble()*0.001, d2+rad*Math.sin(ang), r, g, b));
				}
				break;
		}
	}

	/////// TURNING STUFF ////////////////

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, LEGS, DIRTY);
	}
	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i = i | state.getValue(FACING).getHorizontalIndex();
		i += super.getMetaFromState(state);
		return i;
	}
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta & 3);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(LEGS,(meta & LEGS_BIT) != 0);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

}
