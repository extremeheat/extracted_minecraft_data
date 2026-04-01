package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingBlockCommand;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FollowAction extends CommandActionItem {
   private static final double PLAYER_DISTANCE = 2.0;

   public FollowAction(final Item.Properties properties) {
      super(properties, LivingBlockCommand.Type.TYPE_FOLLOW);
   }

   public boolean actionOnBlock(final Player player, final BlockPos pos, final Direction direction) {
      return player.isSpectator() ? false : this.action(player);
   }

   public boolean actionOnEntity(final Player player, final Entity entity) {
      return player.isSpectator() ? false : this.action(player);
   }

   public boolean actionOnNothing(final Player player) {
      return player.isSpectator() ? false : this.action(player);
   }

   public boolean action(final Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         Vec3 pos = player.position();
         if (this.applyCommand(serverPlayer, pos, (livingBlock) -> command(serverPlayer, livingBlock))) {
            player.level().playSound((Entity)null, pos.x(), pos.y(), pos.z(), SoundEvents.LEAD_TIED, SoundSource.PLAYERS);
         }
      }

      return true;
   }

   private static void command(final ServerPlayer serverPlayer, final LivingBlock livingBlock) {
      livingBlock.setLeashedTo(serverPlayer, true);
      livingBlock.hopesAndDreams.desire(Desires.APPROACH, Target.followingEntity(serverPlayer, 2.0));
   }

   public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
      if (player instanceof ServerPlayer serverPlayer) {
         boolean anyRemoved = false;

         for(Leashable leashable : Leashable.leashableLeashedTo(serverPlayer)) {
            if (leashable instanceof LivingBlock livingBlock) {
               livingBlock.removeLeash();
               livingBlock.interrupt();
               anyRemoved = true;
            }
         }

         if (anyRemoved) {
            Vec3 pos = player.position();
            level.playSound((Entity)null, pos.x(), pos.y(), pos.z(), SoundEvents.LEAD_UNTIED, SoundSource.PLAYERS);
            return InteractionResult.SUCCESS_SERVER;
         }
      }

      return InteractionResult.FAIL;
   }
}
