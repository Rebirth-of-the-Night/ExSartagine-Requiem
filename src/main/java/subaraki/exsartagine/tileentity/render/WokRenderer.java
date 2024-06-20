package subaraki.exsartagine.tileentity.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.RenderUtil;
import subaraki.exsartagine.tileentity.WokBlockEntity;

public class WokRenderer extends TileEntitySpecialRenderer<WokBlockEntity> {

    public WokRenderer() {
    }

    @Override
    public void render(WokBlockEntity wokBlockEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(wokBlockEntity, x, y, z, partialTicks, destroyStage, alpha);

        renderItems(wokBlockEntity.getInventoryInput(), x,y,z,wokBlockEntity.rotation);
        renderItems(wokBlockEntity.getInventoryOutput(), x,y,z, wokBlockEntity.rotation);

        renderFluid(wokBlockEntity.getFluidInventoryInput().getFluid(),x,y,z);
    }

    public void renderFluid(FluidStack stack,double x, double y, double z) {
        if (stack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-.2,-.85,-.2);
            RenderUtil.renderFluidLevel(stack, x, y, z, .28, RenderUtil.PlanarShape.QUAD);
            GlStateManager.popMatrix();
        }
    }

    public <T extends IItemHandler> void renderItems(T handler, double x, double y, double z,double rot) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5F, y+.15, z + 0.5F);
                GlStateManager.rotate(90, 1, 0, 0);      //and is rendered flat down on the pan

                GlStateManager.rotate((float) rot, 0, 0, 1);      //random rotation

                Minecraft.getMinecraft().getItemRenderer().renderItem(Minecraft.getMinecraft().player,stack, ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.popMatrix();
            }
        }
    }
}
