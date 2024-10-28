package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

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
            bindPlayerMobs(var5, var2, var3);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static InteractionResult bindPlayerMobs(Player var0, Level var1, BlockPos var2) {
      LeashFenceKnotEntity var3 = null;
      double var4 = 7.0;
      int var6 = var2.getX();
      int var7 = var2.getY();
      int var8 = var2.getZ();
      AABB var9 = new AABB((double)var6 - 7.0, (double)var7 - 7.0, (double)var8 - 7.0, (double)var6 + 7.0, (double)var7 + 7.0, (double)var8 + 7.0);
      List var10 = var1.getEntitiesOfClass(Mob.class, var9, (var1x) -> {
         return var1x.getLeashHolder() == var0;
      });

      Mob var12;
      for(Iterator var11 = var10.iterator(); var11.hasNext(); var12.setLeashedTo(var3, true)) {
         var12 = (Mob)var11.next();
         if (var3 == null) {
            var3 = LeashFenceKnotEntity.getOrCreateKnot(var1, var2);
            var3.playPlacementSound();
         }
      }

      if (!var10.isEmpty()) {
         var1.gameEvent(GameEvent.BLOCK_ATTACH, var2, GameEvent.Context.of((Entity)var0));
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }
}
