package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {
   protected BlockTrapDoor(Material var1) {
      super(var1);
      float var2 = 0.5F;
      float var3 = 1.0F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var3, 0.5F + var2);
      this.func_149647_a(CreativeTabs.field_78028_d);
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
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      return !func_150118_d(var1.func_72805_g(var2, var3, var4));
   }

   @Override
   public int func_149645_b() {
      return 0;
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149633_g(var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149668_a(var1, var2, var3, var4);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_150117_b(var1.func_72805_g(var2, var3, var4));
   }

   @Override
   public void func_149683_g() {
      float var1 = 0.1875F;
      this.func_149676_a(0.0F, 0.5F - var1 / 2.0F, 0.0F, 1.0F, 0.5F + var1 / 2.0F, 1.0F);
   }

   public void func_150117_b(int var1) {
      float var2 = 0.1875F;
      if ((var1 & 8) != 0) {
         this.func_149676_a(0.0F, 1.0F - var2, 0.0F, 1.0F, 1.0F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, var2, 1.0F);
      }

      if (func_150118_d(var1)) {
         if ((var1 & 3) == 0) {
            this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
         }

         if ((var1 & 3) == 1) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
         }

         if ((var1 & 3) == 2) {
            this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }

         if ((var1 & 3) == 3) {
            this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (this.field_149764_J == Material.field_151573_f) {
         return true;
      } else {
         int var10 = var1.func_72805_g(var2, var3, var4);
         var1.func_72921_c(var2, var3, var4, var10 ^ 4, 2);
         var1.func_72889_a(var5, 1003, var2, var3, var4, 0);
         return true;
      }
   }

   public void func_150120_a(World var1, int var2, int var3, int var4, boolean var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      boolean var7 = (var6 & 4) > 0;
      if (var7 != var5) {
         var1.func_72921_c(var2, var3, var4, var6 ^ 4, 2);
         var1.func_72889_a(null, 1003, var2, var3, var4, 0);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         int var7 = var2;
         int var8 = var4;
         if ((var6 & 3) == 0) {
            var8 = var4 + 1;
         }

         if ((var6 & 3) == 1) {
            --var8;
         }

         if ((var6 & 3) == 2) {
            var7 = var2 + 1;
         }

         if ((var6 & 3) == 3) {
            --var7;
         }

         if (!func_150119_a(var1.func_147439_a(var7, var3, var8))) {
            var1.func_147468_f(var2, var3, var4);
            this.func_149697_b(var1, var2, var3, var4, var6, 0);
         }

         boolean var9 = var1.func_72864_z(var2, var3, var4);
         if (var9 || var5.func_149744_f()) {
            this.func_150120_a(var1, var2, var3, var4, var9);
         }
      }
   }

   @Override
   public MovingObjectPosition func_149731_a(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149731_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      int var10 = 0;
      if (var5 == 2) {
         var10 = 0;
      }

      if (var5 == 3) {
         var10 = 1;
      }

      if (var5 == 4) {
         var10 = 2;
      }

      if (var5 == 5) {
         var10 = 3;
      }

      if (var5 != 1 && var5 != 0 && var7 > 0.5F) {
         var10 |= 8;
      }

      return var10;
   }

   @Override
   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      if (var5 == 0) {
         return false;
      } else if (var5 == 1) {
         return false;
      } else {
         if (var5 == 2) {
            ++var4;
         }

         if (var5 == 3) {
            --var4;
         }

         if (var5 == 4) {
            ++var2;
         }

         if (var5 == 5) {
            --var2;
         }

         return func_150119_a(var1.func_147439_a(var2, var3, var4));
      }
   }

   public static boolean func_150118_d(int var0) {
      return (var0 & 4) != 0;
   }

   private static boolean func_150119_a(Block var0) {
      return var0.field_149764_J.func_76218_k() && var0.func_149686_d()
         || var0 == Blocks.field_150426_aN
         || var0 instanceof BlockSlab
         || var0 instanceof BlockStairs;
   }
}
