package subaraki.exsartagine.recipe;

import net.minecraftforge.items.IItemHandler;

@FunctionalInterface
public interface IRecipeType<T extends IItemHandler> {

    String name();

    static <T extends IItemHandler,U extends CustomRecipe<T>> IRecipeType<T> create(String s) {
     return () -> s;
    }
}
