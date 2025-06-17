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
   public LeadItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(BlockTags.FENCES)) {
         Player var5 = var1.getPlayer();
         if (!var2.isClientSide && var5 != null) {
            return bindPlayerMobs(var5, var2, var3);
         }
      }

      return InteractionResult.PASS;
   }

   public static InteractionResult bindPlayerMobs(Player var0, Level var1, BlockPos var2) {
      LeashFenceKnotEntity var3 = null;
      List var4 = Leashable.leashableInArea(var1, Vec3.atCenterOf(var2), (var1x) -> var1x.getLeashHolder() == var0);
      boolean var5 = false;

      for(Leashable var7 : var4) {
         if (var3 == null) {
            var3 = LeashFenceKnotEntity.getOrCreateKnot(var1, var2);
            var3.playPlacementSound();
         }

         if (var7.canHaveALeashAttachedTo(var3)) {
            var7.setLeashedTo(var3, true);
            var5 = true;
         }
      }

      if (var5) {
         var1.gameEvent(GameEvent.BLOCK_ATTACH, var2, GameEvent.Context.of((Entity)var0));
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.PASS;
      }
   }
}
