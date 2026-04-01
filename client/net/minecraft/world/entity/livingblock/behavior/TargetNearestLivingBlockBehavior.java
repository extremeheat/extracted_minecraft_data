package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class TargetNearestLivingBlockBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 15;
   private static final double LIVING_BLOCK_DISTANCE = 1.0;
   private final Intent<Target> moveTowards;
   private final Predicate<LivingBlock> predicate;
   private int lastTargetedTick = 0;

   public TargetNearestLivingBlockBehavior(final Intent<Target> moveTowards, final Predicate<LivingBlock> predicate) {
      super();
      this.moveTowards = moveTowards;
      this.predicate = predicate;
   }

   public static LivingBlockBehaviorType targetNearestLivingBlock(final Predicate<LivingBlock> predicate) {
      return LivingBlockBehaviorType.behaviorType((Function)((b) -> new TargetNearestLivingBlockBehavior(b.withIntentTo(LivingBlock.MOVE_TOWARDS), predicate)));
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.isIdle() && this.lastTargetedTick + 15 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      Vec3 pos = entity.position();
      LivingBlock nearestOfType = (LivingBlock)level.getNearestEntity(LivingBlock.class, TargetingConditions.livingBlock().selector((e, var2) -> {
         boolean var10000;
         if (e instanceof LivingBlock lb) {
            if (this.predicate.test(lb)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }), (LivingEntity)null, pos.x(), pos.y(), pos.z(), AABB.around(pos, 20.0));
      this.moveTowards.update(Target.nearEntity(nearestOfType, 1.0));
      this.lastTargetedTick = tickCount;
      return false;
   }
}
