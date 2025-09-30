package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class FallingLeavesParticle extends SingleQuadParticle {
   private static final float ACCELERATION_SCALE = 0.0025F;
   private static final int INITIAL_LIFETIME = 300;
   private static final int CURVE_ENDPOINT_TIME = 300;
   private float rotSpeed;
   private final float spinAcceleration;
   private final float windBig;
   private final boolean swirl;
   private final boolean flowAway;
   private final double xaFlowScale;
   private final double zaFlowScale;
   private final double swirlPeriod;

   protected FallingLeavesParticle(ClientLevel var1, double var2, double var4, double var6, TextureAtlasSprite var8, float var9, float var10, boolean var11, boolean var12, float var13, float var14) {
      super(var1, var2, var4, var6, var8);
      this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
      this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);
      this.windBig = var10;
      this.swirl = var11;
      this.flowAway = var12;
      this.lifetime = 300;
      this.gravity = var9 * 1.2F * 0.0025F;
      float var15 = var13 * (this.random.nextBoolean() ? 0.05F : 0.075F);
      this.quadSize = var15;
      this.setSize(var15, var15);
      this.friction = 1.0F;
      this.yd = (double)(-var14);
      float var16 = this.random.nextFloat();
      this.xaFlowScale = Math.cos(Math.toRadians((double)(var16 * 60.0F))) * (double)this.windBig;
      this.zaFlowScale = Math.sin(Math.toRadians((double)(var16 * 60.0F))) * (double)this.windBig;
      this.swirlPeriod = Math.toRadians((double)(1000.0F + var16 * 3000.0F));
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      }

      if (!this.removed) {
         float var1 = (float)(300 - this.lifetime);
         float var2 = Math.min(var1 / 300.0F, 1.0F);
         double var3 = 0.0;
         double var5 = 0.0;
         if (this.flowAway) {
            var3 += this.xaFlowScale * Math.pow((double)var2, 1.25);
            var5 += this.zaFlowScale * Math.pow((double)var2, 1.25);
         }

         if (this.swirl) {
            var3 += (double)var2 * Math.cos((double)var2 * this.swirlPeriod) * (double)this.windBig;
            var5 += (double)var2 * Math.sin((double)var2 * this.swirlPeriod) * (double)this.windBig;
         }

         this.xd += var3 * 0.0024999999441206455;
         this.zd += var5 * 0.0024999999441206455;
         this.yd -= (double)this.gravity;
         this.rotSpeed += this.spinAcceleration / 20.0F;
         this.oRoll = this.roll;
         this.roll += this.rotSpeed / 20.0F;
         this.move(this.xd, this.yd, this.zd);
         if (this.onGround || this.lifetime < 299 && (this.xd == 0.0 || this.zd == 0.0)) {
            this.remove();
         }

         if (!this.removed) {
            this.xd *= (double)this.friction;
            this.yd *= (double)this.friction;
            this.zd *= (double)this.friction;
         }
      }
   }

   public static class CherryProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public CherryProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new FallingLeavesParticle(var2, var3, var5, var7, this.sprites.get(var15), 0.25F, 2.0F, false, true, 1.0F, 0.0F);
      }
   }

   public static class PaleOakProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public PaleOakProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new FallingLeavesParticle(var2, var3, var5, var7, this.sprites.get(var15), 0.07F, 10.0F, true, false, 2.0F, 0.021F);
      }
   }

   public static class TintedLeavesProvider implements ParticleProvider<ColorParticleOption> {
      private final SpriteSet sprites;

      public TintedLeavesProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(ColorParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FallingLeavesParticle var16 = new FallingLeavesParticle(var2, var3, var5, var7, this.sprites.get(var15), 0.07F, 10.0F, true, false, 2.0F, 0.021F);
         var16.setColor(var1.getRed(), var1.getGreen(), var1.getBlue());
         return var16;
      }
   }
}
