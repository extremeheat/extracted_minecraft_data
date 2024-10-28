package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class SnowflakeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected SnowflakeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6);
      this.gravity = 0.225F;
      this.friction = 1.0F;
      this.sprites = var14;
      this.xd = var8 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
      this.yd = var10 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
      this.zd = var12 + (Math.random() * 2.0 - 1.0) * 0.05000000074505806;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 1.0F);
      this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
      this.setSpriteFromAge(var14);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      this.xd *= 0.949999988079071;
      this.yd *= 0.8999999761581421;
      this.zd *= 0.949999988079071;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SnowflakeParticle var15 = new SnowflakeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
         var15.setColor(0.923F, 0.964F, 0.999F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
