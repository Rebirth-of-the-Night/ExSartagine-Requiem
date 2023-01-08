package subaraki.exsartagine.recipe;

import net.minecraftforge.items.IItemHandler;

@FunctionalInterface
public interface IRecipeType<R extends CustomRecipe<?>> {

    String name();

    static <I extends IItemHandler,R extends CustomRecipe<I>> IRecipeType<R> create(String s) {
     return () -> s;
    }
}
