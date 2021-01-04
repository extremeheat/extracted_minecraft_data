package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class FlameParticle extends TextureSheetParticle {
   private FlameParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.xd = this.xd * 0.009999999776482582D + var8;
      this.yd = this.yd * 0.009999999776482582D + var10;
      this.zd = this.zd * 0.009999999776482582D + var12;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   public float getQuadSize(float var1) {
      float var2 = ((float)this.age + var1) / (float)this.lifetime;
      return this.quadSize * (1.0F - var2 * var2 * 0.5F);
   }

   public int getLightColor(float var1) {
      float var2 = ((float)this.age + var1) / (float)this.lifetime;
      var2 = Mth.clamp(var2, 0.0F, 1.0F);
      int var3 = super.getLightColor(var1);
      int var4 = var3 & 255;
      int var5 = var3 >> 16 & 255;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
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
   FlameParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, Object var14) {
      this(var1, var2, var4, var6, var8, var10, var12);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         FlameParticle var15 = new FlameParticle(var2, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
