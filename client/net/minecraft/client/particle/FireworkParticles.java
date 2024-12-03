package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.FireworkExplosion;

public class FireworkParticles {
   public FireworkParticles() {
      super();
   }

   public static class Starter extends NoRenderParticle {
      private static final double[][] CREEPER_PARTICLE_COORDS = new double[][]{{0.0, 0.2}, {0.2, 0.2}, {0.2, 0.6}, {0.6, 0.6}, {0.6, 0.2}, {0.2, 0.2}, {0.2, 0.0}, {0.4, 0.0}, {0.4, -0.6}, {0.2, -0.6}, {0.2, -0.4}, {0.0, -0.4}};
      private static final double[][] STAR_PARTICLE_COORDS = new double[][]{{0.0, 1.0}, {0.3455, 0.309}, {0.9511, 0.309}, {0.3795918367346939, -0.12653061224489795}, {0.6122448979591837, -0.8040816326530612}, {0.0, -0.35918367346938773}};
      private int life;
      private final ParticleEngine engine;
      private final List<FireworkExplosion> explosions;
      private boolean twinkleDelay;

      public Starter(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, ParticleEngine var14, List<FireworkExplosion> var15) {
         super(var1, var2, var4, var6);
         this.xd = var8;
         this.yd = var10;
         this.zd = var12;
         this.engine = var14;
         if (var15.isEmpty()) {
            throw new IllegalArgumentException("Cannot create firework starter with no explosions");
         } else {
            this.explosions = var15;
            this.lifetime = var15.size() * 2 - 1;

            for(FireworkExplosion var17 : var15) {
               if (var17.hasTwinkle()) {
                  this.twinkleDelay = true;
                  this.lifetime += 15;
                  break;
               }
            }

         }
      }

