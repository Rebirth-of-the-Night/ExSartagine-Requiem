package subaraki.exsartagine.gui.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import subaraki.exsartagine.gui.common.ContainerKettle;
import subaraki.exsartagine.network.PacketHandler;
import subaraki.exsartagine.network.SwapTanksPacket;
import subaraki.exsartagine.tileentity.TileEntityKettle;
import subaraki.exsartagine.util.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KettleScreen extends GuiContainer {

    private static final ResourceLocation GUI_POT = new ResourceLocation(Reference.MODID, "textures/gui/kettle.png");

    private final InventoryPlayer playerInventory;
    private final TileEntityKettle pot;

    public KettleScreen(EntityPlayer player, TileEntityKettle pot) {
        super(new ContainerKettle(player.inventory, pot));

        playerInventory = player.inventory;
        this.pot = pot;
        xSize += 16;
        ySize += 16;
    }

    private static final int BUTTON_ID = 837890435;
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(Reference.MODID,"textures/gui/switch.png");

    @Override
    public void initGui() {
        super.initGui();
        addButton(new GuiButtonImage(BUTTON_ID,guiLeft + 93,guiTop+ 72,20,20,0,0,0,BUTTON_TEXTURE));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == BUTTON_ID) {
            PacketHandler.INSTANCE.sendToServer(new SwapTanksPacket());
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format("kettle.gui");
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        this.mc.getTextureManager().bindTexture(GUI_POT);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        float progress = 25 * (float) pot.getProgress() / pot.getCookTime();
        this.drawTexturedModalRect(i + 91, j + 33, 0, 184, (int) progress, 15); //Arrow

        //Draw fluid
            renderFluid(mc, i + 9, j + 15, pot.fluidInputTank);
            renderFluid(mc, i + 176, j + 15, pot.fluidOutputTank);
    }

    public void renderFluid(Minecraft minecraft, final int xPosition, final int yPosition,FluidTank fluidTank) {
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        drawFluid(minecraft, xPosition, yPosition, fluidTank);

        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    private static final int TEX_WIDTH = 16;
    private static final int TEX_HEIGHT = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private void drawFluid(Minecraft minecraft, final int xPosition, final int yPosition, FluidTank fluidTank) {
        FluidStack fluidStack = fluidTank.getFluid();
        if (fluidStack == null) {
            return;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(minecraft, fluid);

        int fluidColor = fluid.getColor(fluidStack);

        int scaledAmount = (fluidStack.amount * 52) / fluidTank.getCapacity();
        if (fluidStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(minecraft, xPosition, yPosition, 7, 52, fluidColor, scaledAmount, fluidStillSprite);
    }

    private void drawTiledSprite(Minecraft minecraft, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * TEX_WIDTH);
        final int yTileCount = scaledAmount / TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * TEX_HEIGHT);

        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEX_WIDTH;
                int height = (yTile == yTileCount) ? yRemainder : TEX_HEIGHT;
                int x = xPosition + (xTile * TEX_WIDTH);
                int y = yStart - ((yTile + 1) * TEX_HEIGHT);
                if (width > 0 && height > 0) {
                    int maskTop = TEX_HEIGHT - height;
                    int maskRight = TEX_WIDTH - width;

                    drawTextureWithMasking(x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(Minecraft minecraft, Fluid fluid) {
        TextureMap textureMapBlocks = minecraft.getTextureMapBlocks();
        ResourceLocation fluidStill = fluid.getStill();
        TextureAtlasSprite fluidStillSprite = null;
        if (fluidStill != null) {
            fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
        }
        if (fluidStillSprite == null) {
            fluidStillSprite = textureMapBlocks.getMissingSprite();
        }
        return fluidStillSprite;
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);
    }

    private static void drawTextureWithMasking(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
        double uMin = textureSprite.getMinU();
        double uMax = textureSprite.getMaxU();
        double vMin = textureSprite.getMinV();
        double vMax = textureSprite.getMaxV();
        uMax = uMax - (maskRight / 16.0 * (uMax - uMin));
        vMax = vMax - (maskTop / 16.0 * (vMax - vMin));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferBuilder.pos(xCoord, yCoord + 16, zLevel).tex(uMin, vMax).endVertex();
        bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + 16, zLevel).tex(uMax, vMax).endVertex();
        bufferBuilder.pos(xCoord + 16 - maskRight, yCoord + maskTop, zLevel).tex(uMax, vMin).endVertex();
        bufferBuilder.pos(xCoord, yCoord + maskTop, zLevel).tex(uMin, vMin).endVertex();
        tessellator.draw();
    }

    public List<String> getFluidTooltip(FluidTank fluidTank) {
        List<String> tooltip = new ArrayList<>();
        FluidStack fluidStack = fluidTank.getFluid();
        if (fluidStack == null) {
            return tooltip;
        }

        String fluidName = fluidStack.getLocalizedName();
        tooltip.add(fluidName);

        String amount = I18n.format("jei.tooltip.liquid.amount.with.capacity", fluidStack.amount, fluidTank.getCapacity());
        tooltip.add(TextFormatting.GRAY + amount);
        return tooltip;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void renderHoveredToolTip(int x, int y) {
        super.renderHoveredToolTip(x, y);
        if (isPointInRegion(8, 14, 7, 52, x, y) && pot.fluidInputTank.getFluid() != null) {
            this.drawHoveringText(getFluidTooltip(pot.fluidInputTank), x, y, fontRenderer);
        }
        if (isPointInRegion(176, 14, 7, 52, x, y) && pot.fluidOutputTank.getFluid() != null) {
            this.drawHoveringText(getFluidTooltip(pot.fluidOutputTank), x, y, fontRenderer);
        }
    }
}
