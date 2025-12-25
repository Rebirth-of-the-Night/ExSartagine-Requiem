package subaraki.exsartagine.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import subaraki.exsartagine.block.BlockPot;
import subaraki.exsartagine.recipe.IRecipeType;
import subaraki.exsartagine.recipe.ModRecipes;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.util.ConfigHandler;

import javax.annotation.Nullable;

public class TileEntityPot extends TileEntityCooker<PotRecipe> {

    public static final int TANK_CAPACITY = 1000;

    private static final int MAX_PICK_UP_DELAY = 16;

    private final FluidTank fluidTank = new FluidTank(TANK_CAPACITY);

    // no need to serialize these
    private BlockPot.Variant variant = null;
    private int pickUpDelay = 0;

    public TileEntityPot() {
        initInventory(2);
    }

    public BlockPot.Variant getVariant() {
        if (variant == null) {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof BlockPot) {
                variant = ((BlockPot)block).getVariant();
            } else {
                variant = BlockPot.Variant.POT;
            }
        }
        return variant;
    }

    public IRecipeType<? extends PotRecipe> getRecipeType() {
        return getVariant().recipeType;
    }

    public FluidStack getStoredFluid() {
        return fluidTank.getFluid();
    }

    public boolean fillFromItem(EntityPlayer player, EnumHand hand) {
        if (FluidUtil.interactWithFluidHandler(player, hand, fluidTank)) {
            if (!world.isRemote) {
                markDirty();
            }
            return true;
        }
        return false;
    }

    public void pickUpItem(EntityItem item) {
        // multiple pick-ups can occur on the same tick, but there's a delay between pick-up ticks
        if (world.isRemote || (pickUpDelay > 0 && pickUpDelay < MAX_PICK_UP_DELAY)) {
            return;
        }

        ItemStack rem = getInventory().insertItem(INPUT, item.getItem(), false);
        if (rem.isEmpty()) {
            item.setDead();
        } else {
            item.setItem(rem);
        }
        pickUpDelay = MAX_PICK_UP_DELAY;
    }

    public void fillWithRain() {
        if (world.isRemote) {
            return;
        }

        fluidTank.fill(new FluidStack(FluidRegistry.WATER, ConfigHandler.pot_rain_fill_amount), true);
        markDirty();
    }

    @Override
    public void update() {
        super.update();
        if (pickUpDelay > 0) {
            --pickUpDelay;
        }
    }

    @Nullable
    @Override
    public PotRecipe findRecipe() {
        return ModRecipes.findFluidRecipe(getInventory(), fluidTank, PotRecipe.class, getRecipeType());
    }

    @Override
    public boolean doesRecipeMatch(PotRecipe recipe) {
        return recipe.match(getInventory(), fluidTank);
    }

    @Override
    public boolean canFitOutputs(PotRecipe recipe) {
        return canFitOutputs0(recipe);
    }

    @Override
    public void processRecipe(PotRecipe recipe) {
        // produce output
        getInventory().insertItem(RESULT, recipe.getResult(getInventory()).copy(), false);

        // consume inputs
        getInput().shrink(1);
        final FluidStack fluid = recipe.getInputFluid();
        if (fluid != null && fluid.amount > 0) {
            fluidTank.drain(fluid.amount, true);
        }

        super.processRecipe(recipe);
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return ModRecipes.hasResult(stack, getRecipeType());
    }

    @Override
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidTank);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        fluidTank.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        fluidTank.readFromNBT(compound);
    }

}
