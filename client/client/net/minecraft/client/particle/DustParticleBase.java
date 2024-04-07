package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.util.Mth;

public class DustParticleBase<T extends DustParticleOptionsBase> extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected DustParticleBase(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, T var14, SpriteSet var15) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = var15;
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      float var16 = this.random.nextFloat() * 0.4F + 0.6F;
      this.rCol = this.randomizeColor(var14.getColor().x(), var16);
      this.gCol = this.randomizeColor(var14.getColor().y(), var16);
      this.bCol = this.randomizeColor(var14.getColor().z(), var16);
      this.quadSize = this.quadSize * 0.75F * var14.getScale();
      int var17 = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
      this.lifetime = (int)Math.max((float)var17 * var14.getScale(), 1.0F);
      this.setSpriteFromAge(var15);
   }

   protected float randomizeColor(float var1, float var2) {
      return (this.random.nextFloat() * 0.2F + 0.8F) * var1 * var2;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   @Override
   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }
}
