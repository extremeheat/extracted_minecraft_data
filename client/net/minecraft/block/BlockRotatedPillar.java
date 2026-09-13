package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockRotatedPillar extends Block {
   protected IIcon field_150164_N;

   protected BlockRotatedPillar(Material var1) {
      super(var1);
   }

   @Override
   public int func_149645_b() {
      return 31;
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      int var10 = var9 & 3;
      byte var11 = 0;
      switch(var5) {
         case 0:
         case 1:
            var11 = 0;
            break;
         case 2:
         case 3:
            var11 = 8;
            break;
         case 4:
         case 5:
            var11 = 4;
      }

      return var10 | var11;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      int var3 = var2 & 12;
      int var4 = var2 & 3;
      if (var3 != 0 || var1 != 1 && var1 != 0) {
         if (var3 != 4 || var1 != 5 && var1 != 4) {
            return var3 != 8 || var1 != 2 && var1 != 3 ? this.func_150163_b(var4) : this.func_150161_d(var4);
         } else {
            return this.func_150161_d(var4);
         }
      } else {
         return this.func_150161_d(var4);
      }
   }

   protected abstract IIcon func_150163_b(int var1);

   protected IIcon func_150161_d(int var1) {
      return this.field_150164_N;
   }

   @Override
   public int func_149692_a(int var1) {
      return var1 & 3;
   }

   public int func_150162_k(int var1) {
      return var1 & 3;
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Item.func_150898_a(this), 1, this.func_150162_k(var1));
   }
}
