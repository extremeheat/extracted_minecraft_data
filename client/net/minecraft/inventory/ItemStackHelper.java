package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ItemStackHelper {
   public static ItemStack func_188382_a(List<ItemStack> var0, int var1, int var2) {
      return var1 >= 0 && var1 < var0.size() && !((ItemStack)var0.get(var1)).func_190926_b() && var2 > 0 ? ((ItemStack)var0.get(var1)).func_77979_a(var2) : ItemStack.field_190927_a;
   }

   public static ItemStack func_188383_a(List<ItemStack> var0, int var1) {
      return var1 >= 0 && var1 < var0.size() ? (ItemStack)var0.set(var1, ItemStack.field_190927_a) : ItemStack.field_190927_a;
   }

   public static NBTTagCompound func_191282_a(NBTTagCompound var0, NonNullList<ItemStack> var1) {
      return func_191281_a(var0, var1, true);
   }

   public static NBTTagCompound func_191281_a(NBTTagCompound var0, NonNullList<ItemStack> var1, boolean var2) {
      NBTTagList var3 = new NBTTagList();

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         ItemStack var5 = (ItemStack)var1.get(var4);
         if (!var5.func_190926_b()) {
            NBTTagCompound var6 = new NBTTagCompound();
            var6.func_74774_a("Slot", (byte)var4);
            var5.func_77955_b(var6);
            var3.add((INBTBase)var6);
         }
      }

      if (!var3.isEmpty() || var2) {
         var0.func_74782_a("Items", var3);
      }

      return var0;
   }

   public static void func_191283_b(NBTTagCompound var0, NonNullList<ItemStack> var1) {
      NBTTagList var2 = var0.func_150295_c("Items", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         int var5 = var4.func_74771_c("Slot") & 255;
         if (var5 >= 0 && var5 < var1.size()) {
            var1.set(var5, ItemStack.func_199557_a(var4));
         }
      }

   }
}
