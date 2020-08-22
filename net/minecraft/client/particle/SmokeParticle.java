package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class SmokeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected SmokeParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, SpriteSet var15) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.sprites = var15;
      this.xd *= 0.10000000149011612D;
      this.yd *= 0.10000000149011612D;
      this.zd *= 0.10000000149011612D;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
      float var16 = (float)(Math.random() * 0.30000001192092896D);
      this.rCol = var16;
      this.gCol = var16;
      this.bCol = var16;
      this.quadSize *= 0.75F * var14;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.lifetime = (int)((float)this.lifetime * var14);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(var15);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
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
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.9599999785423279D;
         this.yd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SmokeParticle(var2, var3, var5, var7, var9, var11, var13, 1.0F, this.sprites);
      }
   }
}
