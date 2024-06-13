package net.minecraft.world.level;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SimpleExplosionDamageCalculator extends ExplosionDamageCalculator {
   private final boolean explodesBlocks;
   private final boolean damagesEntities;
   private final Optional<Float> knockbackMultiplier;
   private final Optional<HolderSet<Block>> immuneBlocks;

   public SimpleExplosionDamageCalculator(boolean var1, boolean var2, Optional<Float> var3, Optional<HolderSet<Block>> var4) {
      super();
      this.explodesBlocks = var1;
      this.damagesEntities = var2;
      this.knockbackMultiplier = var3;
      this.immuneBlocks = var4;
   }

   @Override
   public Optional<Float> getBlockExplosionResistance(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, FluidState var5) {
      if (this.immuneBlocks.isPresent()) {
         return var4.is(this.immuneBlocks.get()) ? Optional.of(3600000.0F) : Optional.empty();
      } else {
         return super.getBlockExplosionResistance(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public boolean shouldBlockExplode(Explosion var1, BlockGetter var2, BlockPos var3, BlockState var4, float var5) {
      return this.explodesBlocks;
   }

   @Override
   public boolean shouldDamageEntity(Explosion var1, Entity var2) {
      return this.damagesEntities;
   }

   @Override
   public float getKnockbackMultiplier(Entity var1) {
      boolean var10000;
      label17: {
         if (var1 instanceof Player var3 && var3.getAbilities().flying) {
            var10000 = true;
            break label17;
         }

         var10000 = false;
      }

      boolean var2 = var10000;
      return var2 ? 0.0F : this.knockbackMultiplier.orElseGet(() -> super.getKnockbackMultiplier(var1));
   }
}
