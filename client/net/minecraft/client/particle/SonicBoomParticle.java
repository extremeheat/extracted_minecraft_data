package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class SonicBoomParticle extends HugeExplosionParticle {
   protected SonicBoomParticle(ClientLevel var1, double var2, double var4, double var6, double var8, SpriteSet var10) {
      super(var1, var2, var4, var6, var8, var10);
      this.lifetime = 16;
      this.quadSize = 1.5F;
      this.setSpriteFromAge(var10);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SonicBoomParticle(var2, var3, var5, var7, var9, this.sprites);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
