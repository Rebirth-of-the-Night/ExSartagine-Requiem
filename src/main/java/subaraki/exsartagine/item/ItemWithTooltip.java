package subaraki.exsartagine.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemWithTooltip extends Item {
    private final String[] tooltipKeys;
    
    public ItemWithTooltip(String... tooltipKeys) {
        this.tooltipKeys = tooltipKeys;
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flags) {
        for (String key : tooltipKeys) {
            tooltip.add(I18n.format(key));
        }
    }
}
