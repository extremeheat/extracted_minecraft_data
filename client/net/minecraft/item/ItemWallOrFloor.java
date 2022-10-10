package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWallOrFloor extends ItemBlock {
   protected final Block field_195947_b;

   public ItemWallOrFloor(Block var1, Block var2, Item.Properties var3) {
      super(var1, var3);
      this.field_195947_b = var2;
   }

   @Nullable
   protected IBlockState func_195945_b(BlockItemUseContext var1) {
      IBlockState var2 = this.field_195947_b.func_196258_a(var1);
      IBlockState var3 = null;
      World var4 = var1.func_195991_k();
      BlockPos var5 = var1.func_195995_a();
      EnumFacing[] var6 = var1.func_196009_e();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         EnumFacing var9 = var6[var8];
         if (var9 != EnumFacing.UP) {
            IBlockState var10 = var9 == EnumFacing.DOWN ? this.func_179223_d().func_196258_a(var1) : var2;
            if (var10 != null && var10.func_196955_c(var4, var5)) {
               var3 = var10;
               break;
            }
         }
      }

      return var3 != null && var4.func_195584_a(var3, var5) ? var3 : null;
   }

   public void func_195946_a(Map<Block, Item> var1, Item var2) {
      super.func_195946_a(var1, var2);
      var1.put(this.field_195947_b, var2);
   }
}
