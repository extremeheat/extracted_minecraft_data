package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.ARGB;

public class SquidInkParticle extends SimpleAnimatedParticle {
   SquidInkParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, int var14, SpriteSet var15) {
      super(var1, var2, var4, var6, var15, 0.0F);
      this.friction = 0.92F;
      this.quadSize = 0.5F;
      this.setAlpha(1.0F);
      this.setColor((float)ARGB.red(var14), (float)ARGB.green(var14), (float)ARGB.blue(var14));
      this.lifetime = (int)((double)(this.quadSize * 12.0F) / (Math.random() * 0.800000011920929 + 0.20000000298023224));
      this.setSpriteFromAge(var15);
      this.hasPhysics = false;
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
         }

         if (this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
            this.yd -= 0.007400000002235174;
         }
      }
   }

   public static class GlowInkProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public GlowInkProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SquidInkParticle(var2, var3, var5, var7, var9, var11, var13, ARGB.color(255, 204, 31, 102), this.sprites);
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SquidInkParticle(var2, var3, var5, var7, var9, var11, var13, ARGB.color(255, 255, 255, 255), this.sprites);
      }
   }
}
