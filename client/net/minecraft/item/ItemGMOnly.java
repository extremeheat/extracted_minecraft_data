package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public class ItemGMOnly extends ItemBlock {
   public ItemGMOnly(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   @Nullable
   protected IBlockState func_195945_b(BlockItemUseContext var1) {
      EntityPlayer var2 = var1.func_195999_j();
      return var2 != null && !var2.func_195070_dx() ? null : super.func_195945_b(var1);
   }
}
