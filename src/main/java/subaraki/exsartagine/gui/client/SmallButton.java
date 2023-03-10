package subaraki.exsartagine.gui.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class SmallButton extends GuiButton {

    public SmallButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public SmallButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public void tint() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawButton(Minecraft mc,int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);

            //tint();

             hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            int i = this.getHoverState(this.hovered);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            int halfwidth1 = this.width / 2;
            int halfwidth2 = this.width - halfwidth1;
            int halfheight1 = this.height / 2;
            int halfheight2 = this.height - halfheight1;
            drawTexturedModalRect(x, y, 0,
                    46 + i * 20, halfwidth1, halfheight1);
            drawTexturedModalRect(x + halfwidth1, y, 200 - halfwidth2,
                    46 + i * 20, halfwidth2, halfheight1);

            drawTexturedModalRect(x, y + halfheight1,
                    0, 46 + i * 20 + 20 - halfheight2, halfwidth1, halfheight2);
            drawTexturedModalRect(x + halfwidth1, y + halfheight1,
                    200 - halfwidth2, 46 + i * 20 + 20 - halfheight2, halfwidth2, halfheight2);
        }
    }
}
