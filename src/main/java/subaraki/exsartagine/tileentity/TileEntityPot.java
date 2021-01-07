package subaraki.exsartagine.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.exsartagine.block.BlockPot;
import subaraki.exsartagine.recipe.KettleRecipe;
import subaraki.exsartagine.recipe.PotRecipe;
import subaraki.exsartagine.recipe.Recipes;

public class TileEntityPot extends TileEntityCooker {

    /**
     * max 192 , value of 3 stacks. one bucket = 192
     */
    private final int cookTime = 125;
    private int waterLevel = 0;
    public PotRecipe cached;

    public TileEntityPot() {
        initInventory();
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void replenishWater() {
        this.waterLevel = 192;
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
                        waterLevel--;
                        markDirty();
                    }
                }
            } else {
                decreaseProgress();
                isCooking = false;
                markDirty();
            }

            //set water block rendering
            if (!world.getBlockState(pos).getValue(BlockPot.FULL) && waterLevel > 0)
                world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockPot.FULL, true), 3);
            //set water block gone
            if (world.getBlockState(pos).getValue(BlockPot.FULL) && waterLevel == 0)
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
        if (waterLevel <= 0) {
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
            if (getOutput().isEmpty() || getOutput().getCount() < getOutput().getMaxStackSize()) {
                if (getOutput().isEmpty()) {
                    ItemStack stack = Recipes.getCookingResult(getInventory(), "pot");

                    if (getInput().getItem() instanceof ItemBlock && getInput().getItem() == Item.getItemFromBlock(Blocks.STONE)) {
                        stack = world.rand.nextInt(5) == 0 ? ItemStack.EMPTY : stack;
                    }

                    setResult(stack.copy());
                } else {
                    if (getInput().getItem() instanceof ItemBlock && getInput().getItem() == Item.getItemFromBlock(Blocks.STONE)) {
                        getOutput().grow(world.rand.nextInt(5) == 0 ? 1 : 0);
                    } else
                        getOutput().grow(1);

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
        compound.setInteger("water", waterLevel);
        return compound;
    }

    @Override
    public int getCookTime() {
        return 125;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.waterLevel = compound.getInteger("water");
    }
}
