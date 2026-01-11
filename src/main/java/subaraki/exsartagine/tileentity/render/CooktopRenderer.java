package subaraki.exsartagine.tileentity.render;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import subaraki.exsartagine.tileentity.TileEntityCooktop;

public class CooktopRenderer extends TileEntitySpecialRenderer<TileEntityCooktop> {
    @Override
    public void render(TileEntityCooktop te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockPos pos = te.getPos();
        IBlockState state = te.getWorld().getBlockState(pos);

        Comparable<?> dir = state.getProperties().get(BlockHorizontal.FACING);
        float rot;
        if (dir instanceof EnumFacing) {
            switch ((EnumFacing) dir) {
                case EAST:
                    rot = 90f;
                    break;
                case SOUTH:
                    rot = 180f;
                    break;
                case WEST:
                    rot = 270f;
                    break;
                default:
                    rot = 0f;
                    break;
            }
        } else {
            rot = 0f;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 1, z);
        GlStateManager.rotate(90f, 1f, 0f, 0f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
        RenderHelper.enableStandardItemLighting();

        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        renderSlot(ri, te, 0, 0, 0, rot);
        renderSlot(ri, te, 1, 1, 0, rot);
        renderSlot(ri, te, 2, 0, 1, rot);
        renderSlot(ri, te, 3, 1, 1, rot);

        GlStateManager.popMatrix();
    }

    private static void renderSlot(RenderItem ri, TileEntityCooktop te, int slot, int dx, int dz, float rot) {
        ItemStack stack = te.getCooktopInventory().getStackInSlot(slot);
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.635f + dx * 1.23f, 0.635f + dz * 1.23f, -0.03125f);
        GlStateManager.rotate(rot, 0f, 0f, 1f);
        ri.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }
}
