package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWritableBook extends Item {
   public ItemWritableBook(Item.Properties var1) {
      super(var1);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      var2.func_184814_a(var4, var3);
      var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      return new ActionResult(EnumActionResult.SUCCESS, var4);
   }

   public static boolean func_150930_a(@Nullable NBTTagCompound var0) {
      if (var0 == null) {
         return false;
      } else if (!var0.func_150297_b("pages", 9)) {
         return false;
      } else {
         NBTTagList var1 = var0.func_150295_c("pages", 8);

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            String var3 = var1.func_150307_f(var2);
            if (var3.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
