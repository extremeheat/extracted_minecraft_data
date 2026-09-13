package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public BlockTripWire() {
      super(Material.field_151594_q);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.func_149675_a(true);
   }

   @Override
   public int func_149738_a(World var1) {
      return 10;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149701_w() {
      return 1;
   }

   @Override
   public int func_149645_b() {
      return 30;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151007_F;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151007_F;
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      boolean var7 = (var6 & 2) == 2;
      boolean var8 = !World.func_147466_a(var1, var2, var3 - 1, var4);
      if (var7 != var8) {
         this.func_149697_b(var1, var2, var3, var4, var6, 0);
         var1.func_147468_f(var2, var3, var4);
      }
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      boolean var6 = (var5 & 4) == 4;
      boolean var7 = (var5 & 2) == 2;
      if (!var7) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!var6) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      int var5 = World.func_147466_a(var1, var2, var3 - 1, var4) ? 0 : 2;
      var1.func_72921_c(var2, var3, var4, var5, 3);
      this.func_150138_a(var1, var2, var3, var4, var5);
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      this.func_150138_a(var1, var2, var3, var4, var6 | 1);
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      if (!var1.field_72995_K) {
         if (var6.func_71045_bC() != null && var6.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
            var1.func_72921_c(var2, var3, var4, var5 | 8, 4);
         }
      }
   }

   private void func_150138_a(World var1, int var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < 2; ++var6) {
         for(int var7 = 1; var7 < 42; ++var7) {
            int var8 = var2 + Direction.field_71583_a[var6] * var7;
            int var9 = var4 + Direction.field_71581_b[var6] * var7;
            Block var10 = var1.func_147439_a(var8, var3, var9);
            if (var10 == Blocks.field_150479_bC) {
               int var11 = var1.func_72805_g(var8, var3, var9) & 3;
               if (var11 == Direction.field_71580_e[var6]) {
                  Blocks.field_150479_bC.func_150136_a(var1, var8, var3, var9, false, var1.func_72805_g(var8, var3, var9), true, var7, var5);
               }
               break;
            }

            if (var10 != Blocks.field_150473_bD) {
               break;
            }
         }
      }
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (!var1.field_72995_K) {
         if ((var1.func_72805_g(var2, var3, var4) & 1) != 1) {
            this.func_150140_e(var1, var2, var3, var4);
         }
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         if ((var1.func_72805_g(var2, var3, var4) & 1) == 1) {
            this.func_150140_e(var1, var2, var3, var4);
         }
      }
   }

   private void func_150140_e(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      boolean var6 = (var5 & 1) == 1;
      boolean var7 = false;
      List var8 = var1.func_72839_b(
         null,
         AxisAlignedBB.func_72330_a(
            (double)var2 + this.field_149759_B,
            (double)var3 + this.field_149760_C,
            (double)var4 + this.field_149754_D,
            (double)var2 + this.field_149755_E,
            (double)var3 + this.field_149756_F,
            (double)var4 + this.field_149757_G
         )
      );
      if (!var8.isEmpty()) {
         for(Entity var10 : var8) {
            if (!var10.func_145773_az()) {
               var7 = true;
               break;
            }
         }
      }

      if (var7 && !var6) {
         var5 |= 1;
      }

      if (!var7 && var6) {
         var5 &= -2;
      }

      if (var7 != var6) {
         var1.func_72921_c(var2, var3, var4, var5, 3);
         this.func_150138_a(var1, var2, var3, var4, var5);
      }

      if (var7) {
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
      }
   }

   public static boolean func_150139_a(IBlockAccess var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 + Direction.field_71583_a[var5];
      int var8 = var3 + Direction.field_71581_b[var5];
      Block var9 = var0.func_147439_a(var6, var2, var8);
      boolean var10 = (var4 & 2) == 2;
      if (var9 == Blocks.field_150479_bC) {
         int var13 = var0.func_72805_g(var6, var2, var8);
         int var14 = var13 & 3;
         return var14 == Direction.field_71580_e[var5];
      } else if (var9 == Blocks.field_150473_bD) {
         int var11 = var0.func_72805_g(var6, var2, var8);
         boolean var12 = (var11 & 2) == 2;
         return var10 == var12;
      } else {
         return false;
      }
   }
}
