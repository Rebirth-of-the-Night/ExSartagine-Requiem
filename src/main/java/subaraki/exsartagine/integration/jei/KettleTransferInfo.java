package subaraki.exsartagine.integration.jei;


import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.Slot;
import subaraki.exsartagine.gui.common.ContainerKettle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KettleTransferInfo implements IRecipeTransferInfo<ContainerKettle> {

    /**
     * Return the container class that this recipe transfer helper supports.
     */
    @Nonnull
    @Override
    public Class<ContainerKettle> getContainerClass() {
        return ContainerKettle.class;
    }

    /**
     * Return the recipe category that this container can handle.
     */
    @Nonnull
    @Override
    public String getRecipeCategoryUid() {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    /**
     * Return true if this recipe transfer info can handle the given container instance.
     *
     * @param container the container
     * @since JEI 4.0.2
     */
    @Override
    public boolean canHandle(@Nonnull ContainerKettle container) {
        return true;
    }

    /**
     * Return a list of slots for the recipe area.
     *
     * @param container the container
     */
    @Nonnull
    @Override
    public List<Slot> getRecipeSlots(@Nonnull ContainerKettle container) {
        return IntStream.range(0, 10).mapToObj(container::getSlot).collect(Collectors.toList());
    }

    /**
     * Return a list of slots that the transfer can use to get items for crafting, or place leftover items.
     *
     * @param container the container
     */
    @Nonnull
    @Override
    public List<Slot> getInventorySlots(@Nonnull ContainerKettle container) {
        return IntStream.range(10 + 9, container.inventorySlots.size()).mapToObj(container::getSlot).collect(Collectors.toList());
    }
}
