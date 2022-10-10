package net.minecraft.inventory;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

public class ContainerBrewingStand extends Container {
   private final IInventory field_75188_e;
   private final Slot field_75186_f;
   private int field_184998_g;
   private int field_184999_h;

   public ContainerBrewingStand(InventoryPlayer var1, IInventory var2) {
      super();
      this.field_75188_e = var2;
      this.func_75146_a(new ContainerBrewingStand.Potion(var2, 0, 56, 51));
      this.func_75146_a(new ContainerBrewingStand.Potion(var2, 1, 79, 58));
      this.func_75146_a(new ContainerBrewingStand.Potion(var2, 2, 102, 51));
      this.field_75186_f = this.func_75146_a(new ContainerBrewingStand.Ingredient(var2, 3, 79, 17));
      this.func_75146_a(new ContainerBrewingStand.Fuel(var2, 4, 17, 17));

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

   public void func_75132_a(IContainerListener var1) {
      super.func_75132_a(var1);
      var1.func_175173_a(this, this.field_75188_e);
   }

   public void func_75142_b() {
      super.func_75142_b();

      for(int var1 = 0; var1 < this.field_75149_d.size(); ++var1) {
         IContainerListener var2 = (IContainerListener)this.field_75149_d.get(var1);
         if (this.field_184998_g != this.field_75188_e.func_174887_a_(0)) {
            var2.func_71112_a(this, 0, this.field_75188_e.func_174887_a_(0));
         }

         if (this.field_184999_h != this.field_75188_e.func_174887_a_(1)) {
            var2.func_71112_a(this, 1, this.field_75188_e.func_174887_a_(1));
         }
      }

      this.field_184998_g = this.field_75188_e.func_174887_a_(0);
      this.field_184999_h = this.field_75188_e.func_174887_a_(1);
   }

   public void func_75137_b(int var1, int var2) {
      this.field_75188_e.func_174885_b(var1, var2);
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75188_e.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if ((var2 < 0 || var2 > 2) && var2 != 3 && var2 != 4) {
            if (this.field_75186_f.func_75214_a(var5)) {
               if (!this.func_75135_a(var5, 3, 4, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (ContainerBrewingStand.Potion.func_75243_a_(var3) && var3.func_190916_E() == 1) {
               if (!this.func_75135_a(var5, 0, 3, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (ContainerBrewingStand.Fuel.func_185004_b_(var3)) {
               if (!this.func_75135_a(var5, 4, 5, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (var2 >= 5 && var2 < 32) {
               if (!this.func_75135_a(var5, 32, 41, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (var2 >= 32 && var2 < 41) {
               if (!this.func_75135_a(var5, 5, 32, false)) {
                  return ItemStack.field_190927_a;
               }
            } else if (!this.func_75135_a(var5, 5, 41, false)) {
               return ItemStack.field_190927_a;
            }
         } else {
            if (!this.func_75135_a(var5, 5, 41, true)) {
               return ItemStack.field_190927_a;
            }

            var4.func_75220_a(var5, var3);
         }

         if (var5.func_190926_b()) {
            var4.func_75215_d(ItemStack.field_190927_a);
         } else {
            var4.func_75218_e();
         }

         if (var5.func_190916_E() == var3.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         var4.func_190901_a(var1, var5);
      }

      return var3;
   }

   static class Fuel extends Slot {
      public Fuel(IInventory var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean func_75214_a(ItemStack var1) {
         return func_185004_b_(var1);
      }

      public static boolean func_185004_b_(ItemStack var0) {
         return var0.func_77973_b() == Items.field_151065_br;
      }

      public int func_75219_a() {
         return 64;
      }
   }

   static class Ingredient extends Slot {
      public Ingredient(IInventory var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean func_75214_a(ItemStack var1) {
         return PotionBrewing.func_185205_a(var1);
      }

      public int func_75219_a() {
         return 64;
      }
   }

   static class Potion extends Slot {
      public Potion(IInventory var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean func_75214_a(ItemStack var1) {
         return func_75243_a_(var1);
      }

      public int func_75219_a() {
         return 1;
      }

      public ItemStack func_190901_a(EntityPlayer var1, ItemStack var2) {
         PotionType var3 = PotionUtils.func_185191_c(var2);
         if (var1 instanceof EntityPlayerMP) {
            CriteriaTriggers.field_192130_j.func_192173_a((EntityPlayerMP)var1, var3);
         }

         super.func_190901_a(var1, var2);
         return var2;
      }

      public static boolean func_75243_a_(ItemStack var0) {
         Item var1 = var0.func_77973_b();
         return var1 == Items.field_151068_bn || var1 == Items.field_185155_bH || var1 == Items.field_185156_bI || var1 == Items.field_151069_bo;
      }
   }
}
