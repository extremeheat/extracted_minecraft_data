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

public class ItemSeeds extends Item {
   private final IBlockState field_195978_a;

   public ItemSeeds(Block var1, Item.Properties var2) {
      super(var2);
      this.field_195978_a = var1.func_176223_P();
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a().func_177984_a();
      if (var1.func_196000_l() == EnumFacing.UP && var2.func_175623_d(var3) && this.field_195978_a.func_196955_c(var2, var3)) {
         var2.func_180501_a(var3, this.field_195978_a, 11);
         ItemStack var4 = var1.func_195996_i();
         EntityPlayer var5 = var1.func_195999_j();
         if (var5 instanceof EntityPlayerMP) {
            CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var5, var3, var4);
         }

         var4.func_190918_g(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }
}
