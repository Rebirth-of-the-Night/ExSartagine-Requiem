package subaraki.exsartagine.gui.client.screen;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import subaraki.exsartagine.tileentity.TileEntityPot;

public class GuiCauldron extends GuiPot {
    public GuiCauldron(final EntityPlayer player, final TileEntityPot pot) {
        super(player, pot);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = I18n.format("cauldron.gui");
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
}
