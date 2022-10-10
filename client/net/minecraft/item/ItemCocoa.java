package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCocoa extends ItemDye {
   public ItemCocoa(EnumDyeColor var1, Item.Properties var2) {
      super(var1, var2);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      BlockItemUseContext var2 = new BlockItemUseContext(var1);
      if (var2.func_196011_b()) {
         World var3 = var1.func_195991_k();
         IBlockState var4 = Blocks.field_150375_by.func_196258_a(var2);
         BlockPos var5 = var2.func_195995_a();
         if (var4 != null && var3.func_180501_a(var5, var4, 2)) {
            ItemStack var6 = var1.func_195996_i();
            EntityPlayer var7 = var2.func_195999_j();
            if (var7 instanceof EntityPlayerMP) {
               CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var7, var5, var6);
            }

            var6.func_190918_g(1);
            return EnumActionResult.SUCCESS;
         }
      }

      return EnumActionResult.FAIL;
   }
}
