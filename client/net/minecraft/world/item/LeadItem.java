package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
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
import net.minecraft.world.phys.AABB;

public class LeadItem extends Item {
   public LeadItem(Item.Properties var1) {
      super(var1);
   }

   @Override
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
      List var4 = leashableInArea(var1, var2, var1x -> var1x.getLeashHolder() == var0);

      for (Leashable var6 : var4) {
         if (var3 == null) {
            var3 = LeashFenceKnotEntity.getOrCreateKnot(var1, var2);
            var3.playPlacementSound();
         }

         var6.setLeashedTo(var3, true);
      }

      if (!var4.isEmpty()) {
         var1.gameEvent(GameEvent.BLOCK_ATTACH, var2, GameEvent.Context.of(var0));
         return InteractionResult.SUCCESS_SERVER;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static List<Leashable> leashableInArea(Level var0, BlockPos var1, Predicate<Leashable> var2) {
      double var3 = 7.0;
      int var5 = var1.getX();
      int var6 = var1.getY();
      int var7 = var1.getZ();
      AABB var8 = new AABB((double)var5 - 7.0, (double)var6 - 7.0, (double)var7 - 7.0, (double)var5 + 7.0, (double)var6 + 7.0, (double)var7 + 7.0);
      return var0.getEntitiesOfClass(Entity.class, var8, var1x -> {
         if (var1x instanceof Leashable var2x && var2.test(var2x)) {
            return true;
         }

         return false;
      }).stream().map(Leashable.class::cast).toList();
   }
}
