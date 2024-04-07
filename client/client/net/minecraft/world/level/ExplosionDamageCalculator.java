package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class ExplosionDamageCalculator {
   public ExplosionDamageCalculator() {
      super();
   }

   public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5) {
      return var4.isAir() && var5.isEmpty() ? Optional.empty() : Optional.of(Math.max(var4.getBlock().getExplosionResistance(), var5.getExplosionResistance()));
   }

   public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
      return true;
   }

   public boolean shouldDamageEntity(Explosion var1, Entity var2) {
      return true;
   }

   public float getKnockbackMultiplier() {
      return 1.0F;
   }

   public float getEntityDamageAmount(Explosion var1, Entity var2) {
      float var3 = var1.radius() * 2.0F;
      Vec3 var4 = var1.center();
      double var5 = Math.sqrt(var2.distanceToSqr(var4)) / (double)var3;
      double var7 = (1.0 - var5) * (double)Explosion.getSeenPercent(var4, var2);
      return (float)((var7 * var7 + var7) / 2.0 * 7.0 * (double)var3 + 1.0);
   }
}
