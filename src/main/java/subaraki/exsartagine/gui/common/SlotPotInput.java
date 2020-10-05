package subaraki.exsartagine.gui.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import subaraki.exsartagine.recipe.Recipes;

public class SlotPotInput extends SlotItemHandler {

	public SlotPotInput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if(!stack.isEmpty())
		{
			return Recipes.hasResult(stack,getItemHandler(),"pot");
		}
		return false;
	}
}