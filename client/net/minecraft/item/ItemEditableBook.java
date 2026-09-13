package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class ItemEditableBook extends Item {
   public ItemEditableBook() {
      super();
      this.func_77625_d(1);
   }

   public static boolean func_77828_a(NBTTagCompound var0) {
      if (!ItemWritableBook.func_150930_a(var0)) {
         return false;
      } else if (!var0.func_150297_b("title", 8)) {
         return false;
      } else {
         String var1 = var0.func_74779_i("title");
         if (var1 == null || var1.length() > 16) {
            return false;
         } else {
            return var0.func_150297_b("author", 8);
         }
      }
   }

   @Override
   public String func_77653_i(ItemStack var1) {
      if (var1.func_77942_o()) {
         NBTTagCompound var2 = var1.func_77978_p();
         String var3 = var2.func_74779_i("title");
         if (!StringUtils.func_151246_b(var3)) {
            return var3;
         }
      }

      return super.func_77653_i(var1);
   }

   @Override
   public void func_77624_a(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
      if (var1.func_77942_o()) {
         NBTTagCompound var5 = var1.func_77978_p();
         String var6 = var5.func_74779_i("author");
         if (!StringUtils.func_151246_b(var6)) {
            var3.add(EnumChatFormatting.GRAY + StatCollector.func_74837_a("book.byAuthor", var6));
         }
      }
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      var3.func_71048_c(var1);
      return var1;
   }

   @Override
   public boolean func_77651_p() {
      return true;
   }

   @Override
   public boolean func_77636_d(ItemStack var1) {
      return true;
   }
}
