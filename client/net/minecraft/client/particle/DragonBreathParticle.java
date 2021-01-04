package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class DragonBreathParticle extends TextureSheetParticle {
   private boolean hasHitGround;
   private final SpriteSet sprites;

   private DragonBreathParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6);
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
      this.rCol = Mth.nextFloat(this.random, 0.7176471F, 0.8745098F);
      this.gCol = Mth.nextFloat(this.random, 0.0F, 0.0F);
      this.bCol = Mth.nextFloat(this.random, 0.8235294F, 0.9764706F);
      this.quadSize *= 0.75F;
      this.lifetime = (int)(20.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D));
      this.hasHitGround = false;
      this.hasPhysics = false;
      this.sprites = var14;
      this.setSpriteFromAge(var14);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if (this.onGround) {
            this.yd = 0.0D;
            this.hasHitGround = true;
         }

         if (this.hasHitGround) {
            this.yd += 0.002D;
         }

         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if (this.hasHitGround) {
            this.yd *= 0.9599999785423279D;
         }

      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   // $FF: synthetic method
   DragonBreathParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new DragonBreathParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
