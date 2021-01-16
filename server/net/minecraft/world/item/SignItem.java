package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignItem extends StandingAndWallBlockItem {
   public SignItem(Item.Properties var1, Block var2, Block var3) {
      super(var2, var3, var1);
   }

   protected boolean updateCustomBlockEntityTag(BlockPos var1, Level var2, @Nullable Player var3, ItemStack var4, BlockState var5) {
      boolean var6 = super.updateCustomBlockEntityTag(var1, var2, var3, var4, var5);
      if (!var2.isClientSide && !var6 && var3 != null) {
         var3.openTextEdit((SignBlockEntity)var2.getBlockEntity(var1));
      }

      return var6;
   }
}
