package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FallingAttackBehavior implements LivingBlockBehavior {
   private final int fallHeight;
   private static final double ATTACK_DISTANCE = 2.0;

   public static LivingBlockBehaviorType setFallingAttackBehavior(final int fallHeight) {
      return LivingBlockBehaviorType.behaviorType((Function)((var1) -> new FallingAttackBehavior(fallHeight)));
   }

   public FallingAttackBehavior(final int fallHeight) {
      super();
      this.fallHeight = fallHeight;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.isAttacking();
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      Targetable attackTarget = entity.getAttackTarget();
      if (attackTarget == null) {
         return false;
      } else {
         if (attackTarget instanceof Entity) {
            Entity targetEntity = (Entity)attackTarget;
            Vec3 entityPos = entity.position();
            Vec3 targetCenter = targetEntity.position().add(0.0, (double)targetEntity.getBbHeight() * 0.5, 0.0);
            double distanceToTarget = entityPos.distanceTo(targetCenter);
            boolean isReadyToAttack = distanceToTarget <= 2.0;
            if (isReadyToAttack) {
               Vec3 attackPosition = targetEntity.position().add(0.0, (double)this.fallHeight, 0.0);
               BlockPos attackBlockPos = BlockPos.containing(attackPosition.x, attackPosition.y, attackPosition.z);
               if (level.getBlockState(attackBlockPos) == Blocks.AIR.defaultBlockState()) {
                  Block var14 = entity.getBlockState().getBlock();
                  if (var14 instanceof PointedDripstoneBlock) {
                     PointedDripstoneBlock dripstone = (PointedDripstoneBlock)var14;
                     BlockPos attackBlockPosAbove = BlockPos.containing(attackPosition.x, attackPosition.y + 1.0, attackPosition.z);
                     if (level.getBlockState(attackBlockPosAbove) == Blocks.AIR.defaultBlockState()) {
                        BlockState blockState = (BlockState)Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN);
                        entity.discard();
                        level.setBlockAndUpdate(attackBlockPos, blockState);
                        level.setBlockAndUpdate(attackBlockPosAbove, Blocks.DRIPSTONE_BLOCK.defaultBlockState());
                        level.setBlockAndUpdate(attackBlockPosAbove, Blocks.AIR.defaultBlockState());
                     }
                  } else {
                     BlockState blockState = entity.getBlockState();
                     entity.discard();
                     level.setBlockAndUpdate(attackBlockPos, blockState);
                  }
               }
            }
         }

         return false;
      }
   }
}
