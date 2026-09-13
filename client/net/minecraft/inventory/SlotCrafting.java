package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item$ToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.AchievementList;

public class SlotCrafting extends Slot {
   private final IInventory field_75239_a;
   private EntityPlayer field_75238_b;
   private int field_75237_g;

   public SlotCrafting(EntityPlayer var1, IInventory var2, IInventory var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.field_75238_b = var1;
      this.field_75239_a = var2;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   @Override
   public ItemStack func_75209_a(int var1) {
      if (this.func_75216_d()) {
         this.field_75237_g += Math.min(var1, this.func_75211_c().field_77994_a);
      }

      return super.func_75209_a(var1);
   }

   @Override
   protected void func_75210_a(ItemStack var1, int var2) {
      this.field_75237_g += var2;
      this.func_75208_c(var1);
   }

   @Override
   protected void func_75208_c(ItemStack var1) {
      var1.func_77980_a(this.field_75238_b.field_70170_p, this.field_75238_b, this.field_75237_g);
      this.field_75237_g = 0;
      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150462_ai)) {
         this.field_75238_b.func_71064_a(AchievementList.field_76017_h, 1);
      }

      if (var1.func_77973_b() instanceof ItemPickaxe) {
         this.field_75238_b.func_71064_a(AchievementList.field_76018_i, 1);
      }

      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150460_al)) {
         this.field_75238_b.func_71064_a(AchievementList.field_76015_j, 1);
      }

      if (var1.func_77973_b() instanceof ItemHoe) {
         this.field_75238_b.func_71064_a(AchievementList.field_76013_l, 1);
      }

      if (var1.func_77973_b() == Items.field_151025_P) {
         this.field_75238_b.func_71064_a(AchievementList.field_76014_m, 1);
      }

      if (var1.func_77973_b() == Items.field_151105_aU) {
         this.field_75238_b.func_71064_a(AchievementList.field_76011_n, 1);
      }

      if (var1.func_77973_b() instanceof ItemPickaxe && ((ItemPickaxe)var1.func_77973_b()).func_150913_i() != Item$ToolMaterial.WOOD) {
         this.field_75238_b.func_71064_a(AchievementList.field_76012_o, 1);
      }

      if (var1.func_77973_b() instanceof ItemSword) {
         this.field_75238_b.func_71064_a(AchievementList.field_76024_r, 1);
      }

      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150381_bn)) {
         this.field_75238_b.func_71064_a(AchievementList.field_75998_D, 1);
      }

      if (var1.func_77973_b() == Item.func_150898_a(Blocks.field_150342_X)) {
         this.field_75238_b.func_71064_a(AchievementList.field_76000_F, 1);
      }
   }

   @Override
   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      this.func_75208_c(var2);

      for(int var3 = 0; var3 < this.field_75239_a.func_70302_i_(); ++var3) {
         ItemStack var4 = this.field_75239_a.func_70301_a(var3);
         if (var4 != null) {
            this.field_75239_a.func_70298_a(var3, 1);
            if (var4.func_77973_b().func_77634_r()) {
               ItemStack var5 = new ItemStack(var4.func_77973_b().func_77668_q());
               if (!var4.func_77973_b().func_77630_h(var4) || !this.field_75238_b.field_71071_by.func_70441_a(var5)) {
                  if (this.field_75239_a.func_70301_a(var3) == null) {
                     this.field_75239_a.func_70299_a(var3, var5);
                  } else {
                     this.field_75238_b.func_71019_a(var5, false);
                  }
               }
            }
         }
      }
   }
}
