package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class WakeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   WakeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.sprites = var14;
      this.xd *= 0.30000001192092896;
      this.yd = Math.random() * 0.20000000298023224 + 0.10000000149011612;
      this.zd *= 0.30000001192092896;
      this.setSize(0.01F, 0.01F);
      this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.setSpriteFromAge(var14);
      this.gravity = 0.0F;
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      int var1 = 60 - this.lifetime;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863;
         this.yd *= 0.9800000190734863;
         this.zd *= 0.9800000190734863;
         float var2 = (float)var1 * 0.001F;
         this.setSize(var2, var2);
         this.setSprite(this.sprites.get(var1 % 4, 4));
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new WakeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
