package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SlimeBlock extends HalfTransparentBlock {
   public static final MapCodec<SlimeBlock> CODEC = simpleCodec(SlimeBlock::new);

   public MapCodec<SlimeBlock> codec() {
      return CODEC;
   }

   public SlimeBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   public void fallOn(final Level level, final BlockState state, final BlockPos pos, final Entity entity, final double fallDistance) {
      if (!entity.isSuppressingBounce()) {
         entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
      }

   }

   public void updateEntityMovementAfterFallOn(final BlockGetter level, final Entity entity) {
      if (entity.isSuppressingBounce()) {
         super.updateEntityMovementAfterFallOn(level, entity);
      } else {
         this.bounceUp(entity);
      }

   }

   private void bounceUp(final Entity entity) {
      Vec3 movement = entity.getDeltaMovement();
      if (movement.y < 0.0) {
         double factor = entity instanceof LivingEntity ? 1.0 : 0.8;
         entity.setDeltaMovement(movement.x, -movement.y * factor, movement.z);
      }

   }

   public void stepOn(final Level level, final BlockPos pos, final BlockState onState, final Entity entity) {
      double absDeltaY = Math.abs(entity.getDeltaMovement().y);
      if (absDeltaY < 0.1 && !entity.isSteppingCarefully()) {
         double scale = 0.4 + absDeltaY * 0.2;
         entity.setDeltaMovement(entity.getDeltaMovement().multiply(scale, 1.0, scale));
      }

      super.stepOn(level, pos, onState, entity);
   }
}
