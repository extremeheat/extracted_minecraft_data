package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class ExplosionDamageCalculator {
   public ExplosionDamageCalculator() {
      super();
   }

   public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5) {
      return var4.isAir() && var5.isEmpty()
         ? Optional.empty()
         : Optional.of(Math.max(var4.getBlock().getExplosionResistance(), var5.getExplosionResistance()));
   }

   public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
      return true;
   }
}
