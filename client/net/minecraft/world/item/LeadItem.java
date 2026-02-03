package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class LeadItem extends Item {
   public LeadItem(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      BlockState state = level.getBlockState(pos);
      if (state.is(BlockTags.FENCES)) {
         Player player = context.getPlayer();
         if (!level.isClientSide() && player != null) {
            return bindPlayerMobs(player, level, pos);
         }
      }

      return InteractionResult.PASS;
   }

   public static InteractionResult bindPlayerMobs(final Player player, final Level level, final BlockPos pos) {
      LeashFenceKnotEntity activeKnot = null;
      List<Leashable> entitiesToLeash = Leashable.leashableInArea(level, Vec3.atCenterOf(pos), (l) -> l.getLeashHolder() == player);
      boolean anyLeashed = false;

      for(Leashable leashable : entitiesToLeash) {
         if (activeKnot == null) {
            activeKnot = LeashFenceKnotEntity.getOrCreateKnot(level, pos);
            activeKnot.playPlacementSound();
         }

         if (leashable.canHaveALeashAttachedTo(activeKnot)) {
            leashable.setLeashedTo(activeKnot, true);
            anyLeashed = true;
         }
      }

      if (anyLeashed) {
         level.gameEvent(GameEvent.BLOCK_ATTACH, pos, GameEvent.Context.of((Entity)player));
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.PASS;
      }
   }
}
