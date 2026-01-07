package subaraki.exsartagine.tileentity.util;

import subaraki.exsartagine.recipe.CustomRecipe;

import javax.annotation.Nullable;

public interface RecipeHost<R extends CustomRecipe<?>> {

    boolean isRemote();

    default int getRecipeTimeScale() {
        return 1;
    }

    default int getRecipeTimeIncrement() {
        return 1;
    }

    default int getRecipeTimeDecay() {
        return 1;
    }

    @Nullable
    R findRecipe();

    boolean doesRecipeMatch(R recipe);

    boolean canDoWork(R recipe);

    boolean canFitOutputs(R recipe);

    default void onWorkTick(R recipe, int work) {
        // NO-OP
    }

    default boolean canFinishRecipe(R recipe) {
        return true;
    }

    void processRecipe(R recipe);

}
