package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class EndRodParticle extends SimpleAnimatedParticle {
   private EndRodParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var14, -5.0E-4F);
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setFadeColor(15916745);
      this.setSpriteFromAge(var14);
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   // $FF: synthetic method
   EndRodParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new EndRodParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
