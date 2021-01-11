package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

public abstract class BlockLeaves extends BlockLeavesBase {
   public static final PropertyBool field_176237_a = PropertyBool.func_177716_a("decayable");
   public static final PropertyBool field_176236_b = PropertyBool.func_177716_a("check_decay");
   int[] field_150128_a;
   protected int field_150127_b;
   protected boolean field_176238_O;

   public BlockLeaves() {
      super(Material.field_151584_j, false);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149711_c(0.2F);
      this.func_149713_g(1);
      this.func_149672_a(field_149779_h);
   }

   public int func_149635_D() {
      return ColorizerFoliage.func_77470_a(0.5D, 1.0D);
   }

   public int func_180644_h(IBlockState var1) {
      return ColorizerFoliage.func_77468_c();
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return BiomeColorHelper.func_180287_b(var1, var2);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      byte var4 = 1;
      int var5 = var4 + 1;
      int var6 = var2.func_177958_n();
      int var7 = var2.func_177956_o();
      int var8 = var2.func_177952_p();
      if (var1.func_175707_a(new BlockPos(var6 - var5, var7 - var5, var8 - var5), new BlockPos(var6 + var5, var7 + var5, var8 + var5))) {
         for(int var9 = -var4; var9 <= var4; ++var9) {
            for(int var10 = -var4; var10 <= var4; ++var10) {
               for(int var11 = -var4; var11 <= var4; ++var11) {
                  BlockPos var12 = var2.func_177982_a(var9, var10, var11);
                  IBlockState var13 = var1.func_180495_p(var12);
                  if (var13.func_177230_c().func_149688_o() == Material.field_151584_j && !(Boolean)var13.func_177229_b(field_176236_b)) {
                     var1.func_180501_a(var12, var13.func_177226_a(field_176236_b, true), 4);
                  }
               }
            }
         }
      }

   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if ((Boolean)var3.func_177229_b(field_176236_b) && (Boolean)var3.func_177229_b(field_176237_a)) {
            byte var5 = 4;
            int var6 = var5 + 1;
            int var7 = var2.func_177958_n();
            int var8 = var2.func_177956_o();
            int var9 = var2.func_177952_p();
            byte var10 = 32;
            int var11 = var10 * var10;
            int var12 = var10 / 2;
            if (this.field_150128_a == null) {
               this.field_150128_a = new int[var10 * var10 * var10];
            }

            if (var1.func_175707_a(new BlockPos(var7 - var6, var8 - var6, var9 - var6), new BlockPos(var7 + var6, var8 + var6, var9 + var6))) {
               BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();
               int var14 = -var5;

               label116:
               while(true) {
                  int var15;
                  int var16;
                  if (var14 > var5) {
                     var14 = 1;

                     while(true) {
                        if (var14 > 4) {
                           break label116;
                        }

                        for(var15 = -var5; var15 <= var5; ++var15) {
                           for(var16 = -var5; var16 <= var5; ++var16) {
                              for(int var19 = -var5; var19 <= var5; ++var19) {
                                 if (this.field_150128_a[(var15 + var12) * var11 + (var16 + var12) * var10 + var19 + var12] == var14 - 1) {
                                    if (this.field_150128_a[(var15 + var12 - 1) * var11 + (var16 + var12) * var10 + var19 + var12] == -2) {
                                       this.field_150128_a[(var15 + var12 - 1) * var11 + (var16 + var12) * var10 + var19 + var12] = var14;
                                    }

                                    if (this.field_150128_a[(var15 + var12 + 1) * var11 + (var16 + var12) * var10 + var19 + var12] == -2) {
                                       this.field_150128_a[(var15 + var12 + 1) * var11 + (var16 + var12) * var10 + var19 + var12] = var14;
                                    }

                                    if (this.field_150128_a[(var15 + var12) * var11 + (var16 + var12 - 1) * var10 + var19 + var12] == -2) {
                                       this.field_150128_a[(var15 + var12) * var11 + (var16 + var12 - 1) * var10 + var19 + var12] = var14;
                                    }

                                    if (this.field_150128_a[(var15 + var12) * var11 + (var16 + var12 + 1) * var10 + var19 + var12] == -2) {
                                       this.field_150128_a[(var15 + var12) * var11 + (var16 + var12 + 1) * var10 + var19 + var12] = var14;
                                    }

                                    if (this.field_150128_a[(var15 + var12) * var11 + (var16 + var12) * var10 + (var19 + var12 - 1)] == -2) {
                                       this.field_150128_a[(var15 + var12) * var11 + (var16 + var12) * var10 + (var19 + var12 - 1)] = var14;
                                    }

                                    if (this.field_150128_a[(var15 + var12) * var11 + (var16 + var12) * var10 + var19 + var12 + 1] == -2) {
                                       this.field_150128_a[(var15 + var12) * var11 + (var16 + var12) * var10 + var19 + var12 + 1] = var14;
                                    }
                                 }
                              }
                           }
                        }

                        ++var14;
                     }
                  }

                  for(var15 = -var5; var15 <= var5; ++var15) {
                     for(var16 = -var5; var16 <= var5; ++var16) {
                        Block var17 = var1.func_180495_p(var13.func_181079_c(var7 + var14, var8 + var15, var9 + var16)).func_177230_c();
                        if (var17 != Blocks.field_150364_r && var17 != Blocks.field_150363_s) {
                           if (var17.func_149688_o() == Material.field_151584_j) {
                              this.field_150128_a[(var14 + var12) * var11 + (var15 + var12) * var10 + var16 + var12] = -2;
                           } else {
                              this.field_150128_a[(var14 + var12) * var11 + (var15 + var12) * var10 + var16 + var12] = -1;
                           }
                        } else {
                           this.field_150128_a[(var14 + var12) * var11 + (var15 + var12) * var10 + var16 + var12] = 0;
                        }
                     }
                  }

                  ++var14;
               }
            }

            int var18 = this.field_150128_a[var12 * var11 + var12 * var10 + var12];
            if (var18 >= 0) {
               var1.func_180501_a(var2, var3.func_177226_a(field_176236_b, false), 4);
            } else {
               this.func_176235_d(var1, var2);
            }
         }

      }
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var1.func_175727_C(var2.func_177984_a()) && !World.func_175683_a(var1, var2.func_177977_b()) && var4.nextInt(15) == 1) {
         double var5 = (double)((float)var2.func_177958_n() + var4.nextFloat());
         double var7 = (double)var2.func_177956_o() - 0.05D;
         double var9 = (double)((float)var2.func_177952_p() + var4.nextFloat());
         var1.func_175688_a(EnumParticleTypes.DRIP_WATER, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      }

   }

   private void func_176235_d(World var1, BlockPos var2) {
      this.func_176226_b(var1, var2, var1.func_180495_p(var2), 0);
      var1.func_175698_g(var2);
   }

   public int func_149745_a(Random var1) {
      return var1.nextInt(20) == 0 ? 1 : 0;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150345_g);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.field_72995_K) {
         int var6 = this.func_176232_d(var3);
         if (var5 > 0) {
            var6 -= 2 << var5;
            if (var6 < 10) {
               var6 = 10;
            }
         }

         if (var1.field_73012_v.nextInt(var6) == 0) {
            Item var7 = this.func_180660_a(var3, var1.field_73012_v, var5);
            func_180635_a(var1, var2, new ItemStack(var7, 1, this.func_180651_a(var3)));
         }

         var6 = 200;
         if (var5 > 0) {
            var6 -= 10 << var5;
            if (var6 < 40) {
               var6 = 40;
            }
         }

         this.func_176234_a(var1, var2, var3, var6);
      }

   }

   protected void func_176234_a(World var1, BlockPos var2, IBlockState var3, int var4) {
   }

   protected int func_176232_d(IBlockState var1) {
      return 20;
   }

   public boolean func_149662_c() {
      return !this.field_150121_P;
   }

   public void func_150122_b(boolean var1) {
      this.field_176238_O = var1;
      this.field_150121_P = var1;
      this.field_150127_b = var1 ? 0 : 1;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return this.field_176238_O ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
   }

   public boolean func_176214_u() {
      return false;
   }

   public abstract BlockPlanks.EnumType func_176233_b(int var1);
}
