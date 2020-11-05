package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SuspendedParticle extends TextureSheetParticle {
   private SuspendedParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4 - 0.125D, var6);
      this.rCol = 0.4F;
      this.gCol = 0.4F;
      this.bCol = 0.7F;
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
   }

   private SuspendedParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4 - 0.125D, var6, var8, var10, var12);
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
      }
   }

   // $FF: synthetic method
   SuspendedParticle(ClientLevel var1, double var2, double var4, double var6, Object var8) {
      this(var1, var2, var4, var6);
   }

   // $FF: synthetic method
   SuspendedParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, Object var14) {
      this(var1, var2, var4, var6, var8, var10, var12);
   }

   public static class WarpedSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WarpedSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         double var15 = (double)var2.random.nextFloat() * -1.9D * (double)var2.random.nextFloat() * 0.1D;
         SuspendedParticle var17 = new SuspendedParticle(var2, var3, var5, var7, 0.0D, var15, 0.0D);
         var17.pickSprite(this.sprite);
         var17.setColor(0.1F, 0.1F, 0.3F);
         var17.setSize(0.001F, 0.001F);
         return var17;
      }
   }

   public static class CrimsonSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public CrimsonSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         Random var15 = var2.random;
         double var16 = var15.nextGaussian() * 9.999999974752427E-7D;
         double var18 = var15.nextGaussian() * 9.999999747378752E-5D;
         double var20 = var15.nextGaussian() * 9.999999974752427E-7D;
         SuspendedParticle var22 = new SuspendedParticle(var2, var3, var5, var7, var16, var18, var20);
         var22.pickSprite(this.sprite);
         var22.setColor(0.9F, 0.4F, 0.5F);
         return var22;
      }
   }

   public static class UnderwaterProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public UnderwaterProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedParticle var15 = new SuspendedParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
