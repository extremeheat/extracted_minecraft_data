package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

public class AttackSweepParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private AttackSweepParticle(Level var1, double var2, double var4, double var6, double var8, SpriteSet var10) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.sprites = var10;
      this.lifetime = 4;
      float var11 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = var11;
      this.gCol = var11;
      this.bCol = var11;
      this.quadSize = 1.0F - (float)var8 * 0.5F;
      this.setSpriteFromAge(var10);
   }

   public int getLightColor(float var1) {
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   // $FF: synthetic method
   AttackSweepParticle(Level var1, double var2, double var4, double var6, double var8, SpriteSet var10, Object var11) {
      this(var1, var2, var4, var6, var8, var10);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new AttackSweepParticle(var2, var3, var5, var7, var9, this.sprites);
      }
   }
}
