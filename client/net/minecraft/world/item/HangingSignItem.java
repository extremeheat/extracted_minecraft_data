package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignItem extends StandingAndWallBlockItem {
   public HangingSignItem(Block var1, Block var2, Item.Properties var3) {
      super(var1, var2, var3, Direction.UP);
   }

   @Override
   protected boolean canPlace(LevelReader var1, BlockState var2, BlockPos var3) {
      Block var5 = var2.getBlock();
      if (var5 instanceof WallHangingSignBlock var4 && !var4.canPlace(var2, var1, var3)) {
         return false;
      }

      return super.canPlace(var1, var2, var3);
   }

   @Override
   protected boolean updateCustomBlockEntityTag(BlockPos var1, Level var2, @Nullable Player var3, ItemStack var4, BlockState var5) {
      boolean var6 = super.updateCustomBlockEntityTag(var1, var2, var3, var4, var5);
      if (!var2.isClientSide && !var6 && var3 != null) {
         BlockEntity var8 = var2.getBlockEntity(var1);
         if (var8 instanceof SignBlockEntity var7) {
            var3.openTextEdit((SignBlockEntity)var7);
         }
      }

      return var6;
   }
}
