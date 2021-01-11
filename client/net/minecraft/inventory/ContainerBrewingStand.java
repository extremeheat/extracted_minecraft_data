package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class ContainerBrewingStand extends Container {
   private IInventory field_75188_e;
   private final Slot field_75186_f;
   private int field_75187_g;

   public ContainerBrewingStand(InventoryPlayer var1, IInventory var2) {
      super();
      this.field_75188_e = var2;
      this.func_75146_a(new ContainerBrewingStand.Potion(var1.field_70458_d, var2, 0, 56, 46));
      this.func_75146_a(new ContainerBrewingStand.Potion(var1.field_70458_d, var2, 1, 79, 53));
      this.func_75146_a(new ContainerBrewingStand.Potion(var1.field_70458_d, var2, 2, 102, 46));
      this.field_75186_f = this.func_75146_a(new ContainerBrewingStand.Ingredient(var2, 3, 79, 17));

      int var3;
      for(var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.func_75146_a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 9; ++var3) {
         this.func_75146_a(new Slot(var1, var3, 8 + var3 * 18, 142));
      }

   }

   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_175173_a(this, this.field_75188_e);
   }

   public void func_75142_b() {
      super.func_75142_b();

      for(int var1 = 0; var1 < this.field_75149_d.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.field_75149_d.get(var1);
         if (this.field_75187_g != this.field_75188_e.func_174887_a_(0)) {
            var2.func_71112_a(this, 0, this.field_75188_e.func_174887_a_(0));
         }
      }

      this.field_75187_g = this.field_75188_e.func_174887_a_(0);
   }

   public void func_75137_b(int var1, int var2) {
      this.field_75188_e.func_174885_b(var1, var2);
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75188_e.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if ((var2 < 0 || var2 > 2) && var2 != 3) {
            if (!this.field_75186_f.func_75216_d() && this.field_75186_f.func_75214_a(var5)) {
               if (!this.func_75135_a(var5, 3, 4, false)) {
                  return null;
               }
            } else if (ContainerBrewingStand.Potion.func_75243_a_(var3)) {
               if (!this.func_75135_a(var5, 0, 3, false)) {
                  return null;
               }
            } else if (var2 >= 4 && var2 < 31) {
               if (!this.func_75135_a(var5, 31, 40, false)) {
                  return null;
               }
            } else if (var2 >= 31 && var2 < 40) {
               if (!this.func_75135_a(var5, 4, 31, false)) {
                  return null;
               }
            } else if (!this.func_75135_a(var5, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.func_75135_a(var5, 4, 40, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d((ItemStack)null);
         } else {
            var4.func_75218_e();
         }

         if (var5.field_77994_a == var3.field_77994_a) {
            return null;
         }

         var4.func_82870_a(var1, var5);
      }

      return var3;
   }

   class Ingredient extends Slot {
      public Ingredient(IInventory var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      public boolean func_75214_a(ItemStack var1) {
         return var1 != null ? var1.func_77973_b().func_150892_m(var1) : false;
      }

      public int func_75219_a() {
         return 64;
      }
   }

   static class Potion extends Slot {
      private EntityPlayer field_75244_a;

      public Potion(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
         this.field_75244_a = var1;
      }

      public boolean func_75214_a(ItemStack var1) {
         return func_75243_a_(var1);
      }

      public int func_75219_a() {
         return 1;
      }

      public void func_82870_a(EntityPlayer var1, ItemStack var2) {
         if (var2.func_77973_b() == Items.field_151068_bn && var2.func_77960_j() > 0) {
            this.field_75244_a.func_71029_a(AchievementList.field_76001_A);
         }

         super.func_82870_a(var1, var2);
      }

      public static boolean func_75243_a_(ItemStack var0) {
         return var0 != null && (var0.func_77973_b() == Items.field_151068_bn || var0.func_77973_b() == Items.field_151069_bo);
      }
   }
}
