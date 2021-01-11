package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class BlockFire extends Block {
   public static final PropertyInteger field_176543_a = PropertyInteger.func_177719_a("age", 0, 15);
   public static final PropertyBool field_176540_b = PropertyBool.func_177716_a("flip");
   public static final PropertyBool field_176544_M = PropertyBool.func_177716_a("alt");
   public static final PropertyBool field_176545_N = PropertyBool.func_177716_a("north");
   public static final PropertyBool field_176546_O = PropertyBool.func_177716_a("east");
   public static final PropertyBool field_176541_P = PropertyBool.func_177716_a("south");
   public static final PropertyBool field_176539_Q = PropertyBool.func_177716_a("west");
   public static final PropertyInteger field_176542_R = PropertyInteger.func_177719_a("upper", 0, 2);
   private final Map<Block, Integer> field_149849_a = Maps.newIdentityHashMap();
   private final Map<Block, Integer> field_149848_b = Maps.newIdentityHashMap();

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      int var4 = var3.func_177958_n();
      int var5 = var3.func_177956_o();
      int var6 = var3.func_177952_p();
      if (!World.func_175683_a(var2, var3.func_177977_b()) && !Blocks.field_150480_ab.func_176535_e(var2, var3.func_177977_b())) {
         boolean var7 = (var4 + var5 + var6 & 1) == 1;
         boolean var8 = (var4 / 2 + var5 / 2 + var6 / 2 & 1) == 1;
         int var9 = 0;
         if (this.func_176535_e(var2, var3.func_177984_a())) {
            var9 = var7 ? 1 : 2;
         }

         return var1.func_177226_a(field_176545_N, this.func_176535_e(var2, var3.func_177978_c())).func_177226_a(field_176546_O, this.func_176535_e(var2, var3.func_177974_f())).func_177226_a(field_176541_P, this.func_176535_e(var2, var3.func_177968_d())).func_177226_a(field_176539_Q, this.func_176535_e(var2, var3.func_177976_e())).func_177226_a(field_176542_R, var9).func_177226_a(field_176540_b, var8).func_177226_a(field_176544_M, var7);
      } else {
         return this.func_176223_P();
      }
   }

   protected BlockFire() {
      super(Material.field_151581_o);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176543_a, 0).func_177226_a(field_176540_b, false).func_177226_a(field_176544_M, false).func_177226_a(field_176545_N, false).func_177226_a(field_176546_O, false).func_177226_a(field_176541_P, false).func_177226_a(field_176539_Q, false).func_177226_a(field_176542_R, 0));
      this.func_149675_a(true);
   }

   public static void func_149843_e() {
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150344_f, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150373_bw, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150376_bx, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180390_bo, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180391_bp, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180392_bq, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180386_br, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180385_bs, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180387_bt, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180407_aO, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180408_aP, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180404_aQ, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180403_aR, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180406_aS, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_180405_aT, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150476_ad, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150487_bG, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150485_bF, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150481_bH, 5, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150364_r, 5, 5);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150363_s, 5, 5);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150362_t, 30, 60);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150361_u, 30, 60);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150342_X, 30, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150335_W, 15, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150329_H, 60, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150398_cm, 60, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150327_N, 60, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150328_O, 60, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150330_I, 60, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150325_L, 30, 60);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150395_bd, 15, 100);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150402_ci, 5, 5);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150407_cf, 60, 20);
      Blocks.field_150480_ab.func_180686_a(Blocks.field_150404_cg, 60, 20);
   }

   public void func_180686_a(Block var1, int var2, int var3) {
      this.field_149849_a.put(var1, var2);
      this.field_149848_b.put(var1, var3);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public int func_149738_a(World var1) {
      return 30;
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.func_82736_K().func_82766_b("doFireTick")) {
         if (!this.func_176196_c(var1, var2)) {
            var1.func_175698_g(var2);
         }

         Block var5 = var1.func_180495_p(var2.func_177977_b()).func_177230_c();
         boolean var6 = var5 == Blocks.field_150424_aL;
         if (var1.field_73011_w instanceof WorldProviderEnd && var5 == Blocks.field_150357_h) {
            var6 = true;
         }

         if (!var6 && var1.func_72896_J() && this.func_176537_d(var1, var2)) {
            var1.func_175698_g(var2);
         } else {
            int var7 = (Integer)var3.func_177229_b(field_176543_a);
            if (var7 < 15) {
               var3 = var3.func_177226_a(field_176543_a, var7 + var4.nextInt(3) / 2);
               var1.func_180501_a(var2, var3, 4);
            }

            var1.func_175684_a(var2, this, this.func_149738_a(var1) + var4.nextInt(10));
            if (!var6) {
               if (!this.func_176533_e(var1, var2)) {
                  if (!World.func_175683_a(var1, var2.func_177977_b()) || var7 > 3) {
                     var1.func_175698_g(var2);
                  }

                  return;
               }

               if (!this.func_176535_e(var1, var2.func_177977_b()) && var7 == 15 && var4.nextInt(4) == 0) {
                  var1.func_175698_g(var2);
                  return;
               }
            }

            boolean var8 = var1.func_180502_D(var2);
            byte var9 = 0;
            if (var8) {
               var9 = -50;
            }

            this.func_176536_a(var1, var2.func_177974_f(), 300 + var9, var4, var7);
            this.func_176536_a(var1, var2.func_177976_e(), 300 + var9, var4, var7);
            this.func_176536_a(var1, var2.func_177977_b(), 250 + var9, var4, var7);
            this.func_176536_a(var1, var2.func_177984_a(), 250 + var9, var4, var7);
            this.func_176536_a(var1, var2.func_177978_c(), 300 + var9, var4, var7);
            this.func_176536_a(var1, var2.func_177968_d(), 300 + var9, var4, var7);

            for(int var10 = -1; var10 <= 1; ++var10) {
               for(int var11 = -1; var11 <= 1; ++var11) {
                  for(int var12 = -1; var12 <= 4; ++var12) {
                     if (var10 != 0 || var12 != 0 || var11 != 0) {
                        int var13 = 100;
                        if (var12 > 1) {
                           var13 += (var12 - 1) * 100;
                        }

                        BlockPos var14 = var2.func_177982_a(var10, var12, var11);
                        int var15 = this.func_176538_m(var1, var14);
                        if (var15 > 0) {
                           int var16 = (var15 + 40 + var1.func_175659_aa().func_151525_a() * 7) / (var7 + 30);
                           if (var8) {
                              var16 /= 2;
                           }

                           if (var16 > 0 && var4.nextInt(var13) <= var16 && (!var1.func_72896_J() || !this.func_176537_d(var1, var14))) {
                              int var17 = var7 + var4.nextInt(5) / 4;
                              if (var17 > 15) {
                                 var17 = 15;
                              }

                              var1.func_180501_a(var14, var3.func_177226_a(field_176543_a, var17), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean func_176537_d(World var1, BlockPos var2) {
      return var1.func_175727_C(var2) || var1.func_175727_C(var2.func_177976_e()) || var1.func_175727_C(var2.func_177974_f()) || var1.func_175727_C(var2.func_177978_c()) || var1.func_175727_C(var2.func_177968_d());
   }

   public boolean func_149698_L() {
      return false;
   }

   private int func_176532_c(Block var1) {
      Integer var2 = (Integer)this.field_149848_b.get(var1);
      return var2 == null ? 0 : var2;
   }

   private int func_176534_d(Block var1) {
      Integer var2 = (Integer)this.field_149849_a.get(var1);
      return var2 == null ? 0 : var2;
   }

   private void func_176536_a(World var1, BlockPos var2, int var3, Random var4, int var5) {
      int var6 = this.func_176532_c(var1.func_180495_p(var2).func_177230_c());
      if (var4.nextInt(var3) < var6) {
         IBlockState var7 = var1.func_180495_p(var2);
         if (var4.nextInt(var5 + 10) < 5 && !var1.func_175727_C(var2)) {
            int var8 = var5 + var4.nextInt(5) / 4;
            if (var8 > 15) {
               var8 = 15;
            }

            var1.func_180501_a(var2, this.func_176223_P().func_177226_a(field_176543_a, var8), 3);
         } else {
            var1.func_175698_g(var2);
         }

         if (var7.func_177230_c() == Blocks.field_150335_W) {
            Blocks.field_150335_W.func_176206_d(var1, var2, var7.func_177226_a(BlockTNT.field_176246_a, true));
         }
      }

   }

   private boolean func_176533_e(World var1, BlockPos var2) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         if (this.func_176535_e(var1, var2.func_177972_a(var6))) {
            return true;
         }
      }

      return false;
   }

   private int func_176538_m(World var1, BlockPos var2) {
      if (!var1.func_175623_d(var2)) {
         return 0;
      } else {
         int var3 = 0;
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            var3 = Math.max(this.func_176534_d(var1.func_180495_p(var2.func_177972_a(var7)).func_177230_c()), var3);
         }

         return var3;
      }
   }

   public boolean func_149703_v() {
      return false;
   }

   public boolean func_176535_e(IBlockAccess var1, BlockPos var2) {
      return this.func_176534_d(var1.func_180495_p(var2).func_177230_c()) > 0;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return World.func_175683_a(var1, var2.func_177977_b()) || this.func_176533_e(var1, var2);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!World.func_175683_a(var1, var2.func_177977_b()) && !this.func_176533_e(var1, var2)) {
         var1.func_175698_g(var2);
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (var1.field_73011_w.func_177502_q() > 0 || !Blocks.field_150427_aO.func_176548_d(var1, var2)) {
         if (!World.func_175683_a(var1, var2.func_177977_b()) && !this.func_176533_e(var1, var2)) {
            var1.func_175698_g(var2);
         } else {
            var1.func_175684_a(var2, this, this.func_149738_a(var1) + var1.field_73012_v.nextInt(10));
         }
      }
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var4.nextInt(24) == 0) {
         var1.func_72980_b((double)((float)var2.func_177958_n() + 0.5F), (double)((float)var2.func_177956_o() + 0.5F), (double)((float)var2.func_177952_p() + 0.5F), "fire.fire", 1.0F + var4.nextFloat(), var4.nextFloat() * 0.7F + 0.3F, false);
      }

      int var5;
      double var6;
      double var8;
      double var10;
      if (!World.func_175683_a(var1, var2.func_177977_b()) && !Blocks.field_150480_ab.func_176535_e(var1, var2.func_177977_b())) {
         if (Blocks.field_150480_ab.func_176535_e(var1, var2.func_177976_e())) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var2.func_177958_n() + var4.nextDouble() * 0.10000000149011612D;
               var8 = (double)var2.func_177956_o() + var4.nextDouble();
               var10 = (double)var2.func_177952_p() + var4.nextDouble();
               var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (Blocks.field_150480_ab.func_176535_e(var1, var2.func_177974_f())) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)(var2.func_177958_n() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var8 = (double)var2.func_177956_o() + var4.nextDouble();
               var10 = (double)var2.func_177952_p() + var4.nextDouble();
               var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (Blocks.field_150480_ab.func_176535_e(var1, var2.func_177978_c())) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var2.func_177958_n() + var4.nextDouble();
               var8 = (double)var2.func_177956_o() + var4.nextDouble();
               var10 = (double)var2.func_177952_p() + var4.nextDouble() * 0.10000000149011612D;
               var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (Blocks.field_150480_ab.func_176535_e(var1, var2.func_177968_d())) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var2.func_177958_n() + var4.nextDouble();
               var8 = (double)var2.func_177956_o() + var4.nextDouble();
               var10 = (double)(var2.func_177952_p() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (Blocks.field_150480_ab.func_176535_e(var1, var2.func_177984_a())) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var2.func_177958_n() + var4.nextDouble();
               var8 = (double)(var2.func_177956_o() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var10 = (double)var2.func_177952_p() + var4.nextDouble();
               var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(var5 = 0; var5 < 3; ++var5) {
            var6 = (double)var2.func_177958_n() + var4.nextDouble();
            var8 = (double)var2.func_177956_o() + var4.nextDouble() * 0.5D + 0.5D;
            var10 = (double)var2.func_177952_p() + var4.nextDouble();
            var1.func_175688_a(EnumParticleTypes.SMOKE_LARGE, var6, var8, var10, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public MapColor func_180659_g(IBlockState var1) {
      return MapColor.field_151656_f;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176543_a, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176543_a);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176543_a, field_176545_N, field_176546_O, field_176541_P, field_176539_Q, field_176542_R, field_176540_b, field_176544_M});
   }
}
