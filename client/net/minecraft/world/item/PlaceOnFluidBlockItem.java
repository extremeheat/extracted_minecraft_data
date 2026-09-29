package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceOnFluidBlockItem extends BlockItem {
   public PlaceOnFluidBlockItem(final Block block, final Item.Properties properties) {
      super(block, properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      return InteractionResult.PASS;
   }

   public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
      BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
      BlockHitResult blockAboveResult = hitResult.withPosition(hitResult.getBlockPos().above());
      InteractionResult result = super.useOn(new UseOnContext(player, hand, blockAboveResult));
      if (result instanceof InteractionResult.Success success) {
         if (success.wasItemInteraction()) {
            player.awardStat(Stats.ITEM_USED.get(this));
         }
      }

      return result;
   }
}
