package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HangingSignItem extends SignItem {
   public HangingSignItem(Block var1, Block var2, Item.Properties var3) {
      super(var3, var1, var2, Direction.UP);
   }

   protected boolean canPlace(LevelReader var1, BlockState var2, BlockPos var3) {
      Block var5 = var2.getBlock();
      if (var5 instanceof WallHangingSignBlock var4) {
         if (!var4.canPlace(var2, var1, var3)) {
            return false;
         }
      }

      return super.canPlace(var1, var2, var3);
   }
}
