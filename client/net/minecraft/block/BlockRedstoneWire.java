package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> field_176348_a = PropertyEnum.func_177709_a("north", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> field_176347_b = PropertyEnum.func_177709_a("east", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> field_176349_M = PropertyEnum.func_177709_a("south", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> field_176350_N = PropertyEnum.func_177709_a("west", BlockRedstoneWire.EnumAttachPosition.class);
   public static final PropertyInteger field_176351_O = PropertyInteger.func_177719_a("power", 0, 15);
   private boolean field_150181_a = true;
   private final Set<BlockPos> field_150179_b = Sets.newHashSet();

   public BlockRedstoneWire() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176348_a, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(field_176347_b, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(field_176349_M, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(field_176350_N, BlockRedstoneWire.EnumAttachPosition.NONE).func_177226_a(field_176351_O, 0));
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      var1 = var1.func_177226_a(field_176350_N, this.func_176341_c(var2, var3, EnumFacing.WEST));
      var1 = var1.func_177226_a(field_176347_b, this.func_176341_c(var2, var3, EnumFacing.EAST));
      var1 = var1.func_177226_a(field_176348_a, this.func_176341_c(var2, var3, EnumFacing.NORTH));
      var1 = var1.func_177226_a(field_176349_M, this.func_176341_c(var2, var3, EnumFacing.SOUTH));
      return var1;
   }

   private BlockRedstoneWire.EnumAttachPosition func_176341_c(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      BlockPos var4 = var2.func_177972_a(var3);
      Block var5 = var1.func_180495_p(var2.func_177972_a(var3)).func_177230_c();
      if (func_176343_a(var1.func_180495_p(var4), var3) || !var5.func_149637_q() && func_176346_d(var1.func_180495_p(var4.func_177977_b()))) {
         return BlockRedstoneWire.EnumAttachPosition.SIDE;
      } else {
         Block var6 = var1.func_180495_p(var2.func_177984_a()).func_177230_c();
         return !var6.func_149637_q() && var5.func_149637_q() && func_176346_d(var1.func_180495_p(var4.func_177984_a())) ? BlockRedstoneWire.EnumAttachPosition.UP : BlockRedstoneWire.EnumAttachPosition.NONE;
      }
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

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      return var4.func_177230_c() != this ? super.func_180662_a(var1, var2, var3) : this.func_176337_b((Integer)var4.func_177229_b(field_176351_O));
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return World.func_175683_a(var1, var2.func_177977_b()) || var1.func_180495_p(var2.func_177977_b()).func_177230_c() == Blocks.field_150426_aN;
   }

   private IBlockState func_176338_e(World var1, BlockPos var2, IBlockState var3) {
      var3 = this.func_176345_a(var1, var2, var2, var3);
      ArrayList var4 = Lists.newArrayList(this.field_150179_b);
      this.field_150179_b.clear();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         var1.func_175685_c(var6, this);
      }

      return var3;
   }

   private IBlockState func_176345_a(World var1, BlockPos var2, BlockPos var3, IBlockState var4) {
      IBlockState var5 = var4;
      int var6 = (Integer)var4.func_177229_b(field_176351_O);
      byte var7 = 0;
      int var14 = this.func_176342_a(var1, var3, var7);
      this.field_150181_a = false;
      int var8 = var1.func_175687_A(var2);
      this.field_150181_a = true;
      if (var8 > 0 && var8 > var14 - 1) {
         var14 = var8;
      }

      int var9 = 0;
      Iterator var10 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(true) {
         while(var10.hasNext()) {
            EnumFacing var11 = (EnumFacing)var10.next();
            BlockPos var12 = var2.func_177972_a(var11);
            boolean var13 = var12.func_177958_n() != var3.func_177958_n() || var12.func_177952_p() != var3.func_177952_p();
            if (var13) {
               var9 = this.func_176342_a(var1, var12, var9);
            }

            if (var1.func_180495_p(var12).func_177230_c().func_149721_r() && !var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149721_r()) {
               if (var13 && var2.func_177956_o() >= var3.func_177956_o()) {
                  var9 = this.func_176342_a(var1, var12.func_177984_a(), var9);
               }
            } else if (!var1.func_180495_p(var12).func_177230_c().func_149721_r() && var13 && var2.func_177956_o() <= var3.func_177956_o()) {
               var9 = this.func_176342_a(var1, var12.func_177977_b(), var9);
            }
         }

         if (var9 > var14) {
            var14 = var9 - 1;
         } else if (var14 > 0) {
            --var14;
         } else {
            var14 = 0;
         }

         if (var8 > var14 - 1) {
            var14 = var8;
         }

         if (var6 != var14) {
            var4 = var4.func_177226_a(field_176351_O, var14);
            if (var1.func_180495_p(var2) == var5) {
               var1.func_180501_a(var2, var4, 2);
            }

            this.field_150179_b.add(var2);
            EnumFacing[] var15 = EnumFacing.values();
            int var16 = var15.length;

            for(int var17 = 0; var17 < var16; ++var17) {
               EnumFacing var18 = var15[var17];
               this.field_150179_b.add(var2.func_177972_a(var18));
            }
         }

         return var4;
      }
   }

   private void func_176344_d(World var1, BlockPos var2) {
      if (var1.func_180495_p(var2).func_177230_c() == this) {
         var1.func_175685_c(var2, this);
         EnumFacing[] var3 = EnumFacing.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing var6 = var3[var5];
            var1.func_175685_c(var2.func_177972_a(var6), this);
         }

      }
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         this.func_176338_e(var1, var2, var3);
         Iterator var4 = EnumFacing.Plane.VERTICAL.iterator();

         EnumFacing var5;
         while(var4.hasNext()) {
            var5 = (EnumFacing)var4.next();
            var1.func_175685_c(var2.func_177972_a(var5), this);
         }

         var4 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var4.hasNext()) {
            var5 = (EnumFacing)var4.next();
            this.func_176344_d(var1, var2.func_177972_a(var5));
         }

         var4 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var4.hasNext()) {
            var5 = (EnumFacing)var4.next();
            BlockPos var6 = var2.func_177972_a(var5);
            if (var1.func_180495_p(var6).func_177230_c().func_149721_r()) {
               this.func_176344_d(var1, var6.func_177984_a());
            } else {
               this.func_176344_d(var1, var6.func_177977_b());
            }
         }

      }
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      super.func_180663_b(var1, var2, var3);
      if (!var1.field_72995_K) {
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            var1.func_175685_c(var2.func_177972_a(var7), this);
         }

         this.func_176338_e(var1, var2, var3);
         Iterator var8 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var9;
         while(var8.hasNext()) {
            var9 = (EnumFacing)var8.next();
            this.func_176344_d(var1, var2.func_177972_a(var9));
         }

         var8 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var8.hasNext()) {
            var9 = (EnumFacing)var8.next();
            BlockPos var10 = var2.func_177972_a(var9);
            if (var1.func_180495_p(var10).func_177230_c().func_149721_r()) {
               this.func_176344_d(var1, var10.func_177984_a());
            } else {
               this.func_176344_d(var1, var10.func_177977_b());
            }
         }

      }
   }

   private int func_176342_a(World var1, BlockPos var2, int var3) {
      if (var1.func_180495_p(var2).func_177230_c() != this) {
         return var3;
      } else {
         int var4 = (Integer)var1.func_180495_p(var2).func_177229_b(field_176351_O);
         return var4 > var3 ? var4 : var3;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         if (this.func_176196_c(var1, var2)) {
            this.func_176338_e(var1, var2, var3);
         } else {
            this.func_176226_b(var1, var2, var3, 0);
            var1.func_175698_g(var2);
         }

      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151137_ax;
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return !this.field_150181_a ? 0 : this.func_180656_a(var1, var2, var3, var4);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!this.field_150181_a) {
         return 0;
      } else {
         int var5 = (Integer)var3.func_177229_b(field_176351_O);
         if (var5 == 0) {
            return 0;
         } else if (var4 == EnumFacing.UP) {
            return var5;
         } else {
            EnumSet var6 = EnumSet.noneOf(EnumFacing.class);
            Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               EnumFacing var8 = (EnumFacing)var7.next();
               if (this.func_176339_d(var1, var2, var8)) {
                  var6.add(var8);
               }
            }

            if (var4.func_176740_k().func_176722_c() && var6.isEmpty()) {
               return var5;
            } else if (var6.contains(var4) && !var6.contains(var4.func_176735_f()) && !var6.contains(var4.func_176746_e())) {
               return var5;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean func_176339_d(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      BlockPos var4 = var2.func_177972_a(var3);
      IBlockState var5 = var1.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      boolean var7 = var6.func_149721_r();
      boolean var8 = var1.func_180495_p(var2.func_177984_a()).func_177230_c().func_149721_r();
      if (!var8 && var7 && func_176340_e(var1, var4.func_177984_a())) {
         return true;
      } else if (func_176343_a(var5, var3)) {
         return true;
      } else if (var6 == Blocks.field_150416_aS && var5.func_177229_b(BlockRedstoneDiode.field_176387_N) == var3) {
         return true;
      } else {
         return !var7 && func_176340_e(var1, var4.func_177977_b());
      }
   }

   protected static boolean func_176340_e(IBlockAccess var0, BlockPos var1) {
      return func_176346_d(var0.func_180495_p(var1));
   }

   protected static boolean func_176346_d(IBlockState var0) {
      return func_176343_a(var0, (EnumFacing)null);
   }

   protected static boolean func_176343_a(IBlockState var0, EnumFacing var1) {
      Block var2 = var0.func_177230_c();
      if (var2 == Blocks.field_150488_af) {
         return true;
      } else if (Blocks.field_150413_aR.func_149907_e(var2)) {
         EnumFacing var3 = (EnumFacing)var0.func_177229_b(BlockRedstoneRepeater.field_176387_N);
         return var3 == var1 || var3.func_176734_d() == var1;
      } else {
         return var2.func_149744_f() && var1 != null;
      }
   }

   public boolean func_149744_f() {
      return this.field_150181_a;
   }

   private int func_176337_b(int var1) {
      float var2 = (float)var1 / 15.0F;
      float var3 = var2 * 0.6F + 0.4F;
      if (var1 == 0) {
         var3 = 0.3F;
      }

      float var4 = var2 * var2 * 0.7F - 0.5F;
      float var5 = var2 * var2 * 0.6F - 0.7F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      if (var5 < 0.0F) {
         var5 = 0.0F;
      }

      int var6 = MathHelper.func_76125_a((int)(var3 * 255.0F), 0, 255);
      int var7 = MathHelper.func_76125_a((int)(var4 * 255.0F), 0, 255);
      int var8 = MathHelper.func_76125_a((int)(var5 * 255.0F), 0, 255);
      return -16777216 | var6 << 16 | var7 << 8 | var8;
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      int var5 = (Integer)var3.func_177229_b(field_176351_O);
      if (var5 != 0) {
         double var6 = (double)var2.func_177958_n() + 0.5D + ((double)var4.nextFloat() - 0.5D) * 0.2D;
         double var8 = (double)((float)var2.func_177956_o() + 0.0625F);
         double var10 = (double)var2.func_177952_p() + 0.5D + ((double)var4.nextFloat() - 0.5D) * 0.2D;
         float var12 = (float)var5 / 15.0F;
         float var13 = var12 * 0.6F + 0.4F;
         float var14 = Math.max(0.0F, var12 * var12 * 0.7F - 0.5F);
         float var15 = Math.max(0.0F, var12 * var12 * 0.6F - 0.7F);
         var1.func_175688_a(EnumParticleTypes.REDSTONE, var6, var8, var10, (double)var13, (double)var14, (double)var15);
      }
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151137_ax;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176351_O, var1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176351_O);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176348_a, field_176347_b, field_176349_M, field_176350_N, field_176351_O});
   }

   static enum EnumAttachPosition implements IStringSerializable {
      UP("up"),
      SIDE("side"),
      NONE("none");

      private final String field_176820_d;

      private EnumAttachPosition(String var3) {
         this.field_176820_d = var3;
      }

      public String toString() {
         return this.func_176610_l();
      }

      public String func_176610_l() {
         return this.field_176820_d;
      }
   }
}
