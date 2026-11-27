package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class PlaceOnFluidBlockItem extends BlockItem {
   public PlaceOnFluidBlockItem(final Block block, final Item.Properties properties) {
      super(block, properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      return InteractionResult.PASS;
   }

   public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
      BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
      if (hitResult.getType() == HitResult.Type.MISS) {
         return super.use(level, player, hand);
      } else {
         BlockPos placeAt = hitResult.getBlockPos().above();
         BlockHitResult blockAboveResult = hitResult.withPosition(placeAt);
         InteractionResult result = super.useOn(new UseOnContext(player, hand, blockAboveResult));
         if (result instanceof InteractionResult.Success) {
            InteractionResult.Success success = (InteractionResult.Success)result;
            if (success.wasItemInteraction()) {
               player.awardStat(Stats.ITEM_USED.get(this));
               return result;
            }
         }

         if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            if (!result.consumesAction()) {
               int maxY = level.getMaxY();
               if (placeAt.getY() > maxY) {
                  serverPlayer.sendBuildLimitMessage(true, maxY);
               }
            }
         }

         return result;
      }
   }
}
