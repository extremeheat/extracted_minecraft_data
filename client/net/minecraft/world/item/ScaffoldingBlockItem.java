package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ScaffoldingBlockItem extends BlockItem {
   public ScaffoldingBlockItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   @Nullable
   public BlockPlaceContext updatePlacementContext(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      BlockState var4 = var3.getBlockState(var2);
      Block var5 = this.getBlock();
      if (!var4.is(var5)) {
         return ScaffoldingBlock.getDistance(var3, var2) == 7 ? null : var1;
      } else {
         Direction var6;
         if (var1.isSecondaryUseActive()) {
            var6 = var1.isInside() ? var1.getClickedFace().getOpposite() : var1.getClickedFace();
         } else {
            var6 = var1.getClickedFace() == Direction.UP ? var1.getHorizontalDirection() : Direction.UP;
         }

         int var7 = 0;
         BlockPos.MutableBlockPos var8 = var2.mutable().move(var6);

         while(var7 < 7) {
            if (!var3.isClientSide && !var3.isInWorldBounds(var8)) {
               Player var9 = var1.getPlayer();
               int var10 = var3.getMaxBuildHeight();
               if (var9 instanceof ServerPlayer && var8.getY() >= var10) {
                  ((ServerPlayer)var9).sendSystemMessage(Component.translatable("build.tooHigh", var10 - 1).withStyle(ChatFormatting.RED), true);
               }
               break;
            }

            var4 = var3.getBlockState(var8);
            if (!var4.is(this.getBlock())) {
               if (var4.canBeReplaced(var1)) {
                  return BlockPlaceContext.at(var1, var8, var6);
               }
               break;
            }

            var8.move(var6);
            if (var6.getAxis().isHorizontal()) {
               ++var7;
            }
         }

         return null;
      }
   }

   protected boolean mustSurvive() {
      return false;
   }
}
