package subaraki.exsartagine.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.tileentity.util.KitchenwareBlockEntity;
import subaraki.exsartagine.util.Helpers;

import java.util.Arrays;

public class GuiHelpers {

    public static final ResourceLocation DIRTY_TEXTURE = new ResourceLocation(ExSartagine.MODID, "textures/gui/dirty.png");

    public static boolean isPointInRect(int pointX, int pointY, int rectX, int rectY, int rectWidth, int rectHeight) {
        return pointX >= rectX && pointX < rectX + rectWidth && pointY >= rectY && pointY < rectY + rectHeight;
    }

    public static void drawDirtyIcon(Minecraft mc, KitchenwareBlockEntity<?> tile, int x, int y) {
        if (tile.isSoiled()) {
            mc.getTextureManager().bindTexture(DIRTY_TEXTURE);
            GlStateManager.enableBlend();
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
        }
    }

    public static boolean drawDirtyTooltip(GuiScreen gui, KitchenwareBlockEntity<?> tile, int x, int y, int mx, int my) {
        if (!tile.isSoiled() || !isPointInRect(mx, my, x, y, 16, 16)) {
            return false;
        }
        int dirtyTime = tile.getSoiledTime();
        if (dirtyTime > 0) {
            gui.drawHoveringText(Arrays.asList(
                    TextFormatting.RED + I18n.format(ExSartagine.MODID + ".gui.dirty"),
                    TextFormatting.GRAY + "(" + Helpers.formatTime(dirtyTime) + ")"
            ), mx, my);
        } else {
            gui.drawHoveringText(TextFormatting.RED + I18n.format(ExSartagine.MODID + ".gui.dirty"), mx, my);
        }
        return true;
    }

}
