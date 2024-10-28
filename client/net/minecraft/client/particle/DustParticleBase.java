package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.util.Mth;

public class DustParticleBase<T extends ScalableParticleOptionsBase> extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected DustParticleBase(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, T var14, SpriteSet var15) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = var15;
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.quadSize *= 0.75F * var14.getScale();
      int var16 = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
      this.lifetime = (int)Math.max((float)var16 * var14.getScale(), 1.0F);
      this.setSpriteFromAge(var15);
   }

   protected float randomizeColor(float var1, float var2) {
      return (this.random.nextFloat() * 0.2F + 0.8F) * var1 * var2;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }
}
