package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class PortalParticle extends SingleQuadParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;

   protected PortalParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      super(var1, var2, var4, var6, var14);
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
      this.x = var2;
      this.y = var4;
      this.z = var6;
      this.xStart = this.x;
      this.yStart = this.y;
      this.zStart = this.z;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
      float var15 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = var15 * 0.9F;
      this.gCol = var15 * 0.3F;
      this.bCol = var15;
      this.lifetime = (int)(this.random.nextFloat() * 10.0F) + 40;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   public float getQuadSize(float var1) {
      float var2 = ((float)this.age + var1) / (float)this.lifetime;
      var2 = 1.0F - var2;
      var2 *= var2;
      var2 = 1.0F - var2;
      return this.quadSize * var2;
   }

   public int getLightColor(float var1) {
      int var2 = super.getLightColor(var1);
      float var3 = (float)this.age / (float)this.lifetime;
      var3 *= var3;
      var3 *= var3;
      int var4 = var2 & 255;
      int var5 = var2 >> 16 & 255;
      var5 += (int)(var3 * 15.0F * 16.0F);
      if (var5 > 240) {
         var5 = 240;
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
         float var1 = (float)this.age / (float)this.lifetime;
         float var2 = var1;
         var1 = -var1 + var1 * var1 * 2.0F;
         var1 = 1.0F - var1;
         this.x = this.xStart + this.xd * (double)var1;
         this.y = this.yStart + this.yd * (double)var1 + (double)(1.0F - var2);
         this.z = this.zStart + this.zd * (double)var1;
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         PortalParticle var16 = new PortalParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         return var16;
      }
   }
}
