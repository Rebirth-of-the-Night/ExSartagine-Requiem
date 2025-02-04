package subaraki.exsartagine;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
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
        putTexturedShape(renderer, still, PlanarShape.QUAD, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness, false, upsideDown);
        putTexturedShape(renderer, flowing, PlanarShape.QUAD, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness, true, upsideDown);
        putTexturedShape(renderer, flowing, PlanarShape.QUAD, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness, true, upsideDown);
        putTexturedShape(renderer, flowing, PlanarShape.QUAD, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness, true, upsideDown);
        putTexturedShape(renderer, flowing, PlanarShape.QUAD, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness, true, upsideDown);
        putTexturedShape(renderer, still, PlanarShape.QUAD, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness, false, upsideDown);

        tessellator.draw();

        post();
    }

    public static void putTexturedShape(BufferBuilder renderer, TextureAtlasSprite sprite, PlanarShape shape,
                                        double x, double y, double z, double w, double h, double d, EnumFacing face,
                                        int color, int brightness, boolean flowing, boolean flipHorizontally) {
        int l1 = brightness >> 0x10 & 0xFFFF;
        int l2 = brightness & 0xFFFF;

        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;

        putTexturedShape(renderer, sprite, shape, x, y, z, w, h, d, face, r, g, b, a, l1, l2, flowing, flipHorizontally);
    }

    // x and x+w has to be within [0,1], same for y/h and z/d
    public static void putTexturedShape(BufferBuilder renderer, TextureAtlasSprite sprite, PlanarShape shape,
                                        double x, double y, double z, double w, double h, double d, EnumFacing face,
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

        for (PV vertex : shape.vertices) {
            vertex.pos(renderer, x1, y1, z1, x2, y2, z2, face);
            renderer.color(r, g, b, a);
            vertex.tex(renderer, minU, minV, maxU, maxV);
            renderer.lightmap(light1, light2).endVertex();
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

    public static void renderFluidLevel(FluidStack fluid, double x, double y, double z, double s, PlanarShape shape) {
        int color = fluid.getFluid().getColor(fluid);

        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        int brightness = mc.world.getCombinedLight(new BlockPos(x,y,z), fluid.getFluid().getLuminosity());
        boolean upsideDown = fluid.getFluid().isGaseous(fluid);

        pre(x, y, z);

        double wd = (1d - s) / 2d;
        double dd = (1d - s) / 2d;

        double wd2 = 1d - wd;
        double dd2 = 1d - dd;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder renderer = tessellator.getBuffer();
        renderer.begin(shape.glMode, DefaultVertexFormats.BLOCK);
        TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());


        putTexturedShape(renderer, still, shape, wd,.5,dd,wd2,.5,dd2, EnumFacing.UP, color, brightness, false, upsideDown);

        tessellator.draw();
        post();
    }

    public static void renderFluidIntoGui(Minecraft minecraft, int xPosition, int yPosition, int width, int height, IFluidTank tank) {
        renderFluidIntoGui(minecraft, xPosition, yPosition, width, height, tank.getFluid(), tank.getCapacity());
    }

    public static void renderFluidIntoGui(Minecraft minecraft, int xPosition, int yPosition, int width, int height, FluidStack fluidStack, int capacity) {
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        drawFluid(minecraft, xPosition, yPosition, width, height, fluidStack, capacity);

        GlStateManager.color(1, 1, 1, 1);

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    private static final int FLUID_TEX_WIDTH = 16;
    private static final int FLUID_TEX_HEIGHT = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible

    private static void drawFluid(Minecraft minecraft, int xPosition, int yPosition, int width, int height, FluidStack fluidStack, int capacity) {
        if (fluidStack == null) {
            return;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(minecraft, fluid);

        int fluidColor = fluid.getColor(fluidStack);

        int scaledAmount = (fluidStack.amount * height) / capacity;
        if (fluidStack.amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(minecraft, xPosition, yPosition, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    public static void drawTiledSprite(Minecraft minecraft, final int xPosition, final int yPosition, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / FLUID_TEX_WIDTH;
        final int xRemainder = tiledWidth - (xTileCount * FLUID_TEX_WIDTH);
        final int yTileCount = scaledAmount / FLUID_TEX_HEIGHT;
        final int yRemainder = scaledAmount - (yTileCount * FLUID_TEX_HEIGHT);

        final int yStart = yPosition + tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : FLUID_TEX_WIDTH;
                int height = (yTile == yTileCount) ? yRemainder : FLUID_TEX_HEIGHT;
                int x = xPosition + (xTile * FLUID_TEX_WIDTH);
                int y = yStart - ((yTile + 1) * FLUID_TEX_HEIGHT);
                if (width > 0 && height > 0) {
                    int maskTop = FLUID_TEX_HEIGHT - height;
                    int maskRight = FLUID_TEX_WIDTH - width;

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

    public static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);
    }

    public static void drawTextureWithMasking(double xCoord, double yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, double zLevel) {
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

    private static final double OCT_SW = 1.0 / (1.0 + 2.0 / Math.sqrt(2)); // width of a regular octagon's side
    private static final double OCT_SO = OCT_SW / Math.sqrt(2); // offset of the side from the square's edge

    public enum PlanarShape {

        QUAD(GL11.GL_QUADS, new PV(0, 0), new PV(0, 1), new PV(1, 1), new PV(1, 0)), // square
        OCTAGON(GL11.GL_TRIANGLE_FAN, new PV(0.5, 0.5), // regular octagon
                new PV(OCT_SO + OCT_SW, 0), new PV(OCT_SO, 0),
                new PV(0, OCT_SO), new PV(0, OCT_SO + OCT_SW),
                new PV(OCT_SO, 1), new PV(OCT_SO + OCT_SW, 1),
                new PV(1, OCT_SO + OCT_SW), new PV(1, OCT_SO),
                new PV(OCT_SO + OCT_SW, 0));

        public final int glMode;
        public final ImmutableList<PV> vertices;

        PlanarShape(int glMode, PV... vertices) {
            this.glMode = glMode;
            this.vertices = ImmutableList.copyOf(vertices);
        }

    }

    public static class PV { // planar vertex

        public final double x, y;

        public PV(double x, double y) {
            this.x = x;
            this.y = y;
        }

        private static double lerp(double k, double min, double max) {
            return min + k * (max - min);
        }

        public void pos(BufferBuilder renderer, double x1, double y1, double z1, double x2, double y2, double z2, EnumFacing face) {
            switch (face) {
                case DOWN:
                    renderer.pos(lerp(this.y, x1, x2), y1, lerp(this.x, z1, z2));
                    break;
                case UP:
                    renderer.pos(lerp(this.x, x1, x2), y2, lerp(this.y, z1, z2));
                    break;
                case NORTH:
                    renderer.pos(lerp(this.x, x1, x2), lerp(this.y, y1, y2), z1);
                    break;
                case SOUTH:
                    renderer.pos(lerp(this.y, x1, x2), lerp(this.x, y1, y2), z2);
                    break;
                case WEST:
                    renderer.pos(x1, lerp(this.x, y1, y2), lerp(this.y, z1, z2));
                    break;
                case EAST:
                    renderer.pos(x2, lerp(this.y, y1, y2), lerp(this.x, z1, z2));
                    break;
            }
        }

        public void tex(BufferBuilder renderer, double u1, double v1, double u2, double v2) {
            renderer.tex(lerp(this.x, u1, u2), lerp(this.y, v1, v2));
        }

    }

}
