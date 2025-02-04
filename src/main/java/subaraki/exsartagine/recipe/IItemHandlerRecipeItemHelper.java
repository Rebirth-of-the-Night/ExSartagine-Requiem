package subaraki.exsartagine.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;

public class IItemHandlerRecipeItemHelper {


    /** Map from {@link #pack} packed ids to counts */
    public final Int2IntMap itemToCount = new Int2IntOpenHashMap();

    public void accountStack(ItemStack stack)
    {
        this.accountStack(stack, -1);
    }

    public void accountStack(ItemStack stack, int forceCount)
    {
        if (!stack.isEmpty() && !stack.isItemDamaged() && !stack.isItemEnchanted() && !stack.hasDisplayName())
        {
            int i = pack(stack);
            int j = forceCount == -1 ? stack.getCount() : forceCount;
            this.increment(i, j);
        }
    }

    public static int pack(ItemStack stack)
    {
        Item item = stack.getItem();
        int i = item.getHasSubtypes() ? stack.getMetadata() : 0;
        return Item.REGISTRY.getIDForObject(item) << 16 | i & 65535;
    }

    public boolean containsItem(int packedItem)
    {
        return this.itemToCount.get(packedItem) > 0;
    }

    public int tryTake(int packedItem, int maximum)
    {
        int i = this.itemToCount.get(packedItem);

        if (i >= maximum)
        {
            this.itemToCount.put(packedItem, i - maximum);
            return packedItem;
        }
        else
        {
            return 0;
        }
    }

    private void increment(int packedItem, int amount)
    {
        this.itemToCount.put(packedItem, this.itemToCount.get(packedItem) + amount);
    }

    public boolean canCraft(CustomRecipe<?> recipe, @Nullable IntList packedItemList)
    {
        return this.canCraft(recipe, packedItemList, 1);
    }

    public boolean canCraft(CustomRecipe<?> recipe, @Nullable IntList packedItemList, int maxAmount)
    {
        return (new IItemHandlerRecipeItemHelper.RecipePicker(recipe)).tryPick(maxAmount, packedItemList);
    }

    public static ItemStack unpack(int packedItem)
    {
        return packedItem == 0 ? ItemStack.EMPTY : new ItemStack(Item.getItemById(packedItem >> 16 & 65535), 1, packedItem & 65535);
    }

    public void clear()
    {
        this.itemToCount.clear();
    }

    class RecipePicker
    {
        private final CustomRecipe<?> recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] possessedIngredientStacks;
        private final int possessedIngredientStackCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public RecipePicker(CustomRecipe<?> recipeIn)
        {
            this.recipe = recipeIn;
            this.ingredients.addAll(recipeIn.getIngredients());
            this.ingredients.removeIf((p_194103_0_) ->
            {
                return p_194103_0_ == Ingredient.EMPTY;
            });
            this.ingredientCount = this.ingredients.size();
            this.possessedIngredientStacks = this.getUniqueAvailIngredientItems();
            this.possessedIngredientStackCount = this.possessedIngredientStacks.length;
            this.data = new BitSet(this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount + this.ingredientCount * this.possessedIngredientStackCount);

            for (int i = 0; i < this.ingredients.size(); ++i)
            {
                IntList intlist = this.ingredients.get(i).getValidItemStacksPacked();

                for (int j = 0; j < this.possessedIngredientStackCount; ++j)
                {
                    if (intlist.contains(this.possessedIngredientStacks[j]))
                    {
                        this.data.set(this.getIndex(true, j, i));
                    }
                }
            }
        }

        public boolean tryPick(int maxAmount, @Nullable IntList listIn)
        {
            if (maxAmount <= 0)
            {
                return true;
            }
            else
            {
                int k;

                for (k = 0; this.dfs(maxAmount); ++k)
                {
                    IItemHandlerRecipeItemHelper.this.tryTake(this.possessedIngredientStacks[this.path.getInt(0)], maxAmount);
                    int l = this.path.size() - 1;
                    this.setSatisfied(this.path.getInt(l));

                    for (int i1 = 0; i1 < l; ++i1)
                    {
                        this.toggleResidual((i1 & 1) == 0, this.path.get(i1), this.path.get(i1 + 1));
                    }

                    this.path.clear();
                    this.data.clear(0, this.ingredientCount + this.possessedIngredientStackCount);
                }

                boolean flag = k == this.ingredientCount;
                boolean flag1 = flag && listIn != null;

                if (flag1)
                {
                    listIn.clear();
                }

                this.data.clear(0, this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount);
                int j1 = 0;
                List<Ingredient> list = this.recipe.getIngredients();

                for (int k1 = 0; k1 < list.size(); ++k1)
                {
                    if (flag1 && list.get(k1) == Ingredient.EMPTY)
                    {
                        listIn.add(0);
                    }
                    else
                    {
                        for (int l1 = 0; l1 < this.possessedIngredientStackCount; ++l1)
                        {
                            if (this.hasResidual(false, j1, l1))
                            {
                                this.toggleResidual(true, l1, j1);
                                IItemHandlerRecipeItemHelper.this.increment(this.possessedIngredientStacks[l1], maxAmount);

                                if (flag1)
                                {
                                    listIn.add(this.possessedIngredientStacks[l1]);
                                }
                            }
                        }

                        ++j1;
                    }
                }

                return flag;
            }
        }

