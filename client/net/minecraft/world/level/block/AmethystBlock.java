package net.minecraft.world.level.block;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AmethystBlock extends Block {
   public AmethystBlock(final BlockBehaviour.Properties props) {
      super(props);
   }

   protected void onProjectileHit(final Level level, final BlockState state, final BlockHitResult hitResult, final Projectile projectile) {
      AmethystSoundUtils.onProjectileHit(level, state, hitResult);
   }
}
