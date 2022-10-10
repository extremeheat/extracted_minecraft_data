package net.minecraft.item;

import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFireCharge extends Item {
   public ItemFireCharge(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      if (var2.field_72995_K) {
         return EnumActionResult.SUCCESS;
      } else {
         BlockPos var3 = var1.func_195995_a().func_177972_a(var1.func_196000_l());
         if (var2.func_180495_p(var3).func_196958_f()) {
            var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187616_bj, SoundCategory.BLOCKS, 1.0F, (field_77697_d.nextFloat() - field_77697_d.nextFloat()) * 0.2F + 1.0F);
            var2.func_175656_a(var3, ((BlockFire)Blocks.field_150480_ab).func_196448_a(var2, var3));
         }

         var1.func_195996_i().func_190918_g(1);
         return EnumActionResult.SUCCESS;
      }
   }
}
