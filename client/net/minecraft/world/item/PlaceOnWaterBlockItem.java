package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceOnWaterBlockItem extends BlockItem {
   public PlaceOnWaterBlockItem(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      return InteractionResult.PASS;
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      BlockHitResult var4 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.SOURCE_ONLY);
      BlockHitResult var5 = var4.withPosition(var4.getBlockPos().above());
      return super.useOn(new UseOnContext(var2, var3, var5));
   }
}
