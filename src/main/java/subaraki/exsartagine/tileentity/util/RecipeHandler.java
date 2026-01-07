package subaraki.exsartagine.tileentity.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import subaraki.exsartagine.recipe.CustomRecipe;

import javax.annotation.Nullable;

public class RecipeHandler<R extends CustomRecipe<?>> {

    private final RecipeHost<R> host;

    @Nullable
    private R runningRecipe = null;
    private int progress = 0;
    private boolean expectRecipeChange = false;

    public RecipeHandler(RecipeHost<R> host) {
        this.host = host;
    }

    @Nullable
    public R getRunningRecipe() {
        return runningRecipe;
    }

    public boolean isWorking() {
        return runningRecipe != null && host.canDoWork(runningRecipe);
    }

    public float getProgressFraction() {
        return runningRecipe != null
                ? MathHelper.clamp(progress / (float) (runningRecipe.getCookTime() * host.getRecipeTimeScale()), 0F, 1F)
                : 0F;
    }

    public boolean tick() {
        boolean didWork = false;

        if (runningRecipe != null && !host.doesRecipeMatch(runningRecipe)) {
            runningRecipe = null;
            if (!expectRecipeChange) {
                progress = 0;
            }
            didWork = true;
        }
        expectRecipeChange = false;

        if (runningRecipe == null) {
            runningRecipe = host.findRecipe();
            if (runningRecipe == null) {
                return didWork;
            }
            didWork = true;
        }

        if (host.isRemote()) {
            return didWork;
        }

        if (!host.canDoWork(runningRecipe)) {
            if (progress > 0) {
                progress = Math.max(progress - host.getRecipeTimeDecay(), 0);
                return true;
            }
            return didWork;
        }

        if (progress <= 0 && !host.canFitOutputs(runningRecipe)) {
            return didWork;
        }

        int maxProgress = runningRecipe.getCookTime() * host.getRecipeTimeScale();
        if (progress < maxProgress) {
            progress = Math.min(progress + host.getRecipeTimeIncrement(), maxProgress);
            host.onWorkTick(runningRecipe, progress);
            if (progress < maxProgress) {
                return true;
            }
        }

        if (!host.canFitOutputs(runningRecipe) || !host.canFinishRecipe(runningRecipe)) {
            return didWork;
        }
        progress = 0;
        host.processRecipe(runningRecipe);
        return true;
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("progress", progress);
    }

    public void readFromNBT(NBTTagCompound tag) {
        progress = tag.getInteger("progress");
        expectRecipeChange = true;
    }

}
