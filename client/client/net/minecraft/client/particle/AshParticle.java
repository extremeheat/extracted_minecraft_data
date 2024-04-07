package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class AshParticle extends BaseAshSmokeParticle {
   protected AshParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, SpriteSet var15) {
      super(var1, var2, var4, var6, 0.1F, -0.1F, 0.1F, var8, var10, var12, var14, var15, 0.5F, 20, 0.1F, false);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new AshParticle(var2, var3, var5, var7, 0.0, 0.0, 0.0, 1.0F, this.sprites);
      }
   }
}
