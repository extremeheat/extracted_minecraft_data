package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingBlockCommand;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.cognition.BuildTarget;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;

public class BuildAction extends CommandActionItem {
   public BuildAction(final Item.Properties properties) {
      super(properties, LivingBlockCommand.Type.TYPE_BUILD);
   }

   public boolean attackBlock(final Player player, final LivingBlock target) {
      if (player.isSpectator()) {
         return false;
      } else {
         Level level = player.level();
         if (!target.canBeControlledBy(player)) {
            return false;
         } else {
            return build(target, level) != InteractionResult.FAIL;
         }
      }
   }

   public static InteractionResult build(final LivingBlock target, final Level level) {
      BlockPos pos = target.blockPosition();
      if (!level.getBlockState(pos).canBeReplaced()) {
         return InteractionResult.FAIL;
      } else {
         BlockState blockState = target.getBlockState();
         if (blockState.isAir()) {
            return InteractionResult.FAIL;
         } else if (!level.isClientSide()) {
            level.setBlock(pos, blockState, 2);
            TagValueOutput fullOutput = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            target.save(fullOutput);
            Tag output = fullOutput.buildResult().get("container_behavior_data");
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && output instanceof CompoundTag) {
               CompoundTag tag = (CompoundTag)output;
               ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, target.registryAccess(), tag);
               blockEntity.loadWithComponents(input);
            }

            level.levelEvent(4000, pos, 0);
            level.playSound((Entity)null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS);
            target.discard();
            return InteractionResult.SUCCESS_SERVER;
         } else {
            return InteractionResult.SUCCESS;
         }
      }
   }

   public void command(final LivingBlock block, final ServerPlayer player, final Vec3 pos, final BlockPos blockPos, final Direction direction) {
      BlockState blockState = block.getBlockState();
      if (!blockState.isAir()) {
         block.hopesAndDreams.desire(Desires.BUILD, new BuildTarget(blockPos, direction));
      }
   }
}
