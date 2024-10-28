package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class HugeExplosionParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected HugeExplosionParticle(ClientLevel var1, double var2, double var4, double var6, double var8, SpriteSet var10) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.lifetime = 6 + this.random.nextInt(4);
      float var11 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = var11;
      this.gCol = var11;
      this.bCol = var11;
      this.quadSize = 2.0F * (1.0F - (float)var8 * 0.5F);
      this.sprites = var10;
      this.setSpriteFromAge(var10);
   }

   public int getLightColor(float var1) {
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new HugeExplosionParticle(var2, var3, var5, var7, var9, this.sprites);
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
