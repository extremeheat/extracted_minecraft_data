package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer$EnumStatus;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {
   public static final int[][] field_149981_a = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
   private IIcon[] field_149980_b;
   private IIcon[] field_149982_M;
   private IIcon[] field_149983_N;

   public BlockBed() {
      super(Material.field_151580_n);
      this.func_149978_e();
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         int var10 = var1.func_72805_g(var2, var3, var4);
         if (!func_149975_b(var10)) {
            int var11 = func_149895_l(var10);
            var2 += field_149981_a[var11][0];
            var4 += field_149981_a[var11][1];
            if (var1.func_147439_a(var2, var3, var4) != this) {
               return true;
            }

            var10 = var1.func_72805_g(var2, var3, var4);
         }

         if (var1.field_73011_w.func_76567_e() && var1.func_72807_a(var2, var4) != BiomeGenBase.field_76778_j) {
            if (func_149976_c(var10)) {
               EntityPlayer var22 = null;

               for(EntityPlayer var25 : var1.field_73010_i) {
                  if (var25.func_70608_bn()) {
                     ChunkCoordinates var14 = var25.field_71081_bT;
                     if (var14.field_71574_a == var2 && var14.field_71572_b == var3 && var14.field_71573_c == var4) {
                        var22 = var25;
                     }
                  }
               }

               if (var22 != null) {
                  var5.func_146105_b(new ChatComponentTranslation("tile.bed.occupied"));
                  return true;
               }

               func_149979_a(var1, var2, var3, var4, false);
            }

            EntityPlayer$EnumStatus var23 = var5.func_71018_a(var2, var3, var4);
            if (var23 == EntityPlayer$EnumStatus.OK) {
               func_149979_a(var1, var2, var3, var4, true);
               return true;
            } else {
               if (var23 == EntityPlayer$EnumStatus.NOT_POSSIBLE_NOW) {
                  var5.func_146105_b(new ChatComponentTranslation("tile.bed.noSleep"));
               } else if (var23 == EntityPlayer$EnumStatus.NOT_SAFE) {
                  var5.func_146105_b(new ChatComponentTranslation("tile.bed.notSafe"));
               }

               return true;
            }
         } else {
            double var20 = (double)var2 + 0.5;
            double var13 = (double)var3 + 0.5;
            double var15 = (double)var4 + 0.5;
            var1.func_147468_f(var2, var3, var4);
            int var17 = func_149895_l(var10);
            var2 += field_149981_a[var17][0];
            var4 += field_149981_a[var17][1];
            if (var1.func_147439_a(var2, var3, var4) == this) {
               var1.func_147468_f(var2, var3, var4);
               var20 = (var20 + (double)var2 + 0.5) / 2.0;
               var13 = (var13 + (double)var3 + 0.5) / 2.0;
               var15 = (var15 + (double)var4 + 0.5) / 2.0;
            }

            var1.func_72885_a(null, (double)((float)var2 + 0.5F), (double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), 5.0F, true, true);
            return true;
         }
      }
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 0) {
         return Blocks.field_150344_f.func_149733_h(var1);
      } else {
         int var3 = func_149895_l(var2);
         int var4 = Direction.field_71584_h[var3][var1];
         int var5 = func_149975_b(var2) ? 1 : 0;
         if ((var5 != 1 || var4 != 2) && (var5 != 0 || var4 != 3)) {
            return var4 != 5 && var4 != 4 ? this.field_149983_N[var5] : this.field_149982_M[var5];
         } else {
            return this.field_149980_b[var5];
         }
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149983_N = new IIcon[]{var1.func_94245_a(this.func_149641_N() + "_feet_top"), var1.func_94245_a(this.func_149641_N() + "_head_top")};
      this.field_149980_b = new IIcon[]{var1.func_94245_a(this.func_149641_N() + "_feet_end"), var1.func_94245_a(this.func_149641_N() + "_head_end")};
      this.field_149982_M = new IIcon[]{var1.func_94245_a(this.func_149641_N() + "_feet_side"), var1.func_94245_a(this.func_149641_N() + "_head_side")};
   }

   @Override
   public int func_149645_b() {
      return 14;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_149978_e();
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      int var7 = func_149895_l(var6);
      if (func_149975_b(var6)) {
         if (var1.func_147439_a(var2 - field_149981_a[var7][0], var3, var4 - field_149981_a[var7][1]) != this) {
            var1.func_147468_f(var2, var3, var4);
         }
      } else if (var1.func_147439_a(var2 + field_149981_a[var7][0], var3, var4 + field_149981_a[var7][1]) != this) {
         var1.func_147468_f(var2, var3, var4);
         if (!var1.field_72995_K) {
            this.func_149697_b(var1, var2, var3, var4, var6, 0);
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return func_149975_b(var1) ? Item.func_150899_d(0) : Items.field_151104_aV;
   }

   private void func_149978_e() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static boolean func_149975_b(int var0) {
      return (var0 & 8) != 0;
   }

   public static boolean func_149976_c(int var0) {
      return (var0 & 4) != 0;
   }

   public static void func_149979_a(World var0, int var1, int var2, int var3, boolean var4) {
      int var5 = var0.func_72805_g(var1, var2, var3);
      if (var4) {
         var5 |= 4;
      } else {
         var5 &= -5;
      }

      var0.func_72921_c(var1, var2, var3, var5, 4);
   }

   public static ChunkCoordinates func_149977_a(World var0, int var1, int var2, int var3, int var4) {
      int var5 = var0.func_72805_g(var1, var2, var3);
      int var6 = BlockDirectional.func_149895_l(var5);

      for(int var7 = 0; var7 <= 1; ++var7) {
         int var8 = var1 - field_149981_a[var6][0] * var7 - 1;
         int var9 = var3 - field_149981_a[var6][1] * var7 - 1;
         int var10 = var8 + 2;
         int var11 = var9 + 2;

         for(int var12 = var8; var12 <= var10; ++var12) {
            for(int var13 = var9; var13 <= var11; ++var13) {
               if (World.func_147466_a(var0, var12, var2 - 1, var13)
                  && !var0.func_147439_a(var12, var2, var13).func_149688_o().func_76218_k()
                  && !var0.func_147439_a(var12, var2 + 1, var13).func_149688_o().func_76218_k()) {
                  if (var4 <= 0) {
                     return new ChunkCoordinates(var12, var2, var13);
                  }

                  --var4;
               }
            }
         }
      }

      return null;
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!func_149975_b(var5)) {
         super.func_149690_a(var1, var2, var3, var4, var5, var6, 0);
      }
   }

   @Override
   public int func_149656_h() {
      return 1;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151104_aV;
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      if (var6.field_71075_bZ.field_75098_d && func_149975_b(var5)) {
         int var7 = func_149895_l(var5);
         var2 -= field_149981_a[var7][0];
         var4 -= field_149981_a[var7][1];
         if (var1.func_147439_a(var2, var3, var4) == this) {
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }
}
