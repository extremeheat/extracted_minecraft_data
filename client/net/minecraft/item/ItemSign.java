package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSign extends ItemWallOrFloor {
   public ItemSign(Item.Properties var1) {
      super(Blocks.field_196649_cc, Blocks.field_150444_as, var1);
   }

   protected boolean func_195943_a(BlockPos var1, World var2, @Nullable EntityPlayer var3, ItemStack var4, IBlockState var5) {
      boolean var6 = super.func_195943_a(var1, var2, var3, var4, var5);
      if (!var2.field_72995_K && !var6 && var3 != null) {
         var3.func_175141_a((TileEntitySign)var2.func_175625_s(var1));
      }

      return var6;
   }
}
