package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

public abstract class BaseAshSmokeParticle extends SingleQuadParticle {
   private final SpriteSet sprites;

   protected BaseAshSmokeParticle(final ClientLevel level, final double x, final double y, final double z, final float dirX, final float dirY, final float dirZ, final double xa, final double ya, final double za, final float scale, final SpriteSet sprites, final float colorRandom, final int maxLifetime, final float gravity, final boolean hasPhysics) {
      super(level, x, y, z, 0.0, 0.0, 0.0, sprites.first());
      this.friction = 0.96F;
      this.gravity = gravity;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = sprites;
      this.xd *= (double)dirX;
      this.yd *= (double)dirY;
      this.zd *= (double)dirZ;
      this.xd += xa;
      this.yd += ya;
      this.zd += za;
      float col = this.random.nextFloat() * colorRandom;
      this.rCol = col;
      this.gCol = col;
      this.bCol = col;
      this.quadSize *= 0.75F * scale;
      this.lifetime = (int)((double)maxLifetime / ((double)this.random.nextFloat() * 0.8 + 0.2) * (double)scale);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(sprites);
      this.hasPhysics = hasPhysics;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public float getQuadSize(final float a) {
      return this.quadSize * Mth.clamp(((float)this.age + a) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }
}
