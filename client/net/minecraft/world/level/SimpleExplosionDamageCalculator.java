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

   public SimpleExplosionDamageCalculator(final boolean explodesBlocks, final boolean damagesEntities, final Optional<Float> knockbackMultiplier, final Optional<HolderSet<Block>> immuneBlocks) {
      super();
      this.explodesBlocks = explodesBlocks;
      this.damagesEntities = damagesEntities;
      this.knockbackMultiplier = knockbackMultiplier;
      this.immuneBlocks = immuneBlocks;
   }

   public Optional<Float> getBlockExplosionResistance(final Explosion explosion, final BlockGetter level, final BlockPos pos, final BlockState block, final FluidState fluid) {
      if (this.immuneBlocks.isPresent()) {
         return block.is((HolderSet)this.immuneBlocks.get()) ? Optional.of(3600000.0F) : Optional.empty();
      } else {
         return super.getBlockExplosionResistance(explosion, level, pos, block, fluid);
      }
   }

   public boolean shouldBlockExplode(final Explosion explosion, final BlockGetter level, final BlockPos pos, final BlockState state, final float power) {
      return this.explodesBlocks;
   }

   public boolean shouldDamageEntity(final Explosion explosion, final Entity entity) {
      return this.damagesEntities;
   }

   public float getKnockbackMultiplier(final Entity entity) {
      boolean var10000;
      label17: {
         if (entity instanceof Player player) {
            if (player.getAbilities().flying) {
               var10000 = true;
               break label17;
            }
         }

         var10000 = false;
      }

      boolean creativeFlying = var10000;
      return creativeFlying ? 0.0F : (Float)this.knockbackMultiplier.orElseGet(() -> super.getKnockbackMultiplier(entity));
   }
}
