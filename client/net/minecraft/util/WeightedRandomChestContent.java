package net.minecraft.util;

import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;

public class WeightedRandomChestContent extends WeightedRandom$Item {
   private ItemStack field_76297_b;
   private int field_76295_d;
   private int field_76296_e;

   public WeightedRandomChestContent(Item var1, int var2, int var3, int var4, int var5) {
      super(var5);
      this.field_76297_b = new ItemStack(var1, 1, var2);
      this.field_76295_d = var3;
      this.field_76296_e = var4;
   }

   public WeightedRandomChestContent(ItemStack var1, int var2, int var3, int var4) {
      super(var4);
      this.field_76297_b = var1;
      this.field_76295_d = var2;
      this.field_76296_e = var3;
   }

   public static void func_76293_a(Random var0, WeightedRandomChestContent[] var1, IInventory var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         WeightedRandomChestContent var5 = (WeightedRandomChestContent)WeightedRandom.func_76274_a(var0, var1);
         int var6 = var5.field_76295_d + var0.nextInt(var5.field_76296_e - var5.field_76295_d + 1);
         if (var5.field_76297_b.func_77976_d() >= var6) {
            ItemStack var9 = var5.field_76297_b.func_77946_l();
            var9.field_77994_a = var6;
            var2.func_70299_a(var0.nextInt(var2.func_70302_i_()), var9);
         } else {
            for(int var7 = 0; var7 < var6; ++var7) {
               ItemStack var8 = var5.field_76297_b.func_77946_l();
               var8.field_77994_a = 1;
               var2.func_70299_a(var0.nextInt(var2.func_70302_i_()), var8);
            }
         }
      }
   }

   public static void func_150706_a(Random var0, WeightedRandomChestContent[] var1, TileEntityDispenser var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         WeightedRandomChestContent var5 = (WeightedRandomChestContent)WeightedRandom.func_76274_a(var0, var1);
         int var6 = var5.field_76295_d + var0.nextInt(var5.field_76296_e - var5.field_76295_d + 1);
         if (var5.field_76297_b.func_77976_d() >= var6) {
            ItemStack var9 = var5.field_76297_b.func_77946_l();
            var9.field_77994_a = var6;
            var2.func_70299_a(var0.nextInt(var2.func_70302_i_()), var9);
         } else {
            for(int var7 = 0; var7 < var6; ++var7) {
               ItemStack var8 = var5.field_76297_b.func_77946_l();
               var8.field_77994_a = 1;
               var2.func_70299_a(var0.nextInt(var2.func_70302_i_()), var8);
            }
         }
      }
   }

   public static WeightedRandomChestContent[] func_92080_a(WeightedRandomChestContent[] var0, WeightedRandomChestContent... var1) {
      WeightedRandomChestContent[] var2 = new WeightedRandomChestContent[var0.length + var1.length];
      int var3 = 0;

      for(int var4 = 0; var4 < var0.length; ++var4) {
         var2[var3++] = var0[var4];
      }

      for(WeightedRandomChestContent var7 : var1) {
         var2[var3++] = var7;
      }

      return var2;
   }
}
