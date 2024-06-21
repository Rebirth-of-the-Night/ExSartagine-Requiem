package subaraki.exsartagine.recipe;

public interface DirtyingRecipe {

    /**
     * When greater than zero, the recipe dirties the cookware; when less than zero, the recipe cleans it.
     */
    int getDirtyTime();

}
