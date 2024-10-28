package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class NoteParticle extends TextureSheetParticle {
   NoteParticle(ClientLevel var1, double var2, double var4, double var6, double var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.friction = 0.66F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.xd *= 0.009999999776482582;
      this.yd *= 0.009999999776482582;
      this.zd *= 0.009999999776482582;
      this.yd += 0.2;
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

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         NoteParticle var15 = new NoteParticle(var2, var3, var5, var7, var9);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
