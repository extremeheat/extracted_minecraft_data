package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSeedFood extends ItemFood {
   private final IBlockState field_195972_b;

   public ItemSeedFood(int var1, float var2, Block var3, Item.Properties var4) {
      super(var1, var2, false, var4);
      this.field_195972_b = var3.func_176223_P();
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a().func_177984_a();
      if (var1.func_196000_l() == EnumFacing.UP && var2.func_175623_d(var3) && this.field_195972_b.func_196955_c(var2, var3)) {
         var2.func_180501_a(var3, this.field_195972_b, 11);
         EntityPlayer var4 = var1.func_195999_j();
         ItemStack var5 = var1.func_195996_i();
         if (var4 instanceof EntityPlayerMP) {
            CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var4, var3, var5);
         }

         var5.func_190918_g(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }
}
