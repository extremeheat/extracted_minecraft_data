package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockVine extends Block {
   public static final PropertyBool field_176277_a = PropertyBool.func_177716_a("up");
   public static final PropertyBool field_176273_b = PropertyBool.func_177716_a("north");
   public static final PropertyBool field_176278_M = PropertyBool.func_177716_a("east");
   public static final PropertyBool field_176279_N = PropertyBool.func_177716_a("south");
   public static final PropertyBool field_176280_O = PropertyBool.func_177716_a("west");
   public static final PropertyBool[] field_176274_P;

   public BlockVine() {
      super(Material.field_151582_l);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176277_a, false).func_177226_a(field_176273_b, false).func_177226_a(field_176278_M, false).func_177226_a(field_176279_N, false).func_177226_a(field_176280_O, false));
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176277_a, var2.func_180495_p(var3.func_177984_a()).func_177230_c().func_149637_q());
   }

   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176200_f(World var1, BlockPos var2) {
      return true;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      float var3 = 0.0625F;
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 1.0F;
      float var7 = 0.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      boolean var10 = false;
      if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176280_O)) {
         var7 = Math.max(var7, 0.0625F);
         var4 = 0.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var6 = 0.0F;
         var9 = 1.0F;
         var10 = true;
      }

      if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176278_M)) {
         var4 = Math.min(var4, 0.9375F);
         var7 = 1.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var6 = 0.0F;
         var9 = 1.0F;
         var10 = true;
      }

      if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176273_b)) {
         var9 = Math.max(var9, 0.0625F);
         var6 = 0.0F;
         var4 = 0.0F;
         var7 = 1.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var10 = true;
      }

      if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176279_N)) {
         var6 = Math.min(var6, 0.9375F);
         var9 = 1.0F;
         var4 = 0.0F;
         var7 = 1.0F;
         var5 = 0.0F;
         var8 = 1.0F;
         var10 = true;
      }

      if (!var10 && this.func_150093_a(var1.func_180495_p(var2.func_177984_a()).func_177230_c())) {
         var5 = Math.min(var5, 0.9375F);
         var8 = 1.0F;
         var4 = 0.0F;
         var7 = 1.0F;
         var6 = 0.0F;
         var9 = 1.0F;
      }

      this.func_149676_a(var4, var5, var6, var7, var8, var9);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      switch(var3) {
      case UP:
         return this.func_150093_a(var1.func_180495_p(var2.func_177984_a()).func_177230_c());
      case NORTH:
      case SOUTH:
      case EAST:
      case WEST:
         return this.func_150093_a(var1.func_180495_p(var2.func_177972_a(var3.func_176734_d())).func_177230_c());
      default:
         return false;
      }
   }

   private boolean func_150093_a(Block var1) {
      return var1.func_149686_d() && var1.field_149764_J.func_76230_c();
   }

   private boolean func_176269_e(World var1, BlockPos var2, IBlockState var3) {
      IBlockState var4 = var3;
      Iterator var5 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(true) {
         PropertyBool var7;
         IBlockState var8;
         do {
            EnumFacing var6;
            do {
               do {
                  if (!var5.hasNext()) {
                     if (func_176268_d(var3) == 0) {
                        return false;
                     }

                     if (var4 != var3) {
                        var1.func_180501_a(var2, var3, 2);
                     }

                     return true;
                  }

                  var6 = (EnumFacing)var5.next();
                  var7 = func_176267_a(var6);
               } while(!(Boolean)var3.func_177229_b(var7));
            } while(this.func_150093_a(var1.func_180495_p(var2.func_177972_a(var6)).func_177230_c()));

            var8 = var1.func_180495_p(var2.func_177984_a());
         } while(var8.func_177230_c() == this && (Boolean)var8.func_177229_b(var7));

         var3 = var3.func_177226_a(var7, false);
      }
   }

   public int func_149635_D() {
      return ColorizerFoliage.func_77468_c();
   }

   public int func_180644_h(IBlockState var1) {
      return ColorizerFoliage.func_77468_c();
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return var1.func_180494_b(var2).func_180625_c(var2);
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K && !this.func_176269_e(var1, var2, var3)) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if (var1.field_73012_v.nextInt(4) == 0) {
            byte var5 = 4;
            int var6 = 5;
            boolean var7 = false;

            label191:
            for(int var8 = -var5; var8 <= var5; ++var8) {
               for(int var9 = -var5; var9 <= var5; ++var9) {
                  for(int var10 = -1; var10 <= 1; ++var10) {
                     if (var1.func_180495_p(var2.func_177982_a(var8, var10, var9)).func_177230_c() == this) {
                        --var6;
                        if (var6 <= 0) {
                           var7 = true;
                           break label191;
                        }
                     }
                  }
               }
            }

            EnumFacing var18 = EnumFacing.func_176741_a(var4);
            BlockPos var19 = var2.func_177984_a();
            EnumFacing var24;
            if (var18 == EnumFacing.UP && var2.func_177956_o() < 255 && var1.func_175623_d(var19)) {
               if (!var7) {
                  IBlockState var21 = var3;
                  Iterator var23 = EnumFacing.Plane.HORIZONTAL.iterator();

                  while(true) {
                     do {
                        if (!var23.hasNext()) {
                           if ((Boolean)var21.func_177229_b(field_176273_b) || (Boolean)var21.func_177229_b(field_176278_M) || (Boolean)var21.func_177229_b(field_176279_N) || (Boolean)var21.func_177229_b(field_176280_O)) {
                              var1.func_180501_a(var19, var21, 2);
                           }

                           return;
                        }

                        var24 = (EnumFacing)var23.next();
                     } while(!var4.nextBoolean() && this.func_150093_a(var1.func_180495_p(var19.func_177972_a(var24)).func_177230_c()));

                     var21 = var21.func_177226_a(func_176267_a(var24), false);
                  }
               }
            } else {
               BlockPos var20;
               if (var18.func_176740_k().func_176722_c() && !(Boolean)var3.func_177229_b(func_176267_a(var18))) {
                  if (!var7) {
                     var20 = var2.func_177972_a(var18);
                     Block var22 = var1.func_180495_p(var20).func_177230_c();
                     if (var22.field_149764_J == Material.field_151579_a) {
                        var24 = var18.func_176746_e();
                        EnumFacing var25 = var18.func_176735_f();
                        boolean var26 = (Boolean)var3.func_177229_b(func_176267_a(var24));
                        boolean var27 = (Boolean)var3.func_177229_b(func_176267_a(var25));
                        BlockPos var28 = var20.func_177972_a(var24);
                        BlockPos var17 = var20.func_177972_a(var25);
                        if (var26 && this.func_150093_a(var1.func_180495_p(var28).func_177230_c())) {
                           var1.func_180501_a(var20, this.func_176223_P().func_177226_a(func_176267_a(var24), true), 2);
                        } else if (var27 && this.func_150093_a(var1.func_180495_p(var17).func_177230_c())) {
                           var1.func_180501_a(var20, this.func_176223_P().func_177226_a(func_176267_a(var25), true), 2);
                        } else if (var26 && var1.func_175623_d(var28) && this.func_150093_a(var1.func_180495_p(var2.func_177972_a(var24)).func_177230_c())) {
                           var1.func_180501_a(var28, this.func_176223_P().func_177226_a(func_176267_a(var18.func_176734_d()), true), 2);
                        } else if (var27 && var1.func_175623_d(var17) && this.func_150093_a(var1.func_180495_p(var2.func_177972_a(var25)).func_177230_c())) {
                           var1.func_180501_a(var17, this.func_176223_P().func_177226_a(func_176267_a(var18.func_176734_d()), true), 2);
                        } else if (this.func_150093_a(var1.func_180495_p(var20.func_177984_a()).func_177230_c())) {
                           var1.func_180501_a(var20, this.func_176223_P(), 2);
                        }
                     } else if (var22.field_149764_J.func_76218_k() && var22.func_149686_d()) {
                        var1.func_180501_a(var2, var3.func_177226_a(func_176267_a(var18), true), 2);
                     }

                  }
               } else {
                  if (var2.func_177956_o() > 1) {
                     var20 = var2.func_177977_b();
                     IBlockState var11 = var1.func_180495_p(var20);
                     Block var12 = var11.func_177230_c();
                     IBlockState var13;
                     Iterator var14;
                     EnumFacing var15;
                     if (var12.field_149764_J == Material.field_151579_a) {
                        var13 = var3;
                        var14 = EnumFacing.Plane.HORIZONTAL.iterator();

                        while(var14.hasNext()) {
                           var15 = (EnumFacing)var14.next();
                           if (var4.nextBoolean()) {
                              var13 = var13.func_177226_a(func_176267_a(var15), false);
                           }
                        }

                        if ((Boolean)var13.func_177229_b(field_176273_b) || (Boolean)var13.func_177229_b(field_176278_M) || (Boolean)var13.func_177229_b(field_176279_N) || (Boolean)var13.func_177229_b(field_176280_O)) {
                           var1.func_180501_a(var20, var13, 2);
                        }
                     } else if (var12 == this) {
                        var13 = var11;
                        var14 = EnumFacing.Plane.HORIZONTAL.iterator();

                        while(var14.hasNext()) {
                           var15 = (EnumFacing)var14.next();
                           PropertyBool var16 = func_176267_a(var15);
                           if (var4.nextBoolean() && (Boolean)var3.func_177229_b(var16)) {
                              var13 = var13.func_177226_a(var16, true);
                           }
                        }

                        if ((Boolean)var13.func_177229_b(field_176273_b) || (Boolean)var13.func_177229_b(field_176278_M) || (Boolean)var13.func_177229_b(field_176279_N) || (Boolean)var13.func_177229_b(field_176280_O)) {
                           var1.func_180501_a(var20, var13, 2);
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var9 = this.func_176223_P().func_177226_a(field_176277_a, false).func_177226_a(field_176273_b, false).func_177226_a(field_176278_M, false).func_177226_a(field_176279_N, false).func_177226_a(field_176280_O, false);
      return var3.func_176740_k().func_176722_c() ? var9.func_177226_a(func_176267_a(var3.func_176734_d()), true) : var9;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
         func_180635_a(var1, var3, new ItemStack(Blocks.field_150395_bd, 1, 0));
      } else {
         super.func_180657_a(var1, var2, var3, var4, var5);
      }

   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176279_N, (var1 & 1) > 0).func_177226_a(field_176280_O, (var1 & 2) > 0).func_177226_a(field_176273_b, (var1 & 4) > 0).func_177226_a(field_176278_M, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      int var2 = 0;
      if ((Boolean)var1.func_177229_b(field_176279_N)) {
         var2 |= 1;
      }

      if ((Boolean)var1.func_177229_b(field_176280_O)) {
         var2 |= 2;
      }

      if ((Boolean)var1.func_177229_b(field_176273_b)) {
         var2 |= 4;
      }

      if ((Boolean)var1.func_177229_b(field_176278_M)) {
         var2 |= 8;
      }

      return var2;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176277_a, field_176273_b, field_176278_M, field_176279_N, field_176280_O});
   }

   public static PropertyBool func_176267_a(EnumFacing var0) {
      switch(var0) {
      case UP:
         return field_176277_a;
      case NORTH:
         return field_176273_b;
      case SOUTH:
         return field_176279_N;
      case EAST:
         return field_176278_M;
      case WEST:
         return field_176280_O;
      default:
         throw new IllegalArgumentException(var0 + " is an invalid choice");
      }
   }

   public static int func_176268_d(IBlockState var0) {
      int var1 = 0;
      PropertyBool[] var2 = field_176274_P;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PropertyBool var5 = var2[var4];
         if ((Boolean)var0.func_177229_b(var5)) {
            ++var1;
         }
      }

      return var1;
   }

   static {
      field_176274_P = new PropertyBool[]{field_176277_a, field_176273_b, field_176279_N, field_176280_O, field_176278_M};
   }
}
