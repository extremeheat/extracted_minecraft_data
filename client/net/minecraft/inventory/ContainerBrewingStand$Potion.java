package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

class ContainerBrewingStand$Potion extends Slot {
   private EntityPlayer field_75244_a;

   public ContainerBrewingStand$Potion(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_75244_a = var1;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return func_75243_a_(var1);
   }

   @Override
   public int func_75219_a() {
      return 1;
   }

   @Override
   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      if (var2.func_77973_b() == Items.field_151068_bn && var2.func_77960_j() > 0) {
         this.field_75244_a.func_71064_a(AchievementList.field_76001_A, 1);
      }

      super.func_82870_a(var1, var2);
   }

   public static boolean func_75243_a_(ItemStack var0) {
      return var0 != null && (var0.func_77973_b() == Items.field_151068_bn || var0.func_77973_b() == Items.field_151069_bo);
   }
}
