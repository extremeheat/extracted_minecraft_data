package net.minecraft.client.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class DripParticle extends TextureSheetParticle {
   private final Fluid type;

   private DripParticle(Level var1, double var2, double var4, double var6, Fluid var8) {
      super(var1, var2, var4, var6);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = var8;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float var1) {
      return this.type.is(FluidTags.LAVA) ? 240 : super.getLightColor(var1);
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
            this.xd *= 0.9800000190734863D;
            this.yd *= 0.9800000190734863D;
            this.zd *= 0.9800000190734863D;
            BlockPos var1 = new BlockPos(this.x, this.y, this.z);
            FluidState var2 = this.level.getFluidState(var1);
            if (var2.getType() == this.type && this.y < (double)((float)var1.getY() + var2.getHeight(this.level, var1))) {
               this.remove();
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

   // $FF: synthetic method
   DripParticle(Level var1, double var2, double var4, double var6, Fluid var8, Object var9) {
      this(var1, var2, var4, var6, var8);
   }

   public static class LavaLandProvider implements ParticleProvider<SimpleParticleType> {
      protected final SpriteSet sprite;

      public LavaLandProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle.DripLandParticle var15 = new DripParticle.DripLandParticle(var2, var3, var5, var7, Fluids.LAVA);
         var15.setColor(1.0F, 0.2857143F, 0.083333336F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   public static class LavaFallProvider implements ParticleProvider<SimpleParticleType> {
      protected final SpriteSet sprite;

      public LavaFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle.DripFallParticle var15 = new DripParticle.DripFallParticle(var2, var3, var5, var7, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
         var15.setColor(1.0F, 0.2857143F, 0.083333336F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   public static class LavaHangProvider implements ParticleProvider<SimpleParticleType> {
      protected final SpriteSet sprite;

      public LavaHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle.CoolingDripHangParticle var15 = new DripParticle.CoolingDripHangParticle(var2, var3, var5, var7, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   public static class WaterFallProvider implements ParticleProvider<SimpleParticleType> {
      protected final SpriteSet sprite;

      public WaterFallProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle.DripFallParticle var15 = new DripParticle.DripFallParticle(var2, var3, var5, var7, Fluids.WATER, ParticleTypes.SPLASH);
         var15.setColor(0.2F, 0.3F, 1.0F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   public static class WaterHangProvider implements ParticleProvider<SimpleParticleType> {
      protected final SpriteSet sprite;

      public WaterHangProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle.DripHangParticle var15 = new DripParticle.DripHangParticle(var2, var3, var5, var7, Fluids.WATER, ParticleTypes.FALLING_WATER);
         var15.setColor(0.2F, 0.3F, 1.0F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   static class DripLandParticle extends DripParticle {
      private DripLandParticle(Level var1, double var2, double var4, double var6, Fluid var8) {
         super(var1, var2, var4, var6, var8, null);
         this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      }

      // $FF: synthetic method
      DripLandParticle(Level var1, double var2, double var4, double var6, Fluid var8, Object var9) {
         this(var1, var2, var4, var6, var8);
      }
   }

   static class DripFallParticle extends DripParticle {
      private final ParticleOptions landParticle;

      private DripFallParticle(Level var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8, null);
         this.landParticle = var9;
         this.lifetime = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      }

      protected void postMoveUpdate() {
         if (this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
         }

      }

      // $FF: synthetic method
      DripFallParticle(Level var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, Object var10) {
         this(var1, var2, var4, var6, var8, var9);
      }
   }

   static class CoolingDripHangParticle extends DripParticle.DripHangParticle {
      private CoolingDripHangParticle(Level var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8, var9, null);
      }

      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
         this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
         super.preMoveUpdate();
      }

      // $FF: synthetic method
      CoolingDripHangParticle(Level var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, Object var10) {
         this(var1, var2, var4, var6, var8, var9);
      }
   }

   static class DripHangParticle extends DripParticle {
      private final ParticleOptions fallingParticle;

      private DripHangParticle(Level var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9) {
         super(var1, var2, var4, var6, var8, null);
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
         this.xd *= 0.02D;
         this.yd *= 0.02D;
         this.zd *= 0.02D;
      }

      // $FF: synthetic method
      DripHangParticle(Level var1, double var2, double var4, double var6, Fluid var8, ParticleOptions var9, Object var10) {
         this(var1, var2, var4, var6, var8, var9);
      }
   }
}
