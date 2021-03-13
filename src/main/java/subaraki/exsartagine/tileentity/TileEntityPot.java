package subaraki.exsartagine.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import subaraki.exsartagine.block.BlockPot;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.recipe.Recipes;

public class TileEntityPot extends TileEntityCooker {


    private static final int CAPACITY = 200;
    private final int cookTime = 125;
    public PotRecipe cached;

    public FluidTank fluidTank = new FluidTank( 1000);

    public TileEntityPot() {
        initInventory();
    }

    public int getWaterLevel() {
        return fluidTank.getFluidAmount();
    }

    public void replenishWater() {
        this.fluidTank.fill(new FluidStack(FluidRegistry.WATER,1000),false);
    }

    @Override
    public void update() {

        if (!world.isRemote) {
            if (canRun()) {
                PotRecipe recipe = getOrCreateRecipe();
                if (recipe != null) {
                    if (cookTime == progress) {
                        process();
                    } else {
                        if (isCooking) {

                        } else {
                            start();
                        }
                        progress++;
                        if (world.rand.nextInt(10) == 0)
                        fluidTank.drain(5,true);
                        markDirty();
                    }
                }
            } else {
                decreaseProgress();
                isCooking = false;
                markDirty();
            }

            //set water block rendering
            if (!world.getBlockState(pos).getValue(BlockPot.FULL) && getWaterLevel() > 0)
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockPot.FULL, true), 3);
            //set water block gone
            if (world.getBlockState(pos).getValue(BlockPot.FULL) && getWaterLevel() == 0)
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockPot.FULL, false), 3);
        }
    }

    public void start() {
        isCooking = true;
    }

    public void decreaseProgress() {
        if (progress > 0) {
            progress--;
        }
    }

    public boolean canRun() {
        if (getWaterLevel() <= 0) {
            return false;
        }
        ItemStack input = getInput();
        ItemStack output = getOutput();
        if (!input.isEmpty()) {
            PotRecipe potRecipe = getOrCreateRecipe();
            if (potRecipe == null) {
                return false;
            }

            if (output.isEmpty()) {
                return true;
            }

            ItemStack result = potRecipe.getResult(getInventory());
            return getInventory().insertItem(RESULT, result, true).isEmpty();
        } else {
            return false;
        }
    }

    public PotRecipe getOrCreateRecipe() {
        if (cached != null && cached.itemMatch(getInventory())) {
            return cached;
        }
        return cached = (PotRecipe) Recipes.findRecipe(getInventory(), "pot");
    }

    public void process() {
        progress = 0;
        if (getInput().getCount() > 0) {
            if (getOutput().getCount() < getOutput().getMaxStackSize()) {
                ItemStack result = Recipes.getCookingResult(getInventory(), "pot");
                if (getOutput().isEmpty()) {
                    setResult(result.copy());
                } else {
                    getOutput().grow(result.getCount());
                }
                getInput().shrink(1);
            }
        }
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return Recipes.hasResult(stack, "pot");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        fluidTank.writeToNBT(compound);
        return compound;
    }

    @Override
    public int getCookTime() {
        return 125;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        fluidTank.readFromNBT(compound);
    }
}
