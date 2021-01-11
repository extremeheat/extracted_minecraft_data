package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   public ItemWritableBook() {
      super();
      this.func_77625_d(1);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      var3.func_71048_c(var1);
      var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      return var1;
   }

   public static boolean func_150930_a(NBTTagCompound var0) {
      if (var0 == null) {
         return false;
      } else if (!var0.func_150297_b("pages", 9)) {
         return false;
      } else {
         NBTTagList var1 = var0.func_150295_c("pages", 8);

         for(int var2 = 0; var2 < var1.func_74745_c(); ++var2) {
            String var3 = var1.func_150307_f(var2);
            if (var3 == null) {
               return false;
            }

            if (var3.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
