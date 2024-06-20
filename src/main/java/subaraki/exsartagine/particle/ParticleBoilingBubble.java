package subaraki.exsartagine.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleBoilingBubble extends Particle {

    private final double startingY;
    private int dormantTime;
    private boolean popped = false;

    public ParticleBoilingBubble(World world, double x, double y, double z, float r, float g, float b) {
        super(world, x, y, z);
        this.startingY = y;
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
        this.setParticleTextureIndex(32);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.2F + 0.2F;
        this.motionX = -0.001 + 0.002 * this.rand.nextDouble();
        this.motionY = 0.004 + 0.002 * this.rand.nextDouble();
        this.motionZ = -0.001 + 0.002 * this.rand.nextDouble();
        this.dormantTime = this.rand.nextInt(8);
        this.particleMaxAge = 20;
    }

    @Override
    public void onUpdate() {
        if (dormantTime > 0) {
            --dormantTime;
            return;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        move(motionX, motionY, motionZ);

        double diffY = posY - startingY;
        if (diffY >= 0.04) {
            setExpired();
            return;
        } else if (diffY >= 0.025 && !popped) {
            this.setParticleTextureIndex(19 + this.rand.nextInt(4));
            popped = true;
        }

        if (--particleMaxAge <= 0) {
            setExpired();
        }
    }

    @Override
    public void renderParticle(final BufferBuilder buffer, final Entity entityIn, final float partialTicks, final float rotationX, final float rotationZ, final float rotationYZ, final float rotationXY, final float rotationXZ) {
        if (dormantTime <= 0) {
            super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        }
    }

}
