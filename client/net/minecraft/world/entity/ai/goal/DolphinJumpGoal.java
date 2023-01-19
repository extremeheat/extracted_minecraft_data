package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class DolphinJumpGoal extends JumpGoal {
   private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7};
   private final Dolphin dolphin;
   private final int interval;
   private boolean breached;

   public DolphinJumpGoal(Dolphin var1, int var2) {
      super();
      this.dolphin = var1;
      this.interval = reducedTickDelay(var2);
   }

   @Override
   public boolean canUse() {
      if (this.dolphin.getRandom().nextInt(this.interval) != 0) {
         return false;
      } else {
         Direction var1 = this.dolphin.getMotionDirection();
         int var2 = var1.getStepX();
         int var3 = var1.getStepZ();
         BlockPos var4 = this.dolphin.blockPosition();

         for(int var8 : STEPS_TO_CHECK) {
            if (!this.waterIsClear(var4, var2, var3, var8) || !this.surfaceIsClear(var4, var2, var3, var8)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean waterIsClear(BlockPos var1, int var2, int var3, int var4) {
      BlockPos var5 = var1.offset(var2 * var4, 0, var3 * var4);
      return this.dolphin.level.getFluidState(var5).is(FluidTags.WATER) && !this.dolphin.level.getBlockState(var5).getMaterial().blocksMotion();
   }

   private boolean surfaceIsClear(BlockPos var1, int var2, int var3, int var4) {
      return this.dolphin.level.getBlockState(var1.offset(var2 * var4, 1, var3 * var4)).isAir()
         && this.dolphin.level.getBlockState(var1.offset(var2 * var4, 2, var3 * var4)).isAir();
   }

   @Override
   public boolean canContinueToUse() {
      double var1 = this.dolphin.getDeltaMovement().y;
      return (
            !(var1 * var1 < 0.029999999329447746)
               || this.dolphin.getXRot() == 0.0F
               || !(Math.abs(this.dolphin.getXRot()) < 10.0F)
               || !this.dolphin.isInWater()
         )
         && !this.dolphin.isOnGround();
   }

   @Override
   public boolean isInterruptable() {
      return false;
   }

   @Override
   public void start() {
      Direction var1 = this.dolphin.getMotionDirection();
      this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add((double)var1.getStepX() * 0.6, 0.7, (double)var1.getStepZ() * 0.6));
      this.dolphin.getNavigation().stop();
   }

   @Override
   public void stop() {
      this.dolphin.setXRot(0.0F);
   }

   @Override
   public void tick() {
      boolean var1 = this.breached;
      if (!var1) {
         FluidState var2 = this.dolphin.level.getFluidState(this.dolphin.blockPosition());
         this.breached = var2.is(FluidTags.WATER);
      }

      if (this.breached && !var1) {
         this.dolphin.playSound(SoundEvents.DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      Vec3 var7 = this.dolphin.getDeltaMovement();
      if (var7.y * var7.y < 0.029999999329447746 && this.dolphin.getXRot() != 0.0F) {
         this.dolphin.setXRot(Mth.rotlerp(this.dolphin.getXRot(), 0.0F, 0.2F));
      } else if (var7.length() > 9.999999747378752E-6) {
         double var3 = var7.horizontalDistance();
         double var5 = Math.atan2(-var7.y, var3) * 57.2957763671875;
         this.dolphin.setXRot((float)var5);
      }
   }
}
