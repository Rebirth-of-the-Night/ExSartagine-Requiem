package subaraki.exsartagine.gui.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import subaraki.exsartagine.gui.common.KettleContainer;
import subaraki.exsartagine.tileentity.KettleBlockEntity;
import subaraki.exsartagine.util.Reference;

public class KettleScreen extends GuiContainer {

    private static final ResourceLocation GUI_POT = new ResourceLocation(Reference.MODID, "textures/gui/kettle.png");

    private final InventoryPlayer playerInventory;
    private final KettleBlockEntity pot;

    public KettleScreen(EntityPlayer player, KettleBlockEntity pot) {
        super(new KettleContainer(player.inventory, pot));

        playerInventory = player.inventory;
        this.pot = pot;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format("kettle.gui");
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }


    private float fade = 0.2f;

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        this.mc.getTextureManager().bindTexture(GUI_POT);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        float progress = 25 * (float) pot.getProgress() / pot.getCookTime();
        this.drawTexturedModalRect(i + 80, j + 34, 0, 166, (int) progress, 15); //Arrow

        //Draw fluid
        FluidStack fluid = pot.fluidTank.getFluid();
        if (fluid != null) {
            TextureAtlasSprite fluidTexture = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill().toString());
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int fluidHeight = (int)(52 * (double)fluid.amount/10000);
            int yPos = 34;
            drawTexturedModalRect(4 + guiLeft, yPos + guiTop + (yPos - fluidHeight) + 1, fluidTexture, 7, fluidHeight);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
