package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GameMasterBlockItem extends BlockItem {
   public GameMasterBlockItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext var1) {
      Player var2 = var1.getPlayer();
      return var2 != null && !var2.canUseGameMasterBlocks() ? null : super.getPlacementState(var1);
   }
}
