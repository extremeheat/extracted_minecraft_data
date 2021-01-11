package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static final PropertyDirection field_176426_a;
   public static final PropertyEnum<BlockPistonExtension.EnumPistonType> field_176425_b;

   public BlockPistonMoving() {
      super(Material.field_76233_E);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176426_a, EnumFacing.NORTH).func_177226_a(field_176425_b, BlockPistonExtension.EnumPistonType.DEFAULT));
      this.func_149711_c(-1.0F);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return null;
   }

   public static TileEntity func_176423_a(IBlockState var0, EnumFacing var1, boolean var2, boolean var3) {
      return new TileEntityPiston(var0, var1, var2, var3);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      if (var4 instanceof TileEntityPiston) {
         ((TileEntityPiston)var4).func_145866_f();
      } else {
         super.func_180663_b(var1, var2, var3);
      }

   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return false;
   }

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return false;
   }

   public void func_176206_d(World var1, BlockPos var2, IBlockState var3) {
      BlockPos var4 = var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_176426_a)).func_176734_d());
      IBlockState var5 = var1.func_180495_p(var4);
      if (var5.func_177230_c() instanceof BlockPistonBase && (Boolean)var5.func_177229_b(BlockPistonBase.field_176320_b)) {
         var1.func_175698_g(var4);
      }

   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var1.field_72995_K && var1.func_175625_s(var2) == null) {
         var1.func_175698_g(var2);
         return true;
      } else {
         return false;
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return null;
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.field_72995_K) {
         TileEntityPiston var6 = this.func_176422_e(var1, var2);
         if (var6 != null) {
            IBlockState var7 = var6.func_174927_b();
            var7.func_177230_c().func_176226_b(var1, var2, var7, 0);
         }
      }
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      return null;
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         var1.func_175625_s(var2);
      }

   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      TileEntityPiston var4 = this.func_176422_e(var1, var2);
      if (var4 == null) {
         return null;
      } else {
         float var5 = var4.func_145860_a(0.0F);
         if (var4.func_145868_b()) {
            var5 = 1.0F - var5;
         }

         return this.func_176424_a(var1, var2, var4.func_174927_b(), var5, var4.func_174930_e());
      }
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      TileEntityPiston var3 = this.func_176422_e(var1, var2);
      if (var3 != null) {
         IBlockState var4 = var3.func_174927_b();
         Block var5 = var4.func_177230_c();
         if (var5 == this || var5.func_149688_o() == Material.field_151579_a) {
            return;
         }

         float var6 = var3.func_145860_a(0.0F);
         if (var3.func_145868_b()) {
            var6 = 1.0F - var6;
         }

         var5.func_180654_a(var1, var2);
         if (var5 == Blocks.field_150331_J || var5 == Blocks.field_150320_F) {
            var6 = 0.0F;
         }

         EnumFacing var7 = var3.func_174930_e();
         this.field_149759_B = var5.func_149704_x() - (double)((float)var7.func_82601_c() * var6);
         this.field_149760_C = var5.func_149665_z() - (double)((float)var7.func_96559_d() * var6);
         this.field_149754_D = var5.func_149706_B() - (double)((float)var7.func_82599_e() * var6);
         this.field_149755_E = var5.func_149753_y() - (double)((float)var7.func_82601_c() * var6);
         this.field_149756_F = var5.func_149669_A() - (double)((float)var7.func_96559_d() * var6);
         this.field_149757_G = var5.func_149693_C() - (double)((float)var7.func_82599_e() * var6);
      }

   }

   public AxisAlignedBB func_176424_a(World var1, BlockPos var2, IBlockState var3, float var4, EnumFacing var5) {
      if (var3.func_177230_c() != this && var3.func_177230_c().func_149688_o() != Material.field_151579_a) {
         AxisAlignedBB var6 = var3.func_177230_c().func_180640_a(var1, var2, var3);
         if (var6 == null) {
            return null;
         } else {
            double var7 = var6.field_72340_a;
            double var9 = var6.field_72338_b;
            double var11 = var6.field_72339_c;
            double var13 = var6.field_72336_d;
            double var15 = var6.field_72337_e;
            double var17 = var6.field_72334_f;
            if (var5.func_82601_c() < 0) {
               var7 -= (double)((float)var5.func_82601_c() * var4);
            } else {
               var13 -= (double)((float)var5.func_82601_c() * var4);
            }

            if (var5.func_96559_d() < 0) {
               var9 -= (double)((float)var5.func_96559_d() * var4);
            } else {
               var15 -= (double)((float)var5.func_96559_d() * var4);
            }

            if (var5.func_82599_e() < 0) {
               var11 -= (double)((float)var5.func_82599_e() * var4);
            } else {
               var17 -= (double)((float)var5.func_82599_e() * var4);
            }

            return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
         }
      } else {
         return null;
      }
   }

   private TileEntityPiston func_176422_e(IBlockAccess var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      return var3 instanceof TileEntityPiston ? (TileEntityPiston)var3 : null;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return null;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176426_a, BlockPistonExtension.func_176322_b(var1)).func_177226_a(field_176425_b, (var1 & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176426_a)).func_176745_a();
      if (var1.func_177229_b(field_176425_b) == BlockPistonExtension.EnumPistonType.STICKY) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176426_a, field_176425_b});
   }

   static {
      field_176426_a = BlockPistonExtension.field_176326_a;
      field_176425_b = BlockPistonExtension.field_176325_b;
   }
}
