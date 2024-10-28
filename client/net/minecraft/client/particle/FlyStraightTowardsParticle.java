package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class FlyStraightTowardsParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private final int startColor;
   private final int endColor;

   FlyStraightTowardsParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, int var14, int var15) {
      super(var1, var2, var4, var6);
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
      this.xStart = var2;
      this.yStart = var4;
      this.zStart = var6;
      this.xo = var2 + var8;
      this.yo = var4 + var10;
      this.zo = var6 + var12;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
      this.hasPhysics = false;
      this.lifetime = (int)(Math.random() * 5.0) + 25;
      this.startColor = var14;
      this.endColor = var15;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
   }

   public int getLightColor(float var1) {
      return 240;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float var1 = (float)this.age / (float)this.lifetime;
         float var2 = 1.0F - var1;
         this.x = this.xStart + this.xd * (double)var2;
         this.y = this.yStart + this.yd * (double)var2;
         this.z = this.zStart + this.zd * (double)var2;
         int var3 = FastColor.ARGB32.lerp(var1, this.startColor, this.endColor);
         this.setColor((float)FastColor.ARGB32.red(var3) / 255.0F, (float)FastColor.ARGB32.green(var3) / 255.0F, (float)FastColor.ARGB32.blue(var3) / 255.0F);
         this.setAlpha((float)FastColor.ARGB32.alpha(var3) / 255.0F);
      }
   }

   public static class OminousSpawnProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public OminousSpawnProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         FlyStraightTowardsParticle var15 = new FlyStraightTowardsParticle(var2, var3, var5, var7, var9, var11, var13, -12210434, -1);
         var15.scale(Mth.randomBetween(var2.getRandom(), 3.0F, 5.0F));
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
