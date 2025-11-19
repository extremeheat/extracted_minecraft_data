package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DripParticle extends SingleQuadParticle {
   private final Fluid type;
   protected boolean isGlowing;

   DripParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, TextureAtlasSprite var9) {
      super(var1, var2, var4, var6, var9);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = var8;
   }

   protected Fluid getType() {
      return this.type;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public int getLightColor(float var1) {
      return this.isGlowing ? 240 : super.getLightColor(var1);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.preMoveUpdate();
      if (!this.removed) {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.postMoveUpdate();
         if (!this.removed) {
            this.xd *= 0.9800000190734863;
            this.yd *= 0.9800000190734863;
            this.zd *= 0.9800000190734863;
            if (this.type != Fluids.EMPTY) {
               BlockPos var1 = BlockPos.containing(this.x, this.y, this.z);
               FluidState var2 = this.level.getFluidState(var1);
               if (var2.getType() == this.type && this.y < (double)((float)var1.getY() + var2.getHeight(this.level, var1))) {
                  this.remove();
               }

            }
         }
      }
   }

   protected void preMoveUpdate() {
      if (this.lifetime-- <= 0) {
         this.remove();
      }

   }

   protected void postMoveUpdate() {
   }

   static class DripHangParticle extends DripParticle {
      private final ParticleOptions fallingParticle;

      DripHangParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, TextureAtlasSprite var10) {
         super(var1, var2, var4, var6, var8, var10);
         this.fallingParticle = var9;
         this.gravity *= 0.02F;
         this.lifetime = 40;
      }

      protected void preMoveUpdate() {
         if (this.lifetime-- <= 0) {
            this.remove();
            this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }

      }

      protected void postMoveUpdate() {
         this.xd *= 0.02;
         this.yd *= 0.02;
         this.zd *= 0.02;
      }
   }

   static class CoolingDripHangParticle extends DripHangParticle {
      CoolingDripHangParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, TextureAtlasSprite var10) {
         super(var1, var2, var4, var6, var8, var9, var10);
      }

      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
         this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
         super.preMoveUpdate();
      }
   }

   static class FallAndLandParticle extends FallingParticle {
      protected final ParticleOptions landParticle;

      FallAndLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, TextureAtlasSprite var10) {
         super(var1, var2, var4, var6, var8, var10);
         this.lifetime = (int)(64.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
         this.landParticle = var9;
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
         }

      }
   }

   static class HoneyFallAndLandParticle extends FallAndLandParticle {
      HoneyFallAndLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, TextureAtlasSprite var10) {
         super(var1, var2, var4, var6, var8, var9, var10);
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            float var1 = Mth.randomBetween(this.random, 0.3F, 1.0F);
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, var1, 1.0F, false);
         }

      }
   }

   static class DripstoneFallAndLandParticle extends FallAndLandParticle {
      DripstoneFallAndLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, TextureAtlasSprite var10) {
         super(var1, var2, var4, var6, var8, var9, var10);
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            SoundEvent var1 = this.getType() == Fluids.LAVA ? SoundEvents.POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
            float var2 = Mth.randomBetween(this.random, 0.3F, 1.0F);
            this.level.playLocalSound(this.x, this.y, this.z, var1, SoundSource.BLOCKS, var2, 1.0F, false);
         }

      }
   }

   static class FallingParticle extends DripParticle {
      FallingParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, TextureAtlasSprite var9) {
         super(var1, var2, var4, var6, var8, var9);
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
         }

      }
   }

   static class DripLandParticle extends DripParticle {
      DripLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, TextureAtlasSprite var9) {
         super(var1, var2, var4, var6, var8, var9);
         this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
      }
   }

   public static class WaterHangProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WaterHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripHangParticle var16 = new DripHangParticle(var2, var3, var5, var7, Fluids.WATER, ParticleTypes.FALLING_WATER, this.sprite.get(var15));
         ((DripParticle)var16).setColor(0.2F, 0.3F, 1.0F);
         return var16;
      }
   }

   public static class WaterFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WaterFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FallAndLandParticle var16 = new FallAndLandParticle(var2, var3, var5, var7, Fluids.WATER, ParticleTypes.SPLASH, this.sprite.get(var15));
         ((DripParticle)var16).setColor(0.2F, 0.3F, 1.0F);
         return var16;
      }
   }

   public static class LavaHangProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public LavaHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         CoolingDripHangParticle var16 = new CoolingDripHangParticle(var2, var3, var5, var7, Fluids.LAVA, ParticleTypes.FALLING_LAVA, this.sprite.get(var15));
         return var16;
      }
   }

   public static class LavaFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public LavaFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FallAndLandParticle var16 = new FallAndLandParticle(var2, var3, var5, var7, Fluids.LAVA, ParticleTypes.LANDING_LAVA, this.sprite.get(var15));
         ((DripParticle)var16).setColor(1.0F, 0.2857143F, 0.083333336F);
         return var16;
      }
   }

   public static class LavaLandProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public LavaLandProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripLandParticle var16 = new DripLandParticle(var2, var3, var5, var7, Fluids.LAVA, this.sprite.get(var15));
         ((DripParticle)var16).setColor(1.0F, 0.2857143F, 0.083333336F);
         return var16;
      }
   }

   public static class HoneyHangProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public HoneyHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripHangParticle var16 = new DripHangParticle(var2, var3, var5, var7, Fluids.EMPTY, ParticleTypes.FALLING_HONEY, this.sprite.get(var15));
         var16.gravity *= 0.01F;
         var16.lifetime = 100;
         var16.setColor(0.622F, 0.508F, 0.082F);
         return var16;
      }
   }

   public static class HoneyFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public HoneyFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         HoneyFallAndLandParticle var16 = new HoneyFallAndLandParticle(var2, var3, var5, var7, Fluids.EMPTY, ParticleTypes.LANDING_HONEY, this.sprite.get(var15));
         var16.gravity = 0.01F;
         ((DripParticle)var16).setColor(0.582F, 0.448F, 0.082F);
         return var16;
      }
   }

   public static class HoneyLandProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public HoneyLandProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripLandParticle var16 = new DripLandParticle(var2, var3, var5, var7, Fluids.EMPTY, this.sprite.get(var15));
         var16.lifetime = (int)(128.0 / ((double)var15.nextFloat() * 0.8 + 0.2));
         ((DripParticle)var16).setColor(0.522F, 0.408F, 0.082F);
         return var16;
      }
   }

   public static class DripstoneWaterHangProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DripstoneWaterHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripHangParticle var16 = new DripHangParticle(var2, var3, var5, var7, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER, this.sprite.get(var15));
         ((DripParticle)var16).setColor(0.2F, 0.3F, 1.0F);
         return var16;
      }
   }

   public static class DripstoneWaterFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DripstoneWaterFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripstoneFallAndLandParticle var16 = new DripstoneFallAndLandParticle(var2, var3, var5, var7, Fluids.WATER, ParticleTypes.SPLASH, this.sprite.get(var15));
         ((DripParticle)var16).setColor(0.2F, 0.3F, 1.0F);
         return var16;
      }
   }

   public static class DripstoneLavaHangProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DripstoneLavaHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         CoolingDripHangParticle var16 = new CoolingDripHangParticle(var2, var3, var5, var7, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA, this.sprite.get(var15));
         return var16;
      }
   }

   public static class DripstoneLavaFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DripstoneLavaFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripstoneFallAndLandParticle var16 = new DripstoneFallAndLandParticle(var2, var3, var5, var7, Fluids.LAVA, ParticleTypes.LANDING_LAVA, this.sprite.get(var15));
         ((DripParticle)var16).setColor(1.0F, 0.2857143F, 0.083333336F);
         return var16;
      }
   }

   public static class NectarFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public NectarFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FallingParticle var16 = new FallingParticle(var2, var3, var5, var7, Fluids.EMPTY, this.sprite.get(var15));
         var16.lifetime = (int)(16.0 / ((double)var15.nextFloat() * 0.8 + 0.2));
         var16.gravity = 0.007F;
         ((DripParticle)var16).setColor(0.92F, 0.782F, 0.72F);
         return var16;
      }
   }

   public static class SporeBlossomFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public SporeBlossomFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FallingParticle var16 = new FallingParticle(var2, var3, var5, var7, Fluids.EMPTY, this.sprite.get(var15));
         var16.lifetime = (int)(64.0F / Mth.randomBetween(var16.random, 0.1F, 0.9F));
         var16.gravity = 0.005F;
         ((DripParticle)var16).setColor(0.32F, 0.5F, 0.22F);
         return var16;
      }
   }

   public static class ObsidianTearHangProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public ObsidianTearHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripHangParticle var16 = new DripHangParticle(var2, var3, var5, var7, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR, this.sprite.get(var15));
         var16.isGlowing = true;
         var16.gravity *= 0.01F;
         var16.lifetime = 100;
         var16.setColor(0.51171875F, 0.03125F, 0.890625F);
         return var16;
      }
   }

   public static class ObsidianTearFallProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public ObsidianTearFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FallAndLandParticle var16 = new FallAndLandParticle(var2, var3, var5, var7, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR, this.sprite.get(var15));
         var16.isGlowing = true;
         var16.gravity = 0.01F;
         ((DripParticle)var16).setColor(0.51171875F, 0.03125F, 0.890625F);
         return var16;
      }
   }

   public static class ObsidianTearLandProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public ObsidianTearLandProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         DripLandParticle var16 = new DripLandParticle(var2, var3, var5, var7, Fluids.EMPTY, this.sprite.get(var15));
         var16.isGlowing = true;
         var16.lifetime = (int)(28.0 / ((double)var15.nextFloat() * 0.8 + 0.2));
         ((DripParticle)var16).setColor(0.51171875F, 0.03125F, 0.890625F);
         return var16;
      }
   }
}
