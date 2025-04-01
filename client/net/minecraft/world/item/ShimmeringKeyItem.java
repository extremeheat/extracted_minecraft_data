package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ShimmeringKeyItem extends Item {
   public ShimmeringKeyItem(Item.Properties var1) {
      super(var1);
   }

   public boolean isFoil(ItemStack var1) {
      return true;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      BlockState var5 = var3.getBlockState(var4);
      if (var5.is(Blocks.SHIMMERING_DOOR)) {
         if ((Boolean)var5.getValue(DoorBlock.OPEN)) {
            return InteractionResult.FAIL;
         } else {
            if (var2 instanceof ServerPlayer) {
               ServerPlayer var6 = (ServerPlayer)var2;
               var6.openDoor(var1.getItemInHand(), var4, (Direction)var5.getValue(DoorBlock.FACING), (DoubleBlockHalf)var5.getValue(DoorBlock.HALF));
            }

            return InteractionResult.SUCCESS_SERVER;
         }
      } else {
         return super.useOn(var1);
      }
   }
}
