package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SlimeBlock extends HalfTransparentBlock {
   public SlimeBlock(Block.Properties var1) {
      super(var1);
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      if (var3.isSuppressingBounce()) {
         super.fallOn(var1, var2, var3, var4);
      } else {
         var3.causeFallDamage(var4, 0.0F);
      }

   }

   public void updateEntityAfterFallOn(BlockGetter var1, Entity var2) {
      if (var2.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(var1, var2);
      } else {
         this.bounceUp(var2);
      }

   }

   private void bounceUp(Entity var1) {
      Vec3 var2 = var1.getDeltaMovement();
      if (var2.y < 0.0D) {
         double var3 = var1 instanceof LivingEntity ? 1.0D : 0.8D;
         var1.setDeltaMovement(var2.x, -var2.y * var3, var2.z);
      }

   }

   public void stepOn(Level var1, BlockPos var2, Entity var3) {
      double var4 = Math.abs(var3.getDeltaMovement().y);
      if (var4 < 0.1D && !var3.isSteppingCarefully()) {
         double var6 = 0.4D + var4 * 0.2D;
         var3.setDeltaMovement(var3.getDeltaMovement().multiply(var6, 1.0D, var6));
      }

      super.stepOn(var1, var2, var3);
   }
}
