package net.minecraft.item;

import net.minecraft.init.Items;

public enum ItemArmor$ArmorMaterial {
   CLOTH(5, new int[]{1, 3, 2, 1}, 15),
   CHAIN(15, new int[]{2, 5, 4, 1}, 12),
   IRON(15, new int[]{2, 6, 5, 2}, 9),
   GOLD(7, new int[]{2, 5, 3, 1}, 25),
   DIAMOND(33, new int[]{3, 8, 6, 3}, 10);

   private int field_78048_f;
   private int[] field_78049_g;
   private int field_78055_h;

   private ItemArmor$ArmorMaterial(int var3, int[] var4, int var5) {
      this.field_78048_f = var3;
      this.field_78049_g = var4;
      this.field_78055_h = var5;
   }

   public int func_78046_a(int var1) {
      return ItemArmor.access$000()[var1] * this.field_78048_f;
   }

   public int func_78044_b(int var1) {
      return this.field_78049_g[var1];
   }

   public int func_78045_a() {
      return this.field_78055_h;
   }

   public Item func_151685_b() {
      if (this == CLOTH) {
         return Items.field_151116_aA;
      } else if (this == CHAIN) {
         return Items.field_151042_j;
      } else if (this == GOLD) {
         return Items.field_151043_k;
      } else if (this == IRON) {
         return Items.field_151042_j;
      } else {
         return this == DIAMOND ? Items.field_151045_i : null;
      }
   }
}
