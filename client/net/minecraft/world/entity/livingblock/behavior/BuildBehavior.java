package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.BuildTarget;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.entity.livingblock.cognition.Prize;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BuildBehavior implements LivingBlockBehavior {
   private static final int MAX_BUILD_OFFSET = 16;
   private static final float GOOD_ENOUGH_DISTANCE = 1.0F;
   private static final float INSTANT_BUILD_DISTANCE = 0.1F;
   private final Prize<BuildTarget> target;
   private final Intent<Target> move;
   private @Nullable BlockPos currentTarget;
   private double lastDistance;
   public static LivingBlockBehaviorType BUILD = LivingBlockBehaviorType.behaviorType((Function)((a) -> new BuildBehavior(a.inPursuitOf(Desires.BUILD), a.withIntentTo(LivingBlock.MOVE_TOWARDS))));

   private BuildBehavior(final Prize<BuildTarget> target, final Intent<Target> move) {
      super();
      this.target = target;
      this.move = move;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return !entity.getBlockState().isAir();
   }

   public void onStart(final LivingBlock entity) {
      this.currentTarget = null;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (this.currentTarget == null || !level.getBlockState(this.currentTarget).canBeReplaced()) {
         this.currentTarget = this.findTarget(level);
         if (this.currentTarget == null) {
            this.target.forget();
            return false;
         }

         this.move.update(Target.exactlyAt(Vec3.atBottomCenterOf(this.currentTarget)));
      }

      BlockState blockState = entity.getBlockState();
      if (blockState.isAir()) {
         this.target.forget();
         return false;
      } else {
         double distance = entity.position().distanceTo(this.currentTarget.getBottomCenter());
         if (!(distance < 0.10000000149011612) && (!(distance < 1.0) || !(distance > this.lastDistance))) {
            this.lastDistance = distance;
            return true;
         } else {
            build(entity, blockState, level, this.currentTarget);
            return false;
         }
      }
   }

   private @Nullable BlockPos findTarget(final ServerLevel level) {
      BuildTarget target = this.target.get();
      BlockPos.MutableBlockPos pos = target.pos().mutable();

      for(int tries = 0; !level.getBlockState(pos).canBeReplaced() && tries < 16; ++tries) {
         pos.move(target.direction());
      }

      return level.getBlockState(pos).canBeReplaced() ? pos.immutable() : null;
   }

   private static void build(final LivingBlock target, final BlockState blockState, final ServerLevel level, final BlockPos pos) {
      level.setBlock(pos, blockState, 2);
      TagValueOutput fullOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
      target.save(fullOutput);
      Tag output = fullOutput.buildResult().get("container_behavior_data");
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity != null && output instanceof CompoundTag tag) {
         ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, target.registryAccess(), tag);
         blockEntity.loadWithComponents(input);
      }

      level.levelEvent(4000, pos, BuiltInRegistries.BLOCK.getId(blockState.getBlock()));
      level.playSound((Entity)null, pos, blockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS);
      target.discard();
   }
}
