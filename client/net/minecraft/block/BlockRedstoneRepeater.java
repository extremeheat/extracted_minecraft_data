package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
   public static final double[] field_149973_b = new double[]{-0.0625, 0.0625, 0.1875, 0.3125};
   private static final int[] field_149974_M = new int[]{1, 2, 3, 4};

   protected BlockRedstoneRepeater(boolean var1) {
      super(var1);
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      int var10 = var1.func_72805_g(var2, var3, var4);
      int var11 = (var10 & 12) >> 2;
      var11 = var11 + 1 << 2 & 12;
      var1.func_72921_c(var2, var3, var4, var11 | var10 & 3, 3);
      return true;
   }

   @Override
   protected int func_149901_b(int var1) {
      return field_149974_M[(var1 & 12) >> 2] * 2;
   }

   @Override
   protected BlockRedstoneDiode func_149906_e() {
      return Blocks.field_150416_aS;
   }

   @Override
   protected BlockRedstoneDiode func_149898_i() {
      return Blocks.field_150413_aR;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151107_aW;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151107_aW;
   }

   @Override
   public int func_149645_b() {
      return 15;
   }

   @Override
   public boolean func_149910_g(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return this.func_149902_h(var1, var2, var3, var4, var5) > 0;
   }

   @Override
   protected boolean func_149908_a(Block var1) {
      return func_149909_d(var1);
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (this.field_149914_a) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         int var7 = func_149895_l(var6);
         double var8 = (double)((float)var2 + 0.5F) + (double)(var5.nextFloat() - 0.5F) * 0.2;
         double var10 = (double)((float)var3 + 0.4F) + (double)(var5.nextFloat() - 0.5F) * 0.2;
         double var12 = (double)((float)var4 + 0.5F) + (double)(var5.nextFloat() - 0.5F) * 0.2;
         double var14 = 0.0;
         double var16 = 0.0;
         if (var5.nextInt(2) == 0) {
            switch(var7) {
               case 0:
                  var16 = -0.3125;
                  break;
               case 1:
                  var14 = 0.3125;
                  break;
               case 2:
                  var16 = 0.3125;
                  break;
               case 3:
                  var14 = -0.3125;
            }
         } else {
            int var18 = (var6 & 12) >> 2;
            switch(var7) {
               case 0:
                  var16 = field_149973_b[var18];
                  break;
               case 1:
                  var14 = -field_149973_b[var18];
                  break;
               case 2:
                  var16 = -field_149973_b[var18];
                  break;
               case 3:
                  var14 = field_149973_b[var18];
            }
         }

         var1.func_72869_a("reddust", var8 + var14, var10, var12 + var16, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      super.func_149749_a(var1, var2, var3, var4, var5, var6);
      this.func_149911_e(var1, var2, var3, var4);
   }
}
