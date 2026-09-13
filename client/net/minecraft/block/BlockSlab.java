package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
   protected final boolean field_150004_a;

   public BlockSlab(boolean var1, Material var2) {
      super(var2);
      this.field_150004_a = var1;
      if (var1) {
         this.field_149787_q = true;
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }

      this.func_149713_g(255);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      if (this.field_150004_a) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         boolean var5 = (var1.func_72805_g(var2, var3, var4) & 8) != 0;
         if (var5) {
            this.func_149676_a(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
         } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
         }
      }
   }

   @Override
   public void func_149683_g() {
      if (this.field_150004_a) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      }
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_149719_a(var1, var2, var3, var4);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public boolean func_149662_c() {
      return this.field_150004_a;
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      if (this.field_150004_a) {
         return var9;
      } else {
         return var5 != 0 && (var5 == 1 || !((double)var7 > 0.5)) ? var9 : var9 | 8;
      }
   }

   @Override
   public int func_149745_a(Random var1) {
      return this.field_150004_a ? 2 : 1;
   }

   @Override
   public int func_149692_a(int var1) {
      return var1 & 7;
   }

   @Override
   public boolean func_149686_d() {
      return this.field_150004_a;
   }

   @Override
   public boolean func_149646_a(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (this.field_150004_a) {
         return super.func_149646_a(var1, var2, var3, var4, var5);
      } else if (var5 != 1 && var5 != 0 && !super.func_149646_a(var1, var2, var3, var4, var5)) {
         return false;
      } else {
         int var6 = var2 + Facing.field_71586_b[Facing.field_71588_a[var5]];
         int var7 = var3 + Facing.field_71587_c[Facing.field_71588_a[var5]];
         int var8 = var4 + Facing.field_71585_d[Facing.field_71588_a[var5]];
         boolean var9 = (var1.func_72805_g(var6, var7, var8) & 8) != 0;
         if (var9) {
            if (var5 == 0) {
               return true;
            } else if (var5 == 1 && super.func_149646_a(var1, var2, var3, var4, var5)) {
               return true;
            } else {
               return !func_150003_a(var1.func_147439_a(var2, var3, var4)) || (var1.func_72805_g(var2, var3, var4) & 8) == 0;
            }
         } else if (var5 == 1) {
            return true;
         } else if (var5 == 0 && super.func_149646_a(var1, var2, var3, var4, var5)) {
            return true;
         } else {
            return !func_150003_a(var1.func_147439_a(var2, var3, var4)) || (var1.func_72805_g(var2, var3, var4) & 8) != 0;
         }
      }
   }

   private static boolean func_150003_a(Block var0) {
      return var0 == Blocks.field_150333_U || var0 == Blocks.field_150376_bx;
   }

   public abstract String func_150002_b(int var1);

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      return super.func_149643_k(var1, var2, var3, var4) & 7;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      if (func_150003_a(this)) {
         return Item.func_150898_a(this);
      } else if (this == Blocks.field_150334_T) {
         return Item.func_150898_a(Blocks.field_150333_U);
      } else {
         return this == Blocks.field_150373_bw ? Item.func_150898_a(Blocks.field_150376_bx) : Item.func_150898_a(Blocks.field_150333_U);
      }
   }
}
