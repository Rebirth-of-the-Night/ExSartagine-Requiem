package subaraki.exsartagine.recipe;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.items.IItemHandler;

import java.util.Collection;
import java.util.Collections;

@FunctionalInterface
public interface IRecipeType<R extends CustomRecipe<?>> {

    String name();

    default Collection<IRecipeType<? super R>> getParents() {
        return Collections.emptyList();
    }

    static <I extends IItemHandler,R extends CustomRecipe<I>> IRecipeType<R> create(String s) {
        return () -> s;
    }

    static <I extends IItemHandler,R extends CustomRecipe<I>> IRecipeType<R> inherit(String name, IRecipeType<? super R>... parents) {
        Collection<IRecipeType<? super R>> parentList = ImmutableList.copyOf(parents);
        return new IRecipeType<R>() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public Collection<IRecipeType<? super R>> getParents() {
                return parentList;
            }
        };
    }
}
