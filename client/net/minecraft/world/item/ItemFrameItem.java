package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemFrameItem extends HangingEntityItem {
   public ItemFrameItem(Item.Properties var1) {
      super(EntityType.ITEM_FRAME, var1);
   }

   protected boolean mayPlace(Player var1, Direction var2, ItemStack var3, BlockPos var4) {
      return !Level.isOutsideBuildHeight(var4) && var1.mayUseItemAt(var4, var2, var3);
   }
}
