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
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandler;
import subaraki.exsartagine.tileentity.TileEntityCuttingBoard;

public class CuttingBoardRenderer extends TileEntitySpecialRenderer<TileEntityCuttingBoard> {
    @Override
    public void render(TileEntityCuttingBoard te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.0625, z + 0.5);

        BlockPos pos = te.getPos();
        IBlockState state = te.getWorld().getBlockState(pos);
        Comparable<?> dir = state.getProperties().get(BlockHorizontal.FACING);
        if (dir instanceof EnumFacing) {
            switch ((EnumFacing) dir) {
                case EAST:
                    GlStateManager.rotate(270f, 0f, 1f, 0f);
                    break;
                case SOUTH:
                    GlStateManager.rotate(180f, 0f, 1f, 0f);
                    break;
                case WEST:
                    GlStateManager.rotate(90f, 0f, 1f, 0f);
                    break;
            }
        }

        GlStateManager.rotate(90f, 1f, 0f, 0f);
        RenderHelper.enableStandardItemLighting();

        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        IItemHandler inv = te.getInventory();
        ItemStack knife = inv.getStackInSlot(TileEntityCuttingBoard.KNIFE);
        if (!knife.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.25f, -0.025f, -0.12f);
            GlStateManager.rotate(120f, 0.57735f, -0.57735f, -0.57735f);
            GlStateManager.scale(0.75f, 0.75f, 0.75f);
            ri.renderItem(knife, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }

        ItemStack ing = inv.getStackInSlot(TileEntityCuttingBoard.INPUT);
        if (!ing.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(12f, 0f, 0f, 1f);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.19f, -0.095f, -0.016125f);
            GlStateManager.scale(0.2f, 0.2f, 0.2f);
            ri.renderItem(ing, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();

            int maxStackSize = ing.getMaxStackSize();
            int count = MathHelper.clamp(
                    maxStackSize <= 15 ? ing.getCount() - 1 : (int) Math.floor((15f * ing.getCount()) / ing.getMaxStackSize()) - 1,
                    0, 14); // sanity check for weird stack sizes
            for (int i = 0; i < count; i++) {
                int m = i % 2;
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.15f - 0.04f * i, -0.08f - 0.03f * m, -0.02f);
                GlStateManager.rotate(12f, -0.5f + m, 1f, 0f);
                GlStateManager.scale(0.2f, 0.2f, 0.2f);
                ri.renderItem(ing, ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }
}