        private int[] getUniqueAvailIngredientItems()
        {
            IntCollection intcollection = new IntAVLTreeSet();

            for (Ingredient ingredient : this.ingredients)
            {
                intcollection.addAll(ingredient.getValidItemStacksPacked());
            }

            IntIterator intiterator = intcollection.iterator();

            while (intiterator.hasNext())
            {
                if (!IItemHandlerRecipeItemHelper.this.containsItem(intiterator.nextInt()))
                {
                    intiterator.remove();
                }
            }

            return intcollection.toIntArray();
        }

        private boolean dfs(int amount)
        {
            int k = this.possessedIngredientStackCount;

            for (int l = 0; l < k; ++l)
            {
                if (IItemHandlerRecipeItemHelper.this.itemToCount.get(this.possessedIngredientStacks[l]) >= amount)
                {
                    this.visit(false, l);

                    while (!this.path.isEmpty())
                    {
                        int i1 = this.path.size();
                        boolean flag = (i1 & 1) == 1;
                        int j1 = this.path.getInt(i1 - 1);

                        if (!flag && !this.isSatisfied(j1))
                        {
                            break;
                        }

                        int k1 = flag ? this.ingredientCount : k;

                        for (int l1 = 0; l1 < k1; ++l1)
                        {
                            if (!this.hasVisited(flag, l1) && this.hasConnection(flag, j1, l1) && this.hasResidual(flag, j1, l1))
                            {
                                this.visit(flag, l1);
                                break;
                            }
                        }

                        int i2 = this.path.size();

                        if (i2 == i1)
                        {
                            this.path.removeInt(i2 - 1);
                        }
                    }

                    if (!this.path.isEmpty())
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean isSatisfied(int p_194091_1_)
        {
            return this.data.get(this.getSatisfiedIndex(p_194091_1_));
        }

        private void setSatisfied(int p_194096_1_)
        {
            this.data.set(this.getSatisfiedIndex(p_194096_1_));
        }

        private int getSatisfiedIndex(int p_194094_1_)
        {
            return this.ingredientCount + this.possessedIngredientStackCount + p_194094_1_;
        }

        private boolean hasConnection(boolean p_194093_1_, int p_194093_2_, int p_194093_3_)
        {
            return this.data.get(this.getIndex(p_194093_1_, p_194093_2_, p_194093_3_));
        }

        private boolean hasResidual(boolean p_194100_1_, int p_194100_2_, int p_194100_3_)
        {
            return p_194100_1_ != this.data.get(1 + this.getIndex(p_194100_1_, p_194100_2_, p_194100_3_));
        }

        private void toggleResidual(boolean p_194089_1_, int p_194089_2_, int p_194089_3_)
        {
            this.data.flip(1 + this.getIndex(p_194089_1_, p_194089_2_, p_194089_3_));
        }

        private int getIndex(boolean p_194095_1_, int p_194095_2_, int p_194095_3_)
        {
            int k = p_194095_1_ ? p_194095_2_ * this.ingredientCount + p_194095_3_ : p_194095_3_ * this.ingredientCount + p_194095_2_;
            return this.ingredientCount + this.possessedIngredientStackCount + this.ingredientCount + 2 * k;
        }

        private void visit(boolean p_194088_1_, int p_194088_2_)
        {
            this.data.set(this.getVisitedIndex(p_194088_1_, p_194088_2_));
            this.path.add(p_194088_2_);
        }

        private boolean hasVisited(boolean p_194101_1_, int p_194101_2_)
        {
            return this.data.get(this.getVisitedIndex(p_194101_1_, p_194101_2_));
        }

        private int getVisitedIndex(boolean p_194099_1_, int p_194099_2_)
        {
            return (p_194099_1_ ? 0 : this.ingredientCount) + p_194099_2_;
        }

        public int tryPickAll(int p_194102_1_, @Nullable IntList list)
        {
            int k = 0;
            int l = Math.min(p_194102_1_, this.getMinIngredientCount()) + 1;

            while (true)
            {
                int i1 = (k + l) / 2;

                if (this.tryPick(i1, null))
                {
                    if (l - k <= 1)
                    {
                        if (i1 > 0)
                        {
                            this.tryPick(i1, list);
                        }

                        return i1;
                    }

                    k = i1;
                }
                else
                {
                    l = i1;
                }
            }
        }

        private int getMinIngredientCount()
        {
            int k = Integer.MAX_VALUE;

            for (Ingredient ingredient : this.ingredients)
            {
                int l = 0;
                int i1;

                for (IntListIterator intlistiterator = ingredient.getValidItemStacksPacked().iterator(); intlistiterator.hasNext(); l = Math.max(l, IItemHandlerRecipeItemHelper.this.itemToCount.get(i1)))
                {
                    i1 = intlistiterator.next();
                }

                if (k > 0)
                {
                    k = Math.min(k, l);
                }
            }

            return k;
        }
    }
}
