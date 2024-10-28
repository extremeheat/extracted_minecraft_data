package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DripParticle extends TextureSheetParticle {
   private final Fluid type;
   protected boolean isGlowing;

   DripParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8) {
      super(var1, var2, var4, var6);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = var8;
   }

   protected Fluid getType() {
      return this.type;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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

   public static TextureSheetParticle createWaterHangParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripHangParticle var14 = new DripHangParticle(var1, var2, var4, var6, Fluids.WATER, ParticleTypes.FALLING_WATER);
      ((DripParticle)var14).setColor(0.2F, 0.3F, 1.0F);
      return var14;
   }

   public static TextureSheetParticle createWaterFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      FallAndLandParticle var14 = new FallAndLandParticle(var1, var2, var4, var6, Fluids.WATER, ParticleTypes.SPLASH);
      ((DripParticle)var14).setColor(0.2F, 0.3F, 1.0F);
      return var14;
   }

   public static TextureSheetParticle createLavaHangParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      return new CoolingDripHangParticle(var1, var2, var4, var6, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
   }

   public static TextureSheetParticle createLavaFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      FallAndLandParticle var14 = new FallAndLandParticle(var1, var2, var4, var6, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
      ((DripParticle)var14).setColor(1.0F, 0.2857143F, 0.083333336F);
      return var14;
   }

   public static TextureSheetParticle createLavaLandParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripLandParticle var14 = new DripLandParticle(var1, var2, var4, var6, Fluids.LAVA);
      ((DripParticle)var14).setColor(1.0F, 0.2857143F, 0.083333336F);
      return var14;
   }

   public static TextureSheetParticle createHoneyHangParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripHangParticle var14 = new DripHangParticle(var1, var2, var4, var6, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
      var14.gravity *= 0.01F;
      var14.lifetime = 100;
      var14.setColor(0.622F, 0.508F, 0.082F);
      return var14;
   }

   public static TextureSheetParticle createHoneyFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      HoneyFallAndLandParticle var14 = new HoneyFallAndLandParticle(var1, var2, var4, var6, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
      var14.gravity = 0.01F;
      ((DripParticle)var14).setColor(0.582F, 0.448F, 0.082F);
      return var14;
   }

   public static TextureSheetParticle createHoneyLandParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripLandParticle var14 = new DripLandParticle(var1, var2, var4, var6, Fluids.EMPTY);
      var14.lifetime = (int)(128.0 / (Math.random() * 0.8 + 0.2));
      ((DripParticle)var14).setColor(0.522F, 0.408F, 0.082F);
      return var14;
   }

   public static TextureSheetParticle createDripstoneWaterHangParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripHangParticle var14 = new DripHangParticle(var1, var2, var4, var6, Fluids.WATER, ParticleTypes.FALLING_DRIPSTONE_WATER);
      ((DripParticle)var14).setColor(0.2F, 0.3F, 1.0F);
      return var14;
   }

   public static TextureSheetParticle createDripstoneWaterFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripstoneFallAndLandParticle var14 = new DripstoneFallAndLandParticle(var1, var2, var4, var6, Fluids.WATER, ParticleTypes.SPLASH);
      ((DripParticle)var14).setColor(0.2F, 0.3F, 1.0F);
      return var14;
   }

   public static TextureSheetParticle createDripstoneLavaHangParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      return new CoolingDripHangParticle(var1, var2, var4, var6, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA);
   }

   public static TextureSheetParticle createDripstoneLavaFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripstoneFallAndLandParticle var14 = new DripstoneFallAndLandParticle(var1, var2, var4, var6, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
      ((DripParticle)var14).setColor(1.0F, 0.2857143F, 0.083333336F);
      return var14;
   }

   public static TextureSheetParticle createNectarFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      FallingParticle var14 = new FallingParticle(var1, var2, var4, var6, Fluids.EMPTY);
      var14.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      var14.gravity = 0.007F;
      ((DripParticle)var14).setColor(0.92F, 0.782F, 0.72F);
      return var14;
   }

   public static TextureSheetParticle createSporeBlossomFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      int var14 = (int)(64.0F / Mth.randomBetween(var1.getRandom(), 0.1F, 0.9F));
      FallingParticle var15 = new FallingParticle(var1, var2, var4, var6, Fluids.EMPTY, var14);
      var15.gravity = 0.005F;
      ((DripParticle)var15).setColor(0.32F, 0.5F, 0.22F);
      return var15;
   }

   public static TextureSheetParticle createObsidianTearHangParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripHangParticle var14 = new DripHangParticle(var1, var2, var4, var6, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
      var14.isGlowing = true;
      var14.gravity *= 0.01F;
      var14.lifetime = 100;
      var14.setColor(0.51171875F, 0.03125F, 0.890625F);
      return var14;
   }

   public static TextureSheetParticle createObsidianTearFallParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      FallAndLandParticle var14 = new FallAndLandParticle(var1, var2, var4, var6, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
      var14.isGlowing = true;
      var14.gravity = 0.01F;
      ((DripParticle)var14).setColor(0.51171875F, 0.03125F, 0.890625F);
      return var14;
   }

   public static TextureSheetParticle createObsidianTearLandParticle(SimpleParticleType var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      DripLandParticle var14 = new DripLandParticle(var1, var2, var4, var6, Fluids.EMPTY);
      var14.isGlowing = true;
      var14.lifetime = (int)(28.0 / (Math.random() * 0.8 + 0.2));
      ((DripParticle)var14).setColor(0.51171875F, 0.03125F, 0.890625F);
      return var14;
   }

   private static class DripHangParticle extends DripParticle {
      private final ParticleOptions fallingParticle;

      DripHangParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8);
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

   private static class FallAndLandParticle extends FallingParticle {
      protected final ParticleOptions landParticle;

      FallAndLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8);
         this.landParticle = var9;
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
         }

      }
   }

   static class CoolingDripHangParticle extends DripHangParticle {
      CoolingDripHangParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8, var9);
      }

      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
         this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
         super.preMoveUpdate();
      }
   }

   static class DripLandParticle extends DripParticle {
      DripLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8) {
         super(var1, var2, var4, var6, var8);
         this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      }
   }

   static class HoneyFallAndLandParticle extends FallAndLandParticle {
      HoneyFallAndLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8, var9);
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

   private static class DripstoneFallAndLandParticle extends FallAndLandParticle {
      DripstoneFallAndLandParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8, var9);
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
      FallingParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8) {
         this(var1, var2, var4, var6, var8, (int)(64.0 / (Math.random() * 0.8 + 0.2)));
      }

      FallingParticle(ClientLevel var1, double var2, double var4, double var6, Fluid var8, int var9) {
         super(var1, var2, var4, var6, var8);
         this.lifetime = var9;
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
         }

      }
   }
}
