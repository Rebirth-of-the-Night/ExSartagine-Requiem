package subaraki.exsartagine.gui.client.screen;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import subaraki.exsartagine.ExSartagine;
import subaraki.exsartagine.RenderUtil;
import subaraki.exsartagine.gui.client.GuiHelpers;
import subaraki.exsartagine.gui.common.ContainerPot;
import subaraki.exsartagine.tileentity.TileEntityPot;

import java.util.Arrays;

public class GuiPot extends GuiContainer {

	private static final ResourceLocation GUI_POT = new ResourceLocation(ExSartagine.MODID,"textures/gui/pot.png");

	protected final InventoryPlayer playerInventory;
	protected final TileEntityPot pot;

	public GuiPot(EntityPlayer player, TileEntityPot pot) {
		super(new ContainerPot(player.inventory, pot));

		playerInventory = player.inventory;
		this.pot = pot;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = I18n.format("pot.gui");
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

		fade +=0.05f;
		if(pot.activeHeatSourceBelow())
		{
			this.drawTexturedModalRect(i+56, j+53, 176, 28, 16, 16); //furnace lit

			GlStateManager.enableBlend();
			GlStateManager.color(1f, 1f, 1f, (float)(Math.cos(Math.sin(fade))));
			this.drawTexturedModalRect(i+57, j+37, 176, 0, 14, 12); //fire
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.disableBlend();

		}
		else
			this.drawTexturedModalRect(i+56, j+53, 176, 12, 16, 16); //furnace out

		float progress = pot.getProgressFraction();
		if (progress > 0F) {
			this.drawTexturedModalRect(i + 76, j + 34, 176, 44, (int)(progress * 33), 18); //Arrow
		}

		RenderUtil.renderFluidIntoGui(mc, i + 14, j + 15, 5, 54, pot.getStoredFluid(), TileEntityPot.TANK_CAPACITY);

		GuiHelpers.drawDirtyIcon(mc, pot, i + 84, j + 56);
	}

	@Override
	protected void renderHoveredToolTip(int x, int y) {
		int i = guiLeft, j = guiTop;
		if (GuiHelpers.isPointInRect(x, y, i + 14, j + 15, 5, 54)) {
			FluidStack fluid = pot.getStoredFluid();
			if (fluid == null || fluid.amount <= 0) {
				drawHoveringText(String.format(TextFormatting.GRAY + "0 / %,d mB", TileEntityPot.TANK_CAPACITY), x, y);
			} else {
				drawHoveringText(Arrays.asList(
								fluid.getLocalizedName(),
								String.format(TextFormatting.GRAY + "%,d / %,d mB", fluid.amount, TileEntityPot.TANK_CAPACITY)),
						x, y);
			}
			return;
		}
		if (GuiHelpers.drawDirtyTooltip(this, pot, i + 84, j + 56, x, y)) {
			return;
		}
		super.renderHoveredToolTip(x, y);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
