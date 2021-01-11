package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot {
   private final InventoryCrafting field_75239_a;
   private final EntityPlayer field_75238_b;
   private int field_75237_g;

   public SlotCrafting(EntityPlayer var1, InventoryCrafting var2, IInventory var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.field_75238_b = var1;
      this.field_75239_a = var2;
   }

   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   public ItemStack func_75209_a(int var1) {
      if (this.func_75216_d()) {
         this.field_75237_g += Math.min(var1, this.func_75211_c().field_77994_a);
      }

      return super.func_75209_a(var1);
   }

   protected void func_75210_a(ItemStack var1, int var2) {
      this.field_75237_g += var2;
      this.func_75208_c(var1);
   }

   protected void func_75208_c(ItemStack var1) {
      if (this.field_75237_g > 0) {
         var1.func_77980_a(this.field_75238_b.field_70170_p, this.field_75238_b, this.field_75237_g);
      }

      this.field_75237_g = 0;
      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150462_ai)) {
         this.field_75238_b.func_71029_a(AchievementList.field_76017_h);
      }

      if (var1.func_77973_b() instanceof ItemPickaxe) {
         this.field_75238_b.func_71029_a(AchievementList.field_76018_i);
      }

      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150460_al)) {
         this.field_75238_b.func_71029_a(AchievementList.field_76015_j);
      }

      if (var1.func_77973_b() instanceof ItemHoe) {
         this.field_75238_b.func_71029_a(AchievementList.field_76013_l);
      }

      if (var1.func_77973_b() == Items.field_151025_P) {
         this.field_75238_b.func_71029_a(AchievementList.field_76014_m);
      }

      if (var1.func_77973_b() == Items.field_151105_aU) {
         this.field_75238_b.func_71029_a(AchievementList.field_76011_n);
      }

      if (var1.func_77973_b() instanceof ItemPickaxe && ((ItemPickaxe)var1.func_77973_b()).func_150913_i() != Item.ToolMaterial.WOOD) {
         this.field_75238_b.func_71029_a(AchievementList.field_76012_o);
      }

      if (var1.func_77973_b() instanceof ItemSword) {
         this.field_75238_b.func_71029_a(AchievementList.field_76024_r);
      }

      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150381_bn)) {
         this.field_75238_b.func_71029_a(AchievementList.field_75998_D);
      }

      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150342_X)) {
         this.field_75238_b.func_71029_a(AchievementList.field_76000_F);
      }

      if (var1.func_77973_b() == Items.field_151153_ao && var1.func_77960_j() == 1) {
         this.field_75238_b.func_71029_a(AchievementList.field_180219_M);
      }

   }

   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      this.func_75208_c(var2);
      ItemStack[] var3 = CraftingManager.func_77594_a().func_180303_b(this.field_75239_a, var1.field_70170_p);

      for(int var4 = 0; var4 < var3.length; ++var4) {
         ItemStack var5 = this.field_75239_a.func_70301_a(var4);
         ItemStack var6 = var3[var4];
         if (var5 != null) {
            this.field_75239_a.func_70298_a(var4, 1);
         }

         if (var6 != null) {
            if (this.field_75239_a.func_70301_a(var4) == null) {
               this.field_75239_a.func_70299_a(var4, var6);
            } else if (!this.field_75238_b.field_71071_by.func_70441_a(var6)) {
               this.field_75238_b.func_71019_a(var6, false);
            }
         }
      }

   }
}
