package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
   private boolean field_150187_a;

   public BlockRedstoneOre(boolean var1) {
      super(Material.field_151576_e);
      if (var1) {
         this.func_149675_a(true);
      }

      this.field_150187_a = var1;
   }

   @Override
   public int func_149738_a(World var1) {
      return 30;
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
      this.func_150185_e(var1, var2, var3, var4);
      super.func_149699_a(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149724_b(World var1, int var2, int var3, int var4, Entity var5) {
      this.func_150185_e(var1, var2, var3, var4);
      super.func_149724_b(var1, var2, var3, var4, var5);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      this.func_150185_e(var1, var2, var3, var4);
      return super.func_149727_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   private void func_150185_e(World var1, int var2, int var3, int var4) {
      this.func_150186_m(var1, var2, var3, var4);
      if (this == Blocks.field_150450_ax) {
         var1.func_147449_b(var2, var3, var4, Blocks.field_150439_ay);
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (this == Blocks.field_150439_ay) {
         var1.func_147449_b(var2, var3, var4, Blocks.field_150450_ax);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151137_ax;
   }

   @Override
   public int func_149679_a(int var1, Random var2) {
      return this.func_149745_a(var2) + var2.nextInt(var1 + 1);
   }

   @Override
   public int func_149745_a(Random var1) {
      return 4 + var1.nextInt(2);
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      super.func_149690_a(var1, var2, var3, var4, var5, var6, var7);
      if (this.func_149650_a(var5, var1.field_73012_v, var7) != Item.func_150898_a(this)) {
         int var8 = 1 + var1.field_73012_v.nextInt(5);
         this.func_149657_c(var1, var2, var3, var4, var8);
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_150187_a) {
         this.func_150186_m(var1, var2, var3, var4);
      }
   }

   private void func_150186_m(World var1, int var2, int var3, int var4) {
      Random var5 = var1.field_73012_v;
      double var6 = 0.0625;

      for(int var8 = 0; var8 < 6; ++var8) {
         double var9 = (double)((float)var2 + var5.nextFloat());
         double var11 = (double)((float)var3 + var5.nextFloat());
         double var13 = (double)((float)var4 + var5.nextFloat());
         if (var8 == 0 && !var1.func_147439_a(var2, var3 + 1, var4).func_149662_c()) {
            var11 = (double)(var3 + 1) + var6;
         }

         if (var8 == 1 && !var1.func_147439_a(var2, var3 - 1, var4).func_149662_c()) {
            var11 = (double)(var3 + 0) - var6;
         }

         if (var8 == 2 && !var1.func_147439_a(var2, var3, var4 + 1).func_149662_c()) {
            var13 = (double)(var4 + 1) + var6;
         }

         if (var8 == 3 && !var1.func_147439_a(var2, var3, var4 - 1).func_149662_c()) {
            var13 = (double)(var4 + 0) - var6;
         }

         if (var8 == 4 && !var1.func_147439_a(var2 + 1, var3, var4).func_149662_c()) {
            var9 = (double)(var2 + 1) + var6;
         }

         if (var8 == 5 && !var1.func_147439_a(var2 - 1, var3, var4).func_149662_c()) {
            var9 = (double)(var2 + 0) - var6;
         }

         if (var9 < (double)var2
            || var9 > (double)(var2 + 1)
            || var11 < 0.0
            || var11 > (double)(var3 + 1)
            || var13 < (double)var4
            || var13 > (double)(var4 + 1)) {
            var1.func_72869_a("reddust", var9, var11, var13, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Blocks.field_150450_ax);
   }
}
