package net.minecraft.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;

public class ItemEndCrystal extends Item {
   public ItemEndCrystal(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      IBlockState var4 = var2.func_180495_p(var3);
      if (var4.func_177230_c() != Blocks.field_150343_Z && var4.func_177230_c() != Blocks.field_150357_h) {
         return EnumActionResult.FAIL;
      } else {
         BlockPos var5 = var3.func_177984_a();
         if (!var2.func_175623_d(var5)) {
            return EnumActionResult.FAIL;
         } else {
            double var6 = (double)var5.func_177958_n();
            double var8 = (double)var5.func_177956_o();
            double var10 = (double)var5.func_177952_p();
            List var12 = var2.func_72839_b((Entity)null, new AxisAlignedBB(var6, var8, var10, var6 + 1.0D, var8 + 2.0D, var10 + 1.0D));
            if (!var12.isEmpty()) {
               return EnumActionResult.FAIL;
            } else {
               if (!var2.field_72995_K) {
                  EntityEnderCrystal var13 = new EntityEnderCrystal(var2, var6 + 0.5D, var8, var10 + 0.5D);
                  var13.func_184517_a(false);
                  var2.func_72838_d(var13);
                  if (var2.field_73011_w instanceof EndDimension) {
                     DragonFightManager var14 = ((EndDimension)var2.field_73011_w).func_186063_s();
                     var14.func_186106_e();
                  }
               }

               var1.func_195996_i().func_190918_g(1);
               return EnumActionResult.SUCCESS;
            }
         }
      }
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }
}
