package subaraki.exsartagine.tileentity.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import subaraki.exsartagine.RenderUtil;
import subaraki.exsartagine.block.BlockPot;
import subaraki.exsartagine.tileentity.TileEntityCooker;
import subaraki.exsartagine.tileentity.TileEntityPot;

public class CookerRenderer extends TileEntitySpecialRenderer<TileEntityCooker> {

	private EntityItem ei;

	public CookerRenderer() {
	}

	@Override
	public void render(TileEntityCooker tileentity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(tileentity, x, y, z, partialTicks, destroyStage, alpha);

		if(ei == null)
		{
			ei = new EntityItem(getWorld(), 0, 0, 0);
			ei.setInfinitePickupDelay();
			ei.setNoDespawn();
			ei.hoverStart = 0f;
		}

		if(tileentity == null)
			return;
		boolean isPot = tileentity instanceof TileEntityPot;

		ItemStack entryToRender = tileentity.getInput().copy();
		ItemStack resultToRender  = tileentity.getOutput().copy();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z ); //translate to correct location

		GlStateManager.translate(0.1, -0.65, 0); //translate to center of the pan
		if (isPot && ((TileEntityPot)tileentity).getVariant() == BlockPot.Variant.CAULDRON) {
			GlStateManager.translate(0, 0.2, 0);
		}

		GlStateManager.translate(0.5, 0.75, 0.5); //set normal and rotate so it rotates in center, 
		GlStateManager.rotate(90, 0, 0, 1);		  //and is rendered flat down on the pan
		GlStateManager.translate(-0.5, -0.75, -0.5);

		if(!entryToRender.isEmpty())
		{
			entryToRender.setCount(1);
			ei.setItem(entryToRender);
			Minecraft.getMinecraft().getRenderManager().renderEntity(ei, 0.5, 0.4, 0.5, 0F, 0, false);
		} 
		else if (!resultToRender.isEmpty())
		{
			resultToRender.setCount(1);
			ei.setItem(resultToRender);
			Minecraft.getMinecraft().getRenderManager().renderEntity(ei, 0.5, 0.4, 0.5, 0F, 0, false);	
		}
		GlStateManager.popMatrix();

		if (isPot) {
			TileEntityPot pot = (TileEntityPot)tileentity;
			FluidStack fluid = pot.getStoredFluid();
			if (fluid != null) {
				GlStateManager.pushMatrix();
				switch (pot.getVariant()) {
					case POT:
						GlStateManager.translate(-0.25, -0.75, -0.25);
						RenderUtil.renderFluidLevel(fluid, x, y, z, 0, RenderUtil.PlanarShape.QUAD);
						break;
					case CAULDRON:
						GlStateManager.translate(-0.09375, -0.6, -0.09375);
						RenderUtil.renderFluidLevel(fluid, x, y, z, 0.625, RenderUtil.PlanarShape.OCTAGON);
						break;
				}
				GlStateManager.popMatrix();
			}
		}
	}
}
