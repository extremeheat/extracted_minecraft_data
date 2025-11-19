package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class SuspendedTownParticle extends SingleQuadParticle {
   SuspendedTownParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14);
      float var15 = this.random.nextFloat() * 0.1F + 0.2F;
      this.rCol = var15;
      this.gCol = var15;
      this.bCol = var15;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.5F;
      this.xd *= 0.019999999552965164;
      this.yd *= 0.019999999552965164;
      this.zd *= 0.019999999552965164;
      this.lifetime = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.99;
         this.yd *= 0.99;
         this.zd *= 0.99;
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new SuspendedTownParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
      }
   }

   public static class HappyVillagerProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public HappyVillagerProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SuspendedTownParticle var16 = new SuspendedTownParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         var16.setColor(1.0F, 1.0F, 1.0F);
         return var16;
      }
   }

   public static class ComposterFillProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public ComposterFillProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SuspendedTownParticle var16 = new SuspendedTownParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         var16.setColor(1.0F, 1.0F, 1.0F);
         var16.setLifetime(3 + var2.getRandom().nextInt(5));
         return var16;
      }
   }

   public static class DolphinSpeedProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DolphinSpeedProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SuspendedTownParticle var16 = new SuspendedTownParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         var16.setColor(0.3F, 0.5F, 1.0F);
         var16.setAlpha(1.0F - var15.nextFloat() * 0.7F);
         var16.setLifetime(var16.getLifetime() / 2);
         return var16;
      }
   }

   public static class EggCrackProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public EggCrackProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SuspendedTownParticle var16 = new SuspendedTownParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         var16.setColor(1.0F, 1.0F, 1.0F);
         return var16;
      }
   }
}
