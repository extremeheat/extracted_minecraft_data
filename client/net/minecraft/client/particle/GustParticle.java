package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class GustParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected GustParticle(ClientLevel var1, double var2, double var4, double var6, SpriteSet var8) {
      super(var1, var2, var4, var6);
      this.sprites = var8;
      this.setSpriteFromAge(var8);
      this.lifetime = 12 + this.random.nextInt(4);
      this.quadSize = 1.0F;
      this.setSize(1.0F, 1.0F);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float var1) {
      return 15728880;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new GustParticle(var2, var3, var5, var7, this.sprites);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SmallProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GustParticle var15 = new GustParticle(var2, var3, var5, var7, this.sprites);
         ((Particle)var15).scale(0.15F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
