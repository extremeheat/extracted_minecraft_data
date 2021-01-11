package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {
   public static final PropertyDirection field_176284_a;
   public static final PropertyBool field_176283_b;
   public static final PropertyEnum<BlockTrapDoor.DoorHalf> field_176285_M;

   protected BlockTrapDoor(Material var1) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176284_a, EnumFacing.NORTH).func_177226_a(field_176283_b, false).func_177226_a(field_176285_M, BlockTrapDoor.DoorHalf.BOTTOM));
      float var2 = 0.5F;
      float var3 = 1.0F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return !(Boolean)var1.func_180495_p(var2).func_177229_b(field_176283_b);
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      this.func_180654_a(var1, var2);
      return super.func_180646_a(var1, var2);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_180654_a(var1, var2);
      return super.func_180640_a(var1, var2, var3);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_180693_d(var1.func_180495_p(var2));
   }

   public void func_149683_g() {
      float var1 = 0.1875F;
      this.func_149676_a(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
   }

   public void func_180693_d(IBlockState var1) {
      if (var1.func_177230_c() == this) {
         boolean var2 = var1.func_177229_b(field_176285_M) == BlockTrapDoor.DoorHalf.TOP;
         Boolean var3 = (Boolean)var1.func_177229_b(field_176283_b);
         EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176284_a);
         float var5 = 0.1875F;
         if (var2) {
            this.func_149676_a(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
         } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
         }

         if (var3) {
            if (var4 == EnumFacing.NORTH) {
               this.func_149676_a(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
            }

            if (var4 == EnumFacing.SOUTH) {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
            }

            if (var4 == EnumFacing.WEST) {
               this.func_149676_a(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if (var4 == EnumFacing.EAST) {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
            }
         }

      }
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.field_149764_J == Material.field_151573_f) {
         return true;
      } else {
         var3 = var3.func_177231_a(field_176283_b);
         var1.func_180501_a(var2, var3, 2);
         var1.func_180498_a(var4, (Boolean)var3.func_177229_b(field_176283_b) ? 1003 : 1006, var2, 0);
         return true;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         BlockPos var5 = var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_176284_a)).func_176734_d());
         if (!func_150119_a(var1.func_180495_p(var5).func_177230_c())) {
            var1.func_175698_g(var2);
            this.func_176226_b(var1, var2, var3, 0);
         } else {
            boolean var6 = var1.func_175640_z(var2);
            if (var6 || var4.func_149744_f()) {
               boolean var7 = (Boolean)var3.func_177229_b(field_176283_b);
               if (var7 != var6) {
                  var1.func_180501_a(var2, var3.func_177226_a(field_176283_b, var6), 2);
                  var1.func_180498_a((EntityPlayer)null, var6 ? 1003 : 1006, var2, 0);
               }
            }

         }
      }
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.func_180654_a(var1, var2);
      return super.func_180636_a(var1, var2, var3, var4);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var9 = this.func_176223_P();
      if (var3.func_176740_k().func_176722_c()) {
         var9 = var9.func_177226_a(field_176284_a, var3).func_177226_a(field_176283_b, false);
         var9 = var9.func_177226_a(field_176285_M, var5 > 0.5F ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
      }

      return var9;
   }

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return !var3.func_176740_k().func_176720_b() && func_150119_a(var1.func_180495_p(var2.func_177972_a(var3.func_176734_d())).func_177230_c());
   }

   protected static EnumFacing func_176281_b(int var0) {
      switch(var0 & 3) {
      case 0:
         return EnumFacing.NORTH;
      case 1:
         return EnumFacing.SOUTH;
      case 2:
         return EnumFacing.WEST;
      case 3:
      default:
         return EnumFacing.EAST;
      }
   }

   protected static int func_176282_a(EnumFacing var0) {
      switch(var0) {
      case NORTH:
         return 0;
      case SOUTH:
         return 1;
      case WEST:
         return 2;
      case EAST:
      default:
         return 3;
      }
   }

   private static boolean func_150119_a(Block var0) {
      return var0.field_149764_J.func_76218_k() && var0.func_149686_d() || var0 == Blocks.field_150426_aN || var0 instanceof BlockSlab || var0 instanceof BlockStairs;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176284_a, func_176281_b(var1)).func_177226_a(field_176283_b, (var1 & 4) != 0).func_177226_a(field_176285_M, (var1 & 8) == 0 ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | func_176282_a((EnumFacing)var1.func_177229_b(field_176284_a));
      if ((Boolean)var1.func_177229_b(field_176283_b)) {
         var3 |= 4;
      }

      if (var1.func_177229_b(field_176285_M) == BlockTrapDoor.DoorHalf.TOP) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176284_a, field_176283_b, field_176285_M});
   }

   static {
      field_176284_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176283_b = PropertyBool.func_177716_a("open");
      field_176285_M = PropertyEnum.func_177709_a("half", BlockTrapDoor.DoorHalf.class);
   }

   public static enum DoorHalf implements IStringSerializable {
      TOP("top"),
      BOTTOM("bottom");

      private final String field_176671_c;

      private DoorHalf(String var3) {
         this.field_176671_c = var3;
      }

      public String toString() {
         return this.field_176671_c;
      }

      public String func_176610_l() {
         return this.field_176671_c;
      }
   }
}
