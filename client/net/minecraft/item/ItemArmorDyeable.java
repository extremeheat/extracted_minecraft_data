package net.minecraft.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;

public class ItemArmorDyeable extends ItemArmor {
   public ItemArmorDyeable(IArmorMaterial var1, EntityEquipmentSlot var2, Item.Properties var3) {
      super(var1, var2, var3);
   }

   public boolean func_200883_f_(ItemStack var1) {
      NBTTagCompound var2 = var1.func_179543_a("display");
      return var2 != null && var2.func_150297_b("color", 99);
   }

   public int func_200886_f(ItemStack var1) {
      NBTTagCompound var2 = var1.func_179543_a("display");
      return var2 != null && var2.func_150297_b("color", 99) ? var2.func_74762_e("color") : 10511680;
   }

   public void func_200884_g(ItemStack var1) {
      NBTTagCompound var2 = var1.func_179543_a("display");
      if (var2 != null && var2.func_74764_b("color")) {
         var2.func_82580_o("color");
      }

   }

   public void func_200885_a(ItemStack var1, int var2) {
      var1.func_190925_c("display").func_74768_a("color", var2);
   }
}
