package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class TotemParticle extends SimpleAnimatedParticle {
   private TotemParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var14, -0.05F);
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setSpriteFromAge(var14);
      if (this.random.nextInt(4) == 0) {
         this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
      } else {
         this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
      }

      this.setBaseAirFriction(0.6F);
   }

   // $FF: synthetic method
   TotemParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new TotemParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
