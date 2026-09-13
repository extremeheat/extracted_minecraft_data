package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
   private boolean field_150181_a = true;
   private Set field_150179_b = new HashSet();
   private IIcon field_150182_M;
   private IIcon field_150183_N;
   private IIcon field_150184_O;
   private IIcon field_150180_P;

   public BlockRedstoneWire() {
      super(Material.field_151594_q);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
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
   public int func_149645_b() {
      return 5;
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      return 8388608;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return World.func_147466_a(var1, var2, var3 - 1, var4) || var1.func_147439_a(var2, var3 - 1, var4) == Blocks.field_150426_aN;
   }

   private void func_150177_e(World var1, int var2, int var3, int var4) {
      this.func_150175_a(var1, var2, var3, var4, var2, var3, var4);
      ArrayList var5 = new ArrayList(this.field_150179_b);
      this.field_150179_b.clear();

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         ChunkPosition var7 = (ChunkPosition)var5.get(var6);
         var1.func_147459_d(var7.field_151329_a, var7.field_151327_b, var7.field_151328_c, this);
      }
   }

   private void func_150175_a(World var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = var1.func_72805_g(var2, var3, var4);
      int var9 = 0;
      var9 = this.func_150178_a(var1, var5, var6, var7, var9);
      this.field_150181_a = false;
      int var10 = var1.func_94572_D(var2, var3, var4);
      this.field_150181_a = true;
      if (var10 > 0 && var10 > var9 - 1) {
         var9 = var10;
      }

      int var11 = 0;

      for(int var12 = 0; var12 < 4; ++var12) {
         int var13 = var2;
         int var14 = var4;
         if (var12 == 0) {
            var13 = var2 - 1;
         }

         if (var12 == 1) {
            ++var13;
         }

         if (var12 == 2) {
            var14 = var4 - 1;
         }

         if (var12 == 3) {
            ++var14;
         }

         if (var13 != var5 || var14 != var7) {
            var11 = this.func_150178_a(var1, var13, var3, var14, var11);
         }

         if (var1.func_147439_a(var13, var3, var14).func_149721_r() && !var1.func_147439_a(var2, var3 + 1, var4).func_149721_r()) {
            if ((var13 != var5 || var14 != var7) && var3 >= var6) {
               var11 = this.func_150178_a(var1, var13, var3 + 1, var14, var11);
            }
         } else if (!var1.func_147439_a(var13, var3, var14).func_149721_r() && (var13 != var5 || var14 != var7) && var3 <= var6) {
            var11 = this.func_150178_a(var1, var13, var3 - 1, var14, var11);
         }
      }

      if (var11 > var9) {
         var9 = var11 - 1;
      } else if (var9 > 0) {
         --var9;
      } else {
         var9 = 0;
      }

      if (var10 > var9 - 1) {
         var9 = var10;
      }

      if (var8 != var9) {
         var1.func_72921_c(var2, var3, var4, var9, 2);
         this.field_150179_b.add(new ChunkPosition(var2, var3, var4));
         this.field_150179_b.add(new ChunkPosition(var2 - 1, var3, var4));
         this.field_150179_b.add(new ChunkPosition(var2 + 1, var3, var4));
         this.field_150179_b.add(new ChunkPosition(var2, var3 - 1, var4));
         this.field_150179_b.add(new ChunkPosition(var2, var3 + 1, var4));
         this.field_150179_b.add(new ChunkPosition(var2, var3, var4 - 1));
         this.field_150179_b.add(new ChunkPosition(var2, var3, var4 + 1));
      }
   }

   private void func_150172_m(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2, var3, var4) == this) {
         var1.func_147459_d(var2, var3, var4, this);
         var1.func_147459_d(var2 - 1, var3, var4, this);
         var1.func_147459_d(var2 + 1, var3, var4, this);
         var1.func_147459_d(var2, var3, var4 - 1, this);
         var1.func_147459_d(var2, var3, var4 + 1, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147459_d(var2, var3 + 1, var4, this);
      }
   }

   @Override
   public void func_149726_b(World var1, int var2, int var3, int var4) {
      super.func_149726_b(var1, var2, var3, var4);
      if (!var1.field_72995_K) {
         this.func_150177_e(var1, var2, var3, var4);
         var1.func_147459_d(var2, var3 + 1, var4, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         this.func_150172_m(var1, var2 - 1, var3, var4);
         this.func_150172_m(var1, var2 + 1, var3, var4);
         this.func_150172_m(var1, var2, var3, var4 - 1);
         this.func_150172_m(var1, var2, var3, var4 + 1);
         if (var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
            this.func_150172_m(var1, var2 - 1, var3 + 1, var4);
         } else {
            this.func_150172_m(var1, var2 - 1, var3 - 1, var4);
         }

         if (var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
            this.func_150172_m(var1, var2 + 1, var3 + 1, var4);
         } else {
            this.func_150172_m(var1, var2 + 1, var3 - 1, var4);
         }

         if (var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
            this.func_150172_m(var1, var2, var3 + 1, var4 - 1);
         } else {
            this.func_150172_m(var1, var2, var3 - 1, var4 - 1);
         }

         if (var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
            this.func_150172_m(var1, var2, var3 + 1, var4 + 1);
         } else {
            this.func_150172_m(var1, var2, var3 - 1, var4 + 1);
         }
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      super.func_149749_a(var1, var2, var3, var4, var5, var6);
      if (!var1.field_72995_K) {
         var1.func_147459_d(var2, var3 + 1, var4, this);
         var1.func_147459_d(var2, var3 - 1, var4, this);
         var1.func_147459_d(var2 + 1, var3, var4, this);
         var1.func_147459_d(var2 - 1, var3, var4, this);
         var1.func_147459_d(var2, var3, var4 + 1, this);
         var1.func_147459_d(var2, var3, var4 - 1, this);
         this.func_150177_e(var1, var2, var3, var4);
         this.func_150172_m(var1, var2 - 1, var3, var4);
         this.func_150172_m(var1, var2 + 1, var3, var4);
         this.func_150172_m(var1, var2, var3, var4 - 1);
         this.func_150172_m(var1, var2, var3, var4 + 1);
         if (var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
            this.func_150172_m(var1, var2 - 1, var3 + 1, var4);
         } else {
            this.func_150172_m(var1, var2 - 1, var3 - 1, var4);
         }

         if (var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
            this.func_150172_m(var1, var2 + 1, var3 + 1, var4);
         } else {
            this.func_150172_m(var1, var2 + 1, var3 - 1, var4);
         }

         if (var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
            this.func_150172_m(var1, var2, var3 + 1, var4 - 1);
         } else {
            this.func_150172_m(var1, var2, var3 - 1, var4 - 1);
         }

         if (var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
            this.func_150172_m(var1, var2, var3 + 1, var4 + 1);
         } else {
            this.func_150172_m(var1, var2, var3 - 1, var4 + 1);
         }
      }
   }

   private int func_150178_a(World var1, int var2, int var3, int var4, int var5) {
      if (var1.func_147439_a(var2, var3, var4) != this) {
         return var5;
      } else {
         int var6 = var1.func_72805_g(var2, var3, var4);
         return var6 > var5 ? var6 : var5;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (!var1.field_72995_K) {
         boolean var6 = this.func_149742_c(var1, var2, var3, var4);
         if (var6) {
            this.func_150177_e(var1, var2, var3, var4);
         } else {
            this.func_149697_b(var1, var2, var3, var4, 0, 0);
            var1.func_147468_f(var2, var3, var4);
         }

         super.func_149695_a(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151137_ax;
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return !this.field_150181_a ? 0 : this.func_149709_b(var1, var2, var3, var4, var5);
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (!this.field_150181_a) {
         return 0;
      } else {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if (var6 == 0) {
            return 0;
         } else if (var5 == 1) {
            return var6;
         } else {
            boolean var7 = func_150176_g(var1, var2 - 1, var3, var4, 1)
               || !var1.func_147439_a(var2 - 1, var3, var4).func_149721_r() && func_150176_g(var1, var2 - 1, var3 - 1, var4, -1);
            boolean var8 = func_150176_g(var1, var2 + 1, var3, var4, 3)
               || !var1.func_147439_a(var2 + 1, var3, var4).func_149721_r() && func_150176_g(var1, var2 + 1, var3 - 1, var4, -1);
            boolean var9 = func_150176_g(var1, var2, var3, var4 - 1, 2)
               || !var1.func_147439_a(var2, var3, var4 - 1).func_149721_r() && func_150176_g(var1, var2, var3 - 1, var4 - 1, -1);
            boolean var10 = func_150176_g(var1, var2, var3, var4 + 1, 0)
               || !var1.func_147439_a(var2, var3, var4 + 1).func_149721_r() && func_150176_g(var1, var2, var3 - 1, var4 + 1, -1);
            if (!var1.func_147439_a(var2, var3 + 1, var4).func_149721_r()) {
               if (var1.func_147439_a(var2 - 1, var3, var4).func_149721_r() && func_150176_g(var1, var2 - 1, var3 + 1, var4, -1)) {
                  var7 = true;
               }

               if (var1.func_147439_a(var2 + 1, var3, var4).func_149721_r() && func_150176_g(var1, var2 + 1, var3 + 1, var4, -1)) {
                  var8 = true;
               }

               if (var1.func_147439_a(var2, var3, var4 - 1).func_149721_r() && func_150176_g(var1, var2, var3 + 1, var4 - 1, -1)) {
                  var9 = true;
               }

               if (var1.func_147439_a(var2, var3, var4 + 1).func_149721_r() && func_150176_g(var1, var2, var3 + 1, var4 + 1, -1)) {
                  var10 = true;
               }
            }

            if (!var9 && !var8 && !var7 && !var10 && var5 >= 2 && var5 <= 5) {
               return var6;
            } else if (var5 == 2 && var9 && !var7 && !var8) {
               return var6;
            } else if (var5 == 3 && var10 && !var7 && !var8) {
               return var6;
            } else if (var5 == 4 && var7 && !var9 && !var10) {
               return var6;
            } else {
               return var5 == 5 && var8 && !var9 && !var10 ? var6 : 0;
            }
         }
      }
   }

   @Override
   public boolean func_149744_f() {
      return this.field_150181_a;
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if (var6 > 0) {
         double var7 = (double)var2 + 0.5 + ((double)var5.nextFloat() - 0.5) * 0.2;
         double var9 = (double)((float)var3 + 0.0625F);
         double var11 = (double)var4 + 0.5 + ((double)var5.nextFloat() - 0.5) * 0.2;
         float var13 = (float)var6 / 15.0F;
         float var14 = var13 * 0.6F + 0.4F;
         if (var6 == 0) {
            var14 = 0.0F;
         }

         float var15 = var13 * var13 * 0.7F - 0.5F;
         float var16 = var13 * var13 * 0.6F - 0.7F;
         if (var15 < 0.0F) {
            var15 = 0.0F;
         }

         if (var16 < 0.0F) {
            var16 = 0.0F;
         }

         var1.func_72869_a("reddust", var7, var9, var11, (double)var14, (double)var15, (double)var16);
      }
   }

   public static boolean func_150174_f(IBlockAccess var0, int var1, int var2, int var3, int var4) {
      Block var5 = var0.func_147439_a(var1, var2, var3);
      if (var5 == Blocks.field_150488_af) {
         return true;
      } else if (!Blocks.field_150413_aR.func_149907_e(var5)) {
         return var5.func_149744_f() && var4 != -1;
      } else {
         int var6 = var0.func_72805_g(var1, var2, var3);
         return var4 == (var6 & 3) || var4 == Direction.field_71580_e[var6 & 3];
      }
   }

   public static boolean func_150176_g(IBlockAccess var0, int var1, int var2, int var3, int var4) {
      if (func_150174_f(var0, var1, var2, var3, var4)) {
         return true;
      } else if (var0.func_147439_a(var1, var2, var3) == Blocks.field_150416_aS) {
         int var5 = var0.func_72805_g(var1, var2, var3);
         return var4 == (var5 & 3);
      } else {
         return false;
      }
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151137_ax;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150182_M = var1.func_94245_a(this.func_149641_N() + "_" + "cross");
      this.field_150183_N = var1.func_94245_a(this.func_149641_N() + "_" + "line");
      this.field_150184_O = var1.func_94245_a(this.func_149641_N() + "_" + "cross_overlay");
      this.field_150180_P = var1.func_94245_a(this.func_149641_N() + "_" + "line_overlay");
      this.field_149761_L = this.field_150182_M;
   }

   public static IIcon func_150173_e(String var0) {
      if (var0.equals("cross")) {
         return Blocks.field_150488_af.field_150182_M;
      } else if (var0.equals("line")) {
         return Blocks.field_150488_af.field_150183_N;
      } else if (var0.equals("cross_overlay")) {
         return Blocks.field_150488_af.field_150184_O;
      } else {
         return var0.equals("line_overlay") ? Blocks.field_150488_af.field_150180_P : null;
      }
   }
}
