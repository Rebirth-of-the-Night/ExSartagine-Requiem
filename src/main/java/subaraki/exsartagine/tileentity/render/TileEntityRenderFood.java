package subaraki.exsartagine.tileentity.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import subaraki.exsartagine.tileentity.TileEntityCooker;

public class TileEntityRenderFood extends TileEntitySpecialRenderer<TileEntityCooker> {

	private EntityItem ei;

	public TileEntityRenderFood() {
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

		ItemStack entryToRender = tileentity.getInput().copy();
		ItemStack resultToRender  = tileentity.getOutput().copy();

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x, (float) y, (float) z ); //translate to correct location

		GlStateManager.translate(0.1, -0.65, 0); //translate to center of the pan

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
	}
}
