package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class SignItem extends StandingAndWallBlockItem {
   public SignItem(final Block sign, final Block wallSign, final Item.Properties properties) {
      super(sign, wallSign, Direction.DOWN, properties);
   }

   public SignItem(final Item.Properties properties, final Block sign, final Block wallSign, final Direction direction) {
      super(sign, wallSign, direction, properties);
   }

   protected boolean updateCustomBlockEntityTag(final BlockPos pos, final Level level, final @Nullable Player player, final ItemStack itemStack, final BlockState placedState) {
      boolean success = super.updateCustomBlockEntityTag(pos, level, player, itemStack, placedState);
      if (!level.isClientSide() && !success && player != null) {
         BlockEntity var9 = level.getBlockEntity(pos);
         if (var9 instanceof SignBlockEntity) {
            SignBlockEntity signEntity = (SignBlockEntity)var9;
            Block var10 = level.getBlockState(pos).getBlock();
            if (var10 instanceof SignBlock) {
               SignBlock sign = (SignBlock)var10;
               sign.openTextEdit(player, signEntity, true);
            }
         }
      }

      return success;
   }
}
