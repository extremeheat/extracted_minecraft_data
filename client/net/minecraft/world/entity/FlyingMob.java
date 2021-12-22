package net.minecraft.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class FlyingMob extends Mob {
   protected FlyingMob(EntityType<? extends FlyingMob> var1, Level var2) {
      super(var1, var2);
   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public void travel(Vec3 var1) {
      if (this.isInWater()) {
         this.moveRelative(0.02F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929D));
      } else if (this.isInLava()) {
         this.moveRelative(0.02F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
      } else {
         float var2 = 0.91F;
         if (this.onGround) {
            var2 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.91F;
         }

         float var3 = 0.16277137F / (var2 * var2 * var2);
         var2 = 0.91F;
         if (this.onGround) {
            var2 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getBlock().getFriction() * 0.91F;
         }

         this.moveRelative(this.onGround ? 0.1F * var3 : 0.02F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale((double)var2));
      }

      this.calculateEntityAnimation(this, false);
   }

   public boolean onClimbable() {
      return false;
   }
}
