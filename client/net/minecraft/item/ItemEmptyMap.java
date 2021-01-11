package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
   protected ItemEmptyMap() {
      super();
      this.func_77637_a(CreativeTabs.field_78026_f);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      ItemStack var4 = new ItemStack(Items.field_151098_aY, 1, var2.func_72841_b("map"));
      String var5 = "map_" + var4.func_77960_j();
      MapData var6 = new MapData(var5);
      var2.func_72823_a(var5, var6);
      var6.field_76197_d = 0;
      var6.func_176054_a(var3.field_70165_t, var3.field_70161_v, var6.field_76197_d);
      var6.field_76200_c = (byte)var2.field_73011_w.func_177502_q();
      var6.func_76185_a();
      --var1.field_77994_a;
      if (var1.field_77994_a <= 0) {
         return var4;
      } else {
         if (!var3.field_71071_by.func_70441_a(var4.func_77946_l())) {
            var3.func_71019_a(var4, false);
         }

         var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
         return var1;
      }
   }
}
