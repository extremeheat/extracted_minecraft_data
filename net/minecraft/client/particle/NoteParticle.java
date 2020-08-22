package net.minecraft.client.particle;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class NoteParticle extends TextureSheetParticle {
   private NoteParticle(Level var1, double var2, double var4, double var6, double var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.xd *= 0.009999999776482582D;
      this.yd *= 0.009999999776482582D;
      this.zd *= 0.009999999776482582D;
      this.yd += 0.2D;
      this.rCol = Math.max(0.0F, Mth.sin(((float)var8 + 0.0F) * 6.2831855F) * 0.65F + 0.35F);
      this.gCol = Math.max(0.0F, Mth.sin(((float)var8 + 0.33333334F) * 6.2831855F) * 0.65F + 0.35F);
      this.bCol = Math.max(0.0F, Mth.sin(((float)var8 + 0.6666667F) * 6.2831855F) * 0.65F + 0.35F);
      this.quadSize *= 1.5F;
      this.lifetime = 6;
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
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.6600000262260437D;
         this.yd *= 0.6600000262260437D;
         this.zd *= 0.6600000262260437D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   // $FF: synthetic method
   NoteParticle(Level var1, double var2, double var4, double var6, double var8, Object var10) {
      this(var1, var2, var4, var6, var8);
   }

   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         NoteParticle var15 = new NoteParticle(var2, var3, var5, var7, var9);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
