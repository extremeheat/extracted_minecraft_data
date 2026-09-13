package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public BlockPistonMoving() {
      super(Material.field_76233_E);
      this.func_149711_c(-1.0F);
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return null;
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      TileEntity var7 = var1.func_147438_o(var2, var3, var4);
      if (var7 instanceof TileEntityPiston) {
         ((TileEntityPiston)var7).func_145866_f();
      } else {
         super.func_149749_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return false;
   }

   @Override
   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      return false;
   }

   @Override
   public int func_149645_b() {
      return -1;
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
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (!var1.field_72995_K && var1.func_147438_o(var2, var3, var4) == null) {
         var1.func_147468_f(var2, var3, var4);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!var1.field_72995_K) {
         TileEntityPiston var8 = this.func_149963_e(var1, var2, var3, var4);
         if (var8 != null) {
            var8.func_145861_a().func_149697_b(var1, var2, var3, var4, var8.func_145832_p(), 0);
         }
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         var1.func_147438_o(var2, var3, var4);
      }
   }

   public static TileEntity func_149962_a(Block var0, int var1, int var2, boolean var3, boolean var4) {
      return new TileEntityPiston(var0, var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      TileEntityPiston var5 = this.func_149963_e(var1, var2, var3, var4);
      if (var5 == null) {
         return null;
      } else {
         float var6 = var5.func_145860_a(0.0F);
         if (var5.func_145868_b()) {
            var6 = 1.0F - var6;
         }

         return this.func_149964_a(var1, var2, var3, var4, var5.func_145861_a(), var6, var5.func_145864_c());
      }
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      TileEntityPiston var5 = this.func_149963_e(var1, var2, var3, var4);
      if (var5 != null) {
         Block var6 = var5.func_145861_a();
         if (var6 == this || var6.func_149688_o() == Material.field_151579_a) {
            return;
         }

         var6.func_149719_a(var1, var2, var3, var4);
         float var7 = var5.func_145860_a(0.0F);
         if (var5.func_145868_b()) {
            var7 = 1.0F - var7;
         }

         int var8 = var5.func_145864_c();
         this.field_149759_B = var6.func_149704_x() - (double)((float)Facing.field_71586_b[var8] * var7);
         this.field_149760_C = var6.func_149665_z() - (double)((float)Facing.field_71587_c[var8] * var7);
         this.field_149754_D = var6.func_149706_B() - (double)((float)Facing.field_71585_d[var8] * var7);
         this.field_149755_E = var6.func_149753_y() - (double)((float)Facing.field_71586_b[var8] * var7);
         this.field_149756_F = var6.func_149669_A() - (double)((float)Facing.field_71587_c[var8] * var7);
         this.field_149757_G = var6.func_149693_C() - (double)((float)Facing.field_71585_d[var8] * var7);
      }
   }

   public AxisAlignedBB func_149964_a(World var1, int var2, int var3, int var4, Block var5, float var6, int var7) {
      if (var5 != this && var5.func_149688_o() != Material.field_151579_a) {
         AxisAlignedBB var8 = var5.func_149668_a(var1, var2, var3, var4);
         if (var8 == null) {
            return null;
         } else {
            if (Facing.field_71586_b[var7] < 0) {
               var8.field_72340_a -= (double)((float)Facing.field_71586_b[var7] * var6);
            } else {
               var8.field_72336_d -= (double)((float)Facing.field_71586_b[var7] * var6);
            }

            if (Facing.field_71587_c[var7] < 0) {
               var8.field_72338_b -= (double)((float)Facing.field_71587_c[var7] * var6);
            } else {
               var8.field_72337_e -= (double)((float)Facing.field_71587_c[var7] * var6);
            }

            if (Facing.field_71585_d[var7] < 0) {
               var8.field_72339_c -= (double)((float)Facing.field_71585_d[var7] * var6);
            } else {
               var8.field_72334_f -= (double)((float)Facing.field_71585_d[var7] * var6);
            }

            return var8;
         }
      } else {
         return null;
      }
   }

   private TileEntityPiston func_149963_e(IBlockAccess var1, int var2, int var3, int var4) {
      TileEntity var5 = var1.func_147438_o(var2, var3, var4);
      return var5 instanceof TileEntityPiston ? (TileEntityPiston)var5 : null;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Item.func_150899_d(0);
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("piston_top_normal");
   }
}