      public void tick() {
         if (this.life == 0) {
            boolean var1 = this.isFarAwayFromCamera();
            boolean var2 = false;
            if (this.explosions.size() >= 3) {
               var2 = true;
            } else {
               for(FireworkExplosion var4 : this.explosions) {
                  if (var4.shape() == FireworkExplosion.Shape.LARGE_BALL) {
                     var2 = true;
                     break;
                  }
               }
            }

            SoundEvent var13;
            if (var2) {
               var13 = var1 ? SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               var13 = var1 ? SoundEvents.FIREWORK_ROCKET_BLAST_FAR : SoundEvents.FIREWORK_ROCKET_BLAST;
            }

            this.level.playLocalSound(this.x, this.y, this.z, var13, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
         }

         if (this.life % 2 == 0 && this.life / 2 < this.explosions.size()) {
            int var9 = this.life / 2;
            FireworkExplosion var11 = (FireworkExplosion)this.explosions.get(var9);
            boolean var14 = var11.hasTrail();
            boolean var15 = var11.hasTwinkle();
            IntList var5 = var11.colors();
            IntList var6 = var11.fadeColors();
            if (var5.isEmpty()) {
               var5 = IntList.of(DyeColor.BLACK.getFireworkColor());
            }

            switch (var11.shape()) {
               case SMALL_BALL -> this.createParticleBall(0.25, 2, var5, var6, var14, var15);
               case LARGE_BALL -> this.createParticleBall(0.5, 4, var5, var6, var14, var15);
               case STAR -> this.createParticleShape(0.5, STAR_PARTICLE_COORDS, var5, var6, var14, var15, false);
               case CREEPER -> this.createParticleShape(0.5, CREEPER_PARTICLE_COORDS, var5, var6, var14, var15, true);
               case BURST -> this.createParticleBurst(var5, var6, var14, var15);
            }

            int var7 = var5.getInt(0);
            Particle var8 = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            var8.setColor((float)ARGB.red(var7) / 255.0F, (float)ARGB.green(var7) / 255.0F, (float)ARGB.blue(var7) / 255.0F);
         }

         ++this.life;
         if (this.life > this.lifetime) {
            if (this.twinkleDelay) {
               boolean var10 = this.isFarAwayFromCamera();
               SoundEvent var12 = var10 ? SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR : SoundEvents.FIREWORK_ROCKET_TWINKLE;
               this.level.playLocalSound(this.x, this.y, this.z, var12, SoundSource.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }

            this.remove();
         }

      }

      private boolean isFarAwayFromCamera() {
         Minecraft var1 = Minecraft.getInstance();
         return var1.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0;
      }

      private void createParticle(double var1, double var3, double var5, double var7, double var9, double var11, IntList var13, IntList var14, boolean var15, boolean var16) {
         SparkParticle var17 = (SparkParticle)this.engine.createParticle(ParticleTypes.FIREWORK, var1, var3, var5, var7, var9, var11);
         var17.setTrail(var15);
         var17.setTwinkle(var16);
         var17.setAlpha(0.99F);
         var17.setColor((Integer)Util.getRandom(var13, this.random));
         if (!var14.isEmpty()) {
            var17.setFadeColor((Integer)Util.getRandom(var14, this.random));
         }

      }

      private void createParticleBall(double var1, int var3, IntList var4, IntList var5, boolean var6, boolean var7) {
         double var8 = this.x;
         double var10 = this.y;
         double var12 = this.z;

         for(int var14 = -var3; var14 <= var3; ++var14) {
            for(int var15 = -var3; var15 <= var3; ++var15) {
               for(int var16 = -var3; var16 <= var3; ++var16) {
                  double var17 = (double)var15 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                  double var19 = (double)var14 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                  double var21 = (double)var16 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                  double var23 = Math.sqrt(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.random.nextGaussian() * 0.05;
                  this.createParticle(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, var4, var5, var6, var7);
                  if (var14 != -var3 && var14 != var3 && var15 != -var3 && var15 != var3) {
                     var16 += var3 * 2 - 1;
                  }
               }
            }
         }

      }

      private void createParticleShape(double var1, double[][] var3, IntList var4, IntList var5, boolean var6, boolean var7, boolean var8) {
         double var9 = var3[0][0];
         double var11 = var3[0][1];
         this.createParticle(this.x, this.y, this.z, var9 * var1, var11 * var1, 0.0, var4, var5, var6, var7);
         float var13 = this.random.nextFloat() * 3.1415927F;
         double var14 = var8 ? 0.034 : 0.34;

         for(int var16 = 0; var16 < 3; ++var16) {
            double var17 = (double)var13 + (double)((float)var16 * 3.1415927F) * var14;
            double var19 = var9;
            double var21 = var11;

            for(int var23 = 1; var23 < var3.length; ++var23) {
               double var24 = var3[var23][0];
               double var26 = var3[var23][1];

               for(double var28 = 0.25; var28 <= 1.0; var28 += 0.25) {
                  double var30 = Mth.lerp(var28, var19, var24) * var1;
                  double var32 = Mth.lerp(var28, var21, var26) * var1;
                  double var34 = var30 * Math.sin(var17);
                  var30 *= Math.cos(var17);

                  for(double var36 = -1.0; var36 <= 1.0; var36 += 2.0) {
                     this.createParticle(this.x, this.y, this.z, var30 * var36, var32, var34 * var36, var4, var5, var6, var7);
                  }
               }

               var19 = var24;
               var21 = var26;
            }
         }

      }

      private void createParticleBurst(IntList var1, IntList var2, boolean var3, boolean var4) {
         double var5 = this.random.nextGaussian() * 0.05;
         double var7 = this.random.nextGaussian() * 0.05;

         for(int var9 = 0; var9 < 70; ++var9) {
            double var10 = this.xd * 0.5 + this.random.nextGaussian() * 0.15 + var5;
            double var12 = this.zd * 0.5 + this.random.nextGaussian() * 0.15 + var7;
            double var14 = this.yd * 0.5 + this.random.nextDouble() * 0.5;
            this.createParticle(this.x, this.y, this.z, var10, var14, var12, var1, var2, var3, var4);
         }

      }
   }

   static class SparkParticle extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean twinkle;
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

      public void setTwinkle(boolean var1) {
         this.twinkle = var1;
      }

      public void render(VertexConsumer var1, Camera var2, float var3) {
         if (!this.twinkle || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
            super.render(var1, var2, var3);
         }

      }

      public void tick() {
         super.tick();
         if (this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
            SparkParticle var1 = new SparkParticle(this.level, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.engine, this.sprites);
            var1.setAlpha(0.99F);
            var1.setColor(this.rCol, this.gCol, this.bCol);
            var1.age = var1.lifetime / 2;
            if (this.hasFade) {
               var1.hasFade = true;
               var1.fadeR = this.fadeR;
               var1.fadeG = this.fadeG;
               var1.fadeB = this.fadeB;
            }

            var1.twinkle = this.twinkle;
            this.engine.add(var1);
         }

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

   public static class FlashProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public FlashProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         OverlayParticle var15 = new OverlayParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class SparkProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SparkProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SparkParticle var15 = new SparkParticle(var2, var3, var5, var7, var9, var11, var13, Minecraft.getInstance().particleEngine, this.sprites);
         var15.setAlpha(0.99F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
