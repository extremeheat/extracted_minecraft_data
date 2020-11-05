package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;

public class DustParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private DustParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, DustParticleOptions var14, SpriteSet var15) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.sprites = var15;
      this.xd *= 0.10000000149011612D;
      this.yd *= 0.10000000149011612D;
      this.zd *= 0.10000000149011612D;
      float var16 = (float)Math.random() * 0.4F + 0.6F;
      this.rCol = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * var14.getR() * var16;
      this.gCol = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * var14.getG() * var16;
      this.bCol = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * var14.getB() * var16;
      this.quadSize *= 0.75F * var14.getScale();
      int var17 = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.lifetime = (int)Math.max((float)var17 * var14.getScale(), 1.0F);
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

   // $FF: synthetic method
   DustParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, DustParticleOptions var14, SpriteSet var15, Object var16) {
      this(var1, var2, var4, var6, var8, var10, var12, var14, var15);
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
