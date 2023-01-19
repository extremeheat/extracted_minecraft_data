package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SlimeBlock extends HalfTransparentBlock {
   public SlimeBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      if (var4.isSuppressingBounce()) {
         super.fallOn(var1, var2, var3, var4, var5);
      } else {
         var4.causeFallDamage(var5, 0.0F, DamageSource.FALL);
      }
   }

   @Override
   public void updateEntityAfterFallOn(BlockGetter var1, Entity var2) {
      if (var2.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(var1, var2);
      } else {
         this.bounceUp(var2);
      }
   }

   private void bounceUp(Entity var1) {
      Vec3 var2 = var1.getDeltaMovement();
      if (var2.y < 0.0) {
         double var3 = var1 instanceof LivingEntity ? 1.0 : 0.8;
         var1.setDeltaMovement(var2.x, -var2.y * var3, var2.z);
      }
   }

   @Override
   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      double var5 = Math.abs(var4.getDeltaMovement().y);
      if (var5 < 0.1 && !var4.isSteppingCarefully()) {
         double var7 = 0.4 + var5 * 0.2;
         var4.setDeltaMovement(var4.getDeltaMovement().multiply(var7, 1.0, var7));
      }

      super.stepOn(var1, var2, var3, var4);
   }
}
