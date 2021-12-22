package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;

public class FireworkParticles {
   public FireworkParticles() {
      super();
   }

   public static class SparkProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SparkProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         FireworkParticles.SparkParticle var15 = new FireworkParticles.SparkParticle(var2, var3, var5, var7, var9, var11, var13, Minecraft.getInstance().particleEngine, this.sprites);
         var15.setAlpha(0.99F);
         return var15;
      }
   }

   public static class FlashProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public FlashProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         FireworkParticles.OverlayParticle var15 = new FireworkParticles.OverlayParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   public static class OverlayParticle extends TextureSheetParticle {
      OverlayParticle(ClientLevel var1, double var2, double var4, double var6) {
         super(var1, var2, var4, var6);
         this.lifetime = 4;
      }

      public ParticleRenderType getRenderType() {
         return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
      }

      public void render(VertexConsumer var1, Camera var2, float var3) {
         this.setAlpha(0.6F - ((float)this.age + var3 - 1.0F) * 0.25F * 0.5F);
         super.render(var1, var2, var3);
      }

      public float getQuadSize(float var1) {
         return 7.1F * Mth.sin(((float)this.age + var1 - 1.0F) * 0.25F * 3.1415927F);
      }
   }

   static class SparkParticle extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean flicker;
      private final ParticleEngine engine;
      private float fadeR;
      private float fadeG;
      private float fadeB;
      private boolean hasFade;

      SparkParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, ParticleEngine var14, SpriteSet var15) {
         super(var1, var2, var4, var6, var15, 0.1F);
         this.xd = var8;
         this.yd = var10;
         this.zd = var12;
         this.engine = var14;
         this.quadSize *= 0.75F;
         this.lifetime = 48 + this.random.nextInt(12);
         this.setSpriteFromAge(var15);
      }

      public void setTrail(boolean var1) {
         this.trail = var1;
      }

      public void setFlicker(boolean var1) {
         this.flicker = var1;
      }

      public void render(VertexConsumer var1, Camera var2, float var3) {
         if (!this.flicker || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
            super.render(var1, var2, var3);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
            FireworkParticles.SparkParticle var1 = new FireworkParticles.SparkParticle(this.level, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D, this.engine, this.sprites);
            var1.setAlpha(0.99F);
            var1.setColor(this.rCol, this.gCol, this.bCol);
            var1.age = var1.lifetime / 2;
            if (this.hasFade) {
               var1.hasFade = true;
               var1.fadeR = this.fadeR;
               var1.fadeG = this.fadeG;
               var1.fadeB = this.fadeB;
            }

            var1.flicker = this.flicker;
            this.engine.add(var1);
         }

      }
   }

   public static class Starter extends NoRenderParticle {
      private int life;
      private final ParticleEngine engine;
      private ListTag explosions;
      private boolean twinkleDelay;

      public Starter(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, ParticleEngine var14, @Nullable CompoundTag var15) {
         super(var1, var2, var4, var6);
         this.xd = var8;
         this.yd = var10;
         this.zd = var12;
         this.engine = var14;
         this.lifetime = 8;
         if (var15 != null) {
            this.explosions = var15.getList("Explosions", 10);
            if (this.explosions.isEmpty()) {
               this.explosions = null;
            } else {
               this.lifetime = this.explosions.size() * 2 - 1;

               for(int var16 = 0; var16 < this.explosions.size(); ++var16) {
                  CompoundTag var17 = this.explosions.getCompound(var16);
                  if (var17.getBoolean("Flicker")) {
                     this.twinkleDelay = true;
                     this.lifetime += 15;
                     break;
                  }
               }
            }
         }

      }

      public void tick() {
         boolean var1;
         if (this.life == 0 && this.explosions != null) {
            var1 = this.isFarAwayFromCamera();
            boolean var2 = false;
            if (this.explosions.size() >= 3) {
               var2 = true;
            } else {
               for(int var3 = 0; var3 < this.explosions.size(); ++var3) {
                  CompoundTag var4 = this.explosions.getCompound(var3);
                  if (FireworkRocketItem.Shape.byId(var4.getByte("Type")) == FireworkRocketItem.Shape.LARGE_BALL) {
                     var2 = true;
                     break;
                  }
               }
            }

            SoundEvent var15;
            if (var2) {
               var15 = var1 ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               var15 = var1 ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
            }

            this.level.playLocalSound(this.x, this.y, this.z, var15, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
         }

         if (this.life % 2 == 0 && this.explosions != null && this.life / 2 < this.explosions.size()) {
            int var13 = this.life / 2;
            CompoundTag var14 = this.explosions.getCompound(var13);
            FireworkRocketItem.Shape var18 = FireworkRocketItem.Shape.byId(var14.getByte("Type"));
            boolean var17 = var14.getBoolean("Trail");
            boolean var5 = var14.getBoolean("Flicker");
            int[] var6 = var14.getIntArray("Colors");
            int[] var7 = var14.getIntArray("FadeColors");
            if (var6.length == 0) {
               var6 = new int[]{DyeColor.BLACK.getFireworkColor()};
            }

            switch(var18) {
            case SMALL_BALL:
            default:
               this.createParticleBall(0.25D, 2, var6, var7, var17, var5);
               break;
            case LARGE_BALL:
               this.createParticleBall(0.5D, 4, var6, var7, var17, var5);
               break;
            case STAR:
               this.createParticleShape(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, var6, var7, var17, var5, false);
               break;
            case CREEPER:
               this.createParticleShape(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, var6, var7, var17, var5, true);
               break;
            case BURST:
               this.createParticleBurst(var6, var7, var17, var5);
            }

            int var8 = var6[0];
            float var9 = (float)((var8 & 16711680) >> 16) / 255.0F;
            float var10 = (float)((var8 & '\uff00') >> 8) / 255.0F;
            float var11 = (float)((var8 & 255) >> 0) / 255.0F;
            Particle var12 = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            var12.setColor(var9, var10, var11);
         }

         ++this.life;
         if (this.life > this.lifetime) {
            if (this.twinkleDelay) {
               var1 = this.isFarAwayFromCamera();
               SoundEvent var16 = var1 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
               this.level.playLocalSound(this.x, this.y, this.z, var16, SoundSource.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }

            this.remove();
         }

      }

      private boolean isFarAwayFromCamera() {
         Minecraft var1 = Minecraft.getInstance();
         return var1.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0D;
      }

      private void createParticle(double var1, double var3, double var5, double var7, double var9, double var11, int[] var13, int[] var14, boolean var15, boolean var16) {
         FireworkParticles.SparkParticle var17 = (FireworkParticles.SparkParticle)this.engine.createParticle(ParticleTypes.FIREWORK, var1, var3, var5, var7, var9, var11);
         var17.setTrail(var15);
         var17.setFlicker(var16);
         var17.setAlpha(0.99F);
         int var18 = this.random.nextInt(var13.length);
         var17.setColor(var13[var18]);
         if (var14.length > 0) {
            var17.setFadeColor(Util.getRandom(var14, this.random));
         }

      }

      private void createParticleBall(double var1, int var3, int[] var4, int[] var5, boolean var6, boolean var7) {
         double var8 = this.x;
         double var10 = this.y;
         double var12 = this.z;

         for(int var14 = -var3; var14 <= var3; ++var14) {
            for(int var15 = -var3; var15 <= var3; ++var15) {
               for(int var16 = -var3; var16 <= var3; ++var16) {
                  double var17 = (double)var15 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double var19 = (double)var14 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double var21 = (double)var16 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double var23 = Math.sqrt(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.random.nextGaussian() * 0.05D;
                  this.createParticle(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, var4, var5, var6, var7);
                  if (var14 != -var3 && var14 != var3 && var15 != -var3 && var15 != var3) {
                     var16 += var3 * 2 - 1;
                  }
               }
            }
         }

      }

      private void createParticleShape(double var1, double[][] var3, int[] var4, int[] var5, boolean var6, boolean var7, boolean var8) {
         double var9 = var3[0][0];
         double var11 = var3[0][1];
         this.createParticle(this.x, this.y, this.z, var9 * var1, var11 * var1, 0.0D, var4, var5, var6, var7);
         float var13 = this.random.nextFloat() * 3.1415927F;
         double var14 = var8 ? 0.034D : 0.34D;

         for(int var16 = 0; var16 < 3; ++var16) {
            double var17 = (double)var13 + (double)((float)var16 * 3.1415927F) * var14;
            double var19 = var9;
            double var21 = var11;

            for(int var23 = 1; var23 < var3.length; ++var23) {
               double var24 = var3[var23][0];
               double var26 = var3[var23][1];

               for(double var28 = 0.25D; var28 <= 1.0D; var28 += 0.25D) {
                  double var30 = Mth.lerp(var28, var19, var24) * var1;
                  double var32 = Mth.lerp(var28, var21, var26) * var1;
                  double var34 = var30 * Math.sin(var17);
                  var30 *= Math.cos(var17);

                  for(double var36 = -1.0D; var36 <= 1.0D; var36 += 2.0D) {
                     this.createParticle(this.x, this.y, this.z, var30 * var36, var32, var34 * var36, var4, var5, var6, var7);
                  }
               }

               var19 = var24;
               var21 = var26;
            }
         }

      }

      private void createParticleBurst(int[] var1, int[] var2, boolean var3, boolean var4) {
         double var5 = this.random.nextGaussian() * 0.05D;
         double var7 = this.random.nextGaussian() * 0.05D;

         for(int var9 = 0; var9 < 70; ++var9) {
            double var10 = this.xd * 0.5D + this.random.nextGaussian() * 0.15D + var5;
            double var12 = this.zd * 0.5D + this.random.nextGaussian() * 0.15D + var7;
            double var14 = this.yd * 0.5D + this.random.nextDouble() * 0.5D;
            this.createParticle(this.x, this.y, this.z, var10, var14, var12, var1, var2, var3, var4);
         }

      }
   }
}
