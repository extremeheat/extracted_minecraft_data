package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;

public class DustParticle extends DustParticleBase<DustParticleOptions> {
   protected DustParticle(
      ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, DustParticleOptions var14, SpriteSet var15
   ) {
      super(var1, var2, var4, var6, var8, var10, var12, var14, var15);
      float var16 = this.random.nextFloat() * 0.4F + 0.6F;
      Vector3f var17 = var14.getColor();
      this.rCol = this.randomizeColor(var17.x(), var16);
      this.gCol = this.randomizeColor(var17.y(), var16);
      this.bCol = this.randomizeColor(var17.z(), var16);
   }

   public static class Provider implements ParticleProvider<DustParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(DustParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new DustParticle(var2, var3, var5, var7, var9, var11, var13, var1, this.sprites);
      }
   }
}
