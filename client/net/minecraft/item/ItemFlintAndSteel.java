package net.minecraft.item;

import java.util.Iterator;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemFlintAndSteel extends Item {
   public ItemFlintAndSteel(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      EntityPlayer var2 = var1.func_195999_j();
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a().func_177972_a(var1.func_196000_l());
      if (func_201825_a(var3, var4)) {
         var3.func_184133_a(var2, var4, SoundEvents.field_187649_bu, SoundCategory.BLOCKS, 1.0F, field_77697_d.nextFloat() * 0.4F + 0.8F);
         IBlockState var5 = ((BlockFire)Blocks.field_150480_ab).func_196448_a(var3, var4);
         var3.func_180501_a(var4, var5, 11);
         ItemStack var6 = var1.func_195996_i();
         if (var2 instanceof EntityPlayerMP) {
            CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var2, var4, var6);
         }

         if (var2 != null) {
            var6.func_77972_a(1, var2);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public static boolean func_201825_a(IWorld var0, BlockPos var1) {
      IBlockState var2 = ((BlockFire)Blocks.field_150480_ab).func_196448_a(var0, var1);
      boolean var3 = false;
      Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         EnumFacing var5 = (EnumFacing)var4.next();
         if (var0.func_180495_p(var1.func_177972_a(var5)).func_177230_c() == Blocks.field_150343_Z && ((BlockPortal)Blocks.field_150427_aO).func_201816_b(var0, var1) != null) {
            var3 = true;
         }
      }

      return var0.func_175623_d(var1) && (var2.func_196955_c(var0, var1) || var3);
   }
}
