package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
   public ItemGlassBottle() {
      super();
      this.func_77637_a(CreativeTabs.field_78038_k);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      MovingObjectPosition var4 = this.func_77621_a(var2, var3, true);
      if (var4 == null) {
         return var1;
      } else {
         if (var4.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos var5 = var4.func_178782_a();
            if (!var2.func_175660_a(var3, var5)) {
               return var1;
            }

            if (!var3.func_175151_a(var5.func_177972_a(var4.field_178784_b), var4.field_178784_b, var1)) {
               return var1;
            }

            if (var2.func_180495_p(var5).func_177230_c().func_149688_o() == Material.field_151586_h) {
               --var1.field_77994_a;
               var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
               if (var1.field_77994_a <= 0) {
                  return new ItemStack(Items.field_151068_bn);
               }

               if (!var3.field_71071_by.func_70441_a(new ItemStack(Items.field_151068_bn))) {
                  var3.func_71019_a(new ItemStack(Items.field_151068_bn, 1, 0), false);
               }
            }
         }

         return var1;
      }
   }
}
