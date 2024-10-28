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

   public float getKnockbackMultiplier(Entity var1) {
      return 1.0F;
   }

   public float getEntityDamageAmount(Explosion var1, Entity var2, float var3) {
      float var4 = var1.radius() * 2.0F;
      Vec3 var5 = var1.center();
      double var6 = Math.sqrt(var2.distanceToSqr(var5)) / (double)var4;
      double var8 = (1.0 - var6) * (double)var3;
      return (float)((var8 * var8 + var8) / 2.0 * 7.0 * (double)var4 + 1.0);
   }
}
