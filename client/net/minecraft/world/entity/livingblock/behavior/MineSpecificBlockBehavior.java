package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.livingblock.cognition.Prize;
import net.minecraft.world.phys.Vec3;

public class MineSpecificBlockBehavior extends BlockMiningBehavior implements LivingBlockBehavior {
   private static final float MAX_DISTANCE_FROM_BLOCK = 3.0F;
   private static final float APPROACH_DISTANCE = 2.0F;
   private final Prize<BlockPos> target;
   private final Intent<Target> moveTowards;
   public static final LivingBlockBehaviorType MOVE_TO_BLOCK_TO_MINE;
   public static LivingBlockBehaviorType MINE_THE_BLOCK;

   private MineSpecificBlockBehavior(final Prize<BlockPos> target, final Intent<Target> moveTowards) {
      super();
      this.target = target;
      this.moveTowards = moveTowards;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.position().distanceTo(Vec3.atCenterOf(this.target.get())) < 3.0 && canMine(entity, this.target.get());
   }

   public void onStart(final LivingBlock entity) {
      this.moveTowards.clear();
      this.startMining();
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      boolean stillMining = this.mineBlockTick(entity, level, this.target.get());
      if (!stillMining) {
         this.target.forget();
      }

      return stillMining;
   }

   static {
      MOVE_TO_BLOCK_TO_MINE = ActOnDesireBehavior.actOnDesire(Desires.MINE, LivingBlock.MOVE_TOWARDS, (pos) -> Target.near(Vec3.atCenterOf(pos), 2.0), (e, pos) -> e.position().distanceTo(Vec3.atCenterOf(pos)) > 2.0 && canMine(e, pos), false);
      MINE_THE_BLOCK = LivingBlockBehaviorType.behaviorType((Function)((a) -> new MineSpecificBlockBehavior(a.inPursuitOf(Desires.MINE), a.withIntentTo(LivingBlock.MOVE_TOWARDS))));
   }
}
