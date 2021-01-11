package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemCarrotOnAStick extends Item {
   public ItemCarrotOnAStick() {
      super();
      this.func_77637_a(CreativeTabs.field_78029_e);
      this.func_77625_d(1);
      this.func_77656_e(25);
   }

   public boolean func_77662_d() {
      return true;
   }

   public boolean func_77629_n_() {
      return true;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (var3.func_70115_ae() && var3.field_70154_o instanceof EntityPig) {
         EntityPig var4 = (EntityPig)var3.field_70154_o;
         if (var4.func_82183_n().func_82633_h() && var1.func_77958_k() - var1.func_77960_j() >= 7) {
            var4.func_82183_n().func_82632_g();
            var1.func_77972_a(7, var3);
            if (var1.field_77994_a == 0) {
               ItemStack var5 = new ItemStack(Items.field_151112_aM);
               var5.func_77982_d(var1.func_77978_p());
               return var5;
            }
         }
      }

      var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      return var1;
   }
}
