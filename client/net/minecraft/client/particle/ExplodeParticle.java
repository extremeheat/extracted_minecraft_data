package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class ExplodeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected ExplodeParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6);
      this.sprites = var14;
      this.xd = var8 + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.yd = var10 + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.zd = var12 + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      float var15 = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = var15;
      this.gCol = var15;
      this.bCol = var15;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
      this.lifetime = (int)(16.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) + 2;
      this.setSpriteFromAge(var14);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.yd += 0.004D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.8999999761581421D;
         this.yd *= 0.8999999761581421D;
         this.zd *= 0.8999999761581421D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ExplodeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
