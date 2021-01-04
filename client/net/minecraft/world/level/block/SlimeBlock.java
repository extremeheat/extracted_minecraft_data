package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SlimeBlock extends HalfTransparentBlock {
   public SlimeBlock(Block.Properties var1) {
      super(var1);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      if (var3.isSneaking()) {
         super.fallOn(var1, var2, var3, var4);
      } else {
         var3.causeFallDamage(var4, 0.0F);
      }

   }

   public void updateEntityAfterFallOn(BlockGetter var1, Entity var2) {
      if (var2.isSneaking()) {
         super.updateEntityAfterFallOn(var1, var2);
      } else {
         Vec3 var3 = var2.getDeltaMovement();
         if (var3.y < 0.0D) {
            double var4 = var2 instanceof LivingEntity ? 1.0D : 0.8D;
            var2.setDeltaMovement(var3.x, -var3.y * var4, var3.z);
         }
      }

   }

   public void stepOn(Level var1, BlockPos var2, Entity var3) {
      double var4 = Math.abs(var3.getDeltaMovement().y);
      if (var4 < 0.1D && !var3.isSneaking()) {
         double var6 = 0.4D + var4 * 0.2D;
         var3.setDeltaMovement(var3.getDeltaMovement().multiply(var6, 1.0D, var6));
      }

      super.stepOn(var1, var2, var3);
   }
}
