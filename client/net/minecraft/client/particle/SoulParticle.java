package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class SoulParticle extends RisingParticle {
   private final SpriteSet sprites;
   protected boolean isGlowing;

   SoulParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14.first());
      this.sprites = var14;
      this.scale(1.5F);
      this.setSpriteFromAge(var14);
   }

   public int getLightColor(float var1) {
      return this.isGlowing ? 240 : super.getLightColor(var1);
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SoulParticle var16 = new SoulParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var16.setAlpha(1.0F);
         return var16;
      }
   }

   public static class EmissiveProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public EmissiveProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SoulParticle var16 = new SoulParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite);
         var16.setAlpha(1.0F);
         var16.isGlowing = true;
         return var16;
      }
   }
}
