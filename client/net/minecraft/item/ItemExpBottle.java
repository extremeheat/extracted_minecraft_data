package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemExpBottle extends Item {
   public ItemExpBottle() {
      super();
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var3.field_71075_bZ.field_75098_d) {
         --var1.field_77994_a;
      }

      var2.func_72956_a(var3, "random.bow", 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
      if (!var2.field_72995_K) {
         var2.func_72838_d(new EntityExpBottle(var2, var3));
      }

      var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      return var1;
   }
}
