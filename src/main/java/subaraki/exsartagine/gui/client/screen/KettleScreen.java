package subaraki.exsartagine.gui.client.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.RenderUtil;
import subaraki.exsartagine.gui.client.SmallButton;
import subaraki.exsartagine.gui.common.ContainerKettle;
import subaraki.exsartagine.network.ClearTankPacket;
import subaraki.exsartagine.network.PacketHandler;
import subaraki.exsartagine.network.SwapTanksPacket;
import subaraki.exsartagine.tileentity.TileEntityKettle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KettleScreen extends GuiContainer {

    private static final ResourceLocation GUI_POT = new ResourceLocation(ExSartagine.MODID, "textures/gui/kettle.png");

    private final InventoryPlayer playerInventory;
    private final TileEntityKettle kettle;

    public KettleScreen(EntityPlayer player, TileEntityKettle kettle) {
        super(new ContainerKettle(player.inventory, kettle));

        playerInventory = player.inventory;
        this.kettle = kettle;
        xSize += 16;
        ySize += 16;
    }

    private static final int BUTTON_ID = 837890435;
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(ExSartagine.MODID,"textures/gui/switch.png");

    @Override
    public void initGui() {
        super.initGui();
        addButton(new GuiButtonImage(BUTTON_ID,guiLeft + 86,guiTop+ 50,20,20,0,0,0,BUTTON_TEXTURE));
        addButton(new SmallButton(1,guiLeft + 76,guiTop + 69,10,10,""));
        addButton(new SmallButton(2,guiLeft + 107,guiTop + 69,10,10,""));


    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == BUTTON_ID) {
            PacketHandler.INSTANCE.sendToServer(new SwapTanksPacket());
        } else if (button.id == 1) {
            PacketHandler.INSTANCE.sendToServer(new ClearTankPacket(true));
        } else if (button.id == 2) {
            PacketHandler.INSTANCE.sendToServer(new ClearTankPacket(false));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format("kettle.gui");
        this.fontRenderer.drawString(s, 1, 0, 0xffffff);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 10, this.ySize - 93, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        this.mc.getTextureManager().bindTexture(GUI_POT);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        float progress = 22 * kettle.getProgressFraction();
        this.drawTexturedModalRect(i + 85, j + 34, 0, 182, (int) progress, 15); //Arrow

        //Draw fluid
        RenderUtil.renderFluidIntoGui(mc, i + FL_INPUT_X, j + FL_Y - 2, FL_WIDTH, FL_HEIGHT, kettle.fluidInputTank);
        RenderUtil.renderFluidIntoGui(mc, i + FL_OUTPUT_X, j + FL_Y - 2, FL_WIDTH, FL_HEIGHT, kettle.fluidOutputTank);
    }

    private static final int FL_WIDTH = 7;
    private static final int FL_HEIGHT = 52;
    private static final int FL_Y = 18;
    private static final int FL_INPUT_X = 77;
    private static final int FL_OUTPUT_X = 108;


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

    //77,18


    @Override
    protected void renderHoveredToolTip(int x, int y) {
        super.renderHoveredToolTip(x, y);
        if (isPointInRegion(FL_INPUT_X, FL_Y, FL_WIDTH, FL_HEIGHT, x, y) && kettle.fluidInputTank.getFluid() != null) {
            this.drawHoveringText(getFluidTooltip(kettle.fluidInputTank), x, y, fontRenderer);
        }
        if (isPointInRegion(FL_OUTPUT_X, FL_Y, FL_WIDTH, FL_HEIGHT, x, y) && kettle.fluidOutputTank.getFluid() != null) {
            this.drawHoveringText(getFluidTooltip(kettle.fluidOutputTank), x, y, fontRenderer);
        }
    }
}
