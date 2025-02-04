package subaraki.exsartagine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

//borrowed from tinker's construct https://github.com/SlimeKnights/TinkersConstruct/blob/1.12/src/main/java/slimeknights/tconstruct/library/client/RenderUtil.java
public class RenderUtil {

    private RenderUtil() {
    }

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Renders a fluid block, call from TESR. x/y/z is the rendering offset.
     *
     * @param fluid Fluid to render
     * @param pos   BlockPos where the Block is rendered. Used for brightness.
     * @param x     Rendering offset. TESR x parameter.
     * @param y     Rendering offset. TESR x parameter.
     * @param z     Rendering offset. TESR x parameter.
     * @param w     Width. 1 = full X-Width
     * @param h     Height. 1 = full Y-Height
     * @param d     Depth. 1 = full Z-Depth
     */
    public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double w, double h, double d) {
        double wd = (1d - w) / 2d;
        double hd = (1d - h) / 2d;
        double dd = (1d - d) / 2d;

        renderFluidCuboid(fluid, pos, x, y, z, wd, hd, dd, 1d - wd, 1d - hd, 1d - dd);
    }

    public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
        int color = fluid.getFluid().getColor(fluid);
        renderFluidCuboid(fluid, pos, x, y, z, x1, y1, z1, x2, y2, z2, color);
    }

    /**
     * Renders block with offset x/y/z from x1/y1/z1 to x2/y2/z2 inside the block local coordinates, so from 0-1
     */
    public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        //RenderUtil.setColorRGBA(color);
        int brightness = mc.world.getCombinedLight(pos, fluid.getFluid().getLuminosity());
        boolean upsideDown = fluid.getFluid().isGaseous(fluid);

        pre(x, y, z);

        TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
        TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

        // x/y/z2 - x/y/z1 is because we need the width/height/depth
        putATexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness, false, upsideDown);
        putATexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness, true, upsideDown);
        putATexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness, true, upsideDown);
        putATexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness, true, upsideDown);
        putATexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness, true, upsideDown);
        putATexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness, false, upsideDown);

        tessellator.draw();

        post();
    }

    public static void putATexturedQuad(BufferBuilder renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face,
                                        int color, int brightness, boolean flowing, boolean flipHorizontally) {
        int l1 = brightness >> 0x10 & 0xFFFF;
        int l2 = brightness & 0xFFFF;

        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;

        putATexturedQuad(renderer, sprite, x, y, z, w, h, d, face, r, g, b, a, l1, l2, flowing, flipHorizontally);
    }

    // x and x+w has to be within [0,1], same for y/h and z/d
    public static void putATexturedQuad(BufferBuilder renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face,
                                        int r, int g, int b, int a, int light1, int light2, boolean flowing, boolean flipHorizontally) {
        // safety
        if (sprite == null) {
            return;
        }
        double minU;
        double maxU;
        double minV;
        double maxV;

        double size = 16f;
        if (flowing) {
            size = 8f;
        }

        double x1 = x;
        double x2 = x + w;
        double y1 = y;
        double y2 = y + h;
        double z1 = z;
        double z2 = z + d;

        double xt1 = x1 % 1d;
        double xt2 = xt1 + w;
        while (xt2 > 1f) xt2 -= 1f;
        double yt1 = y1 % 1d;
        double yt2 = yt1 + h;
        while (yt2 > 1f) yt2 -= 1f;
        double zt1 = z1 % 1d;
        double zt2 = zt1 + d;
        while (zt2 > 1f) zt2 -= 1f;

        // flowing stuff should start from the bottom, not from the start
        if (flowing) {
            double tmp = 1d - yt1;
            yt1 = 1d - yt2;
            yt2 = tmp;
        }

        switch (face) {
            case DOWN:
            case UP:
                minU = sprite.getInterpolatedU(xt1 * size);
                maxU = sprite.getInterpolatedU(xt2 * size);
                minV = sprite.getInterpolatedV(zt1 * size);
                maxV = sprite.getInterpolatedV(zt2 * size);
                break;
            case NORTH:
            case SOUTH:
                minU = sprite.getInterpolatedU(xt2 * size);
                maxU = sprite.getInterpolatedU(xt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            case WEST:
            case EAST:
                minU = sprite.getInterpolatedU(zt2 * size);
                maxU = sprite.getInterpolatedU(zt1 * size);
                minV = sprite.getInterpolatedV(yt1 * size);
                maxV = sprite.getInterpolatedV(yt2 * size);
                break;
            default:
                minU = sprite.getMinU();
                maxU = sprite.getMaxU();
                minV = sprite.getMinV();
                maxV = sprite.getMaxV();
        }

        if (flipHorizontally) {
            double tmp = minV;
            minV = maxV;
            maxV = tmp;
        }

        switch (face) {
            case DOWN:
                renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                break;
            case UP:
                renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case NORTH:
                renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
            case SOUTH:
                renderer.pos(x1, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case WEST:
                renderer.pos(x1, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x1, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;
            case EAST:
                renderer.pos(x2, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
                renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
        }
    }

    public static void pre(double x, double y, double z) {
        GlStateManager.pushMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        GlStateManager.translate(x, y, z);
    }

    public static void post() {
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }

    public static void renderFluidLevel(FluidStack fluid,double x, double y, double z) {
        int color = fluid.getFluid().getColor(fluid);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int brightness = mc.world.getCombinedLight(new BlockPos(x,y,z), fluid.getFluid().getLuminosity());
        boolean upsideDown = fluid.getFluid().isGaseous(fluid);

        pre(x, y, z);

        double s = .28;

        double wd = (1d - s) / 2d;
        double dd = (1d - s) / 2d;

        double wd2 = 1d - wd;
        double dd2 = 1d - dd;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());


        putATexturedQuad(renderer, still, wd,.5,dd,wd2,.5,dd2, EnumFacing.UP, color, brightness, false, upsideDown);

        tessellator.draw();
        post();
    }

}