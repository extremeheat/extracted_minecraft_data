package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class EntityBasedExplosionDamageCalculator extends ExplosionDamageCalculator {
   private final Entity source;

   public EntityBasedExplosionDamageCalculator(Entity var1) {
      super();
      this.source = var1;
   }

   public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5) {
      return super.getBlockExplosionResistance(var1, var2, var3, var4, var5).map((var6) -> {
         return this.source.getBlockExplosionResistance(var1, var2, var3, var4, var5, var6);
      });
   }

   public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
      return this.source.shouldBlockExplode(var1, var2, var3, var4, var5);
   }
}
