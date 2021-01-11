package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {
   public static final PropertyDirection field_176520_a;
   public static final PropertyBool field_176519_b;
   public static final PropertyEnum<BlockDoor.EnumHingePosition> field_176521_M;
   public static final PropertyBool field_176522_N;
   public static final PropertyEnum<BlockDoor.EnumDoorHalf> field_176523_O;

   protected BlockDoor(Material var1) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176520_a, EnumFacing.NORTH).func_177226_a(field_176519_b, false).func_177226_a(field_176521_M, BlockDoor.EnumHingePosition.LEFT).func_177226_a(field_176522_N, false).func_177226_a(field_176523_O, BlockDoor.EnumDoorHalf.LOWER));
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a((this.func_149739_a() + ".name").replaceAll("tile", "item"));
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return func_176516_g(func_176515_e(var1, var2));
   }

   public boolean func_149686_d() {
      return false;
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
      this.func_150011_b(func_176515_e(var1, var2));
   }

   private void func_150011_b(int var1) {
      float var2 = 0.1875F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      EnumFacing var3 = func_176511_f(var1);
      boolean var4 = func_176516_g(var1);
      boolean var5 = func_176513_j(var1);
      if (var4) {
         if (var3 == EnumFacing.EAST) {
            if (!var5) {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            } else {
               this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }
         } else if (var3 == EnumFacing.SOUTH) {
            if (!var5) {
               this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
         } else if (var3 == EnumFacing.WEST) {
            if (!var5) {
               this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            } else {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }
         } else if (var3 == EnumFacing.NORTH) {
            if (!var5) {
               this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            } else {
               this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (var3 == EnumFacing.EAST) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
      } else if (var3 == EnumFacing.SOUTH) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
      } else if (var3 == EnumFacing.WEST) {
         this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (var3 == EnumFacing.NORTH) {
         this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
      }

   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.field_149764_J == Material.field_151573_f) {
         return true;
      } else {
         BlockPos var9 = var3.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.LOWER ? var2 : var2.func_177977_b();
         IBlockState var10 = var2.equals(var9) ? var3 : var1.func_180495_p(var9);
         if (var10.func_177230_c() != this) {
            return false;
         } else {
            var3 = var10.func_177231_a(field_176519_b);
            var1.func_180501_a(var9, var3, 2);
            var1.func_175704_b(var9, var2);
            var1.func_180498_a(var4, (Boolean)var3.func_177229_b(field_176519_b) ? 1003 : 1006, var2, 0);
            return true;
         }
      }
   }

   public void func_176512_a(World var1, BlockPos var2, boolean var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      if (var4.func_177230_c() == this) {
         BlockPos var5 = var4.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.LOWER ? var2 : var2.func_177977_b();
         IBlockState var6 = var2 == var5 ? var4 : var1.func_180495_p(var5);
         if (var6.func_177230_c() == this && (Boolean)var6.func_177229_b(field_176519_b) != var3) {
            var1.func_180501_a(var5, var6.func_177226_a(field_176519_b, var3), 2);
            var1.func_175704_b(var5, var2);
            var1.func_180498_a((EntityPlayer)null, var3 ? 1003 : 1006, var2, 0);
         }

      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var3.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.UPPER) {
         BlockPos var5 = var2.func_177977_b();
         IBlockState var6 = var1.func_180495_p(var5);
         if (var6.func_177230_c() != this) {
            var1.func_175698_g(var2);
         } else if (var4 != this) {
            this.func_176204_a(var1, var5, var6, var4);
         }
      } else {
         boolean var9 = false;
         BlockPos var10 = var2.func_177984_a();
         IBlockState var7 = var1.func_180495_p(var10);
         if (var7.func_177230_c() != this) {
            var1.func_175698_g(var2);
            var9 = true;
         }

         if (!World.func_175683_a(var1, var2.func_177977_b())) {
            var1.func_175698_g(var2);
            var9 = true;
            if (var7.func_177230_c() == this) {
               var1.func_175698_g(var10);
            }
         }

         if (var9) {
            if (!var1.field_72995_K) {
               this.func_176226_b(var1, var2, var3, 0);
            }
         } else {
            boolean var8 = var1.func_175640_z(var2) || var1.func_175640_z(var10);
            if ((var8 || var4.func_149744_f()) && var4 != this && var8 != (Boolean)var7.func_177229_b(field_176522_N)) {
               var1.func_180501_a(var10, var7.func_177226_a(field_176522_N, var8), 2);
               if (var8 != (Boolean)var3.func_177229_b(field_176519_b)) {
                  var1.func_180501_a(var2, var3.func_177226_a(field_176519_b, var8), 2);
                  var1.func_175704_b(var2, var2);
                  var1.func_180498_a((EntityPlayer)null, var8 ? 1003 : 1006, var2, 0);
               }
            }
         }
      }

   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return var1.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.UPPER ? null : this.func_176509_j();
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.func_180654_a(var1, var2);
      return super.func_180636_a(var1, var2, var3, var4);
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      if (var2.func_177956_o() >= 255) {
         return false;
      } else {
         return World.func_175683_a(var1, var2.func_177977_b()) && super.func_176196_c(var1, var2) && super.func_176196_c(var1, var2.func_177984_a());
      }
   }

   public int func_149656_h() {
      return 1;
   }

   public static int func_176515_e(IBlockAccess var0, BlockPos var1) {
      IBlockState var2 = var0.func_180495_p(var1);
      int var3 = var2.func_177230_c().func_176201_c(var2);
      boolean var4 = func_176518_i(var3);
      IBlockState var5 = var0.func_180495_p(var1.func_177977_b());
      int var6 = var5.func_177230_c().func_176201_c(var5);
      int var7 = var4 ? var6 : var3;
      IBlockState var8 = var0.func_180495_p(var1.func_177984_a());
      int var9 = var8.func_177230_c().func_176201_c(var8);
      int var10 = var4 ? var3 : var9;
      boolean var11 = (var10 & 1) != 0;
      boolean var12 = (var10 & 2) != 0;
      return func_176510_b(var7) | (var4 ? 8 : 0) | (var11 ? 16 : 0) | (var12 ? 32 : 0);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return this.func_176509_j();
   }

   private Item func_176509_j() {
      if (this == Blocks.field_150454_av) {
         return Items.field_151139_aw;
      } else if (this == Blocks.field_180414_ap) {
         return Items.field_179569_ar;
      } else if (this == Blocks.field_180412_aq) {
         return Items.field_179568_as;
      } else if (this == Blocks.field_180411_ar) {
         return Items.field_179567_at;
      } else if (this == Blocks.field_180410_as) {
         return Items.field_179572_au;
      } else {
         return this == Blocks.field_180409_at ? Items.field_179571_av : Items.field_179570_aq;
      }
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      BlockPos var5 = var2.func_177977_b();
      if (var4.field_71075_bZ.field_75098_d && var3.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.UPPER && var1.func_180495_p(var5).func_177230_c() == this) {
         var1.func_175698_g(var5);
      }

   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      IBlockState var4;
      if (var1.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.LOWER) {
         var4 = var2.func_180495_p(var3.func_177984_a());
         if (var4.func_177230_c() == this) {
            var1 = var1.func_177226_a(field_176521_M, var4.func_177229_b(field_176521_M)).func_177226_a(field_176522_N, var4.func_177229_b(field_176522_N));
         }
      } else {
         var4 = var2.func_180495_p(var3.func_177977_b());
         if (var4.func_177230_c() == this) {
            var1 = var1.func_177226_a(field_176520_a, var4.func_177229_b(field_176520_a)).func_177226_a(field_176519_b, var4.func_177229_b(field_176519_b));
         }
      }

      return var1;
   }

   public IBlockState func_176203_a(int var1) {
      return (var1 & 8) > 0 ? this.func_176223_P().func_177226_a(field_176523_O, BlockDoor.EnumDoorHalf.UPPER).func_177226_a(field_176521_M, (var1 & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT).func_177226_a(field_176522_N, (var1 & 2) > 0) : this.func_176223_P().func_177226_a(field_176523_O, BlockDoor.EnumDoorHalf.LOWER).func_177226_a(field_176520_a, EnumFacing.func_176731_b(var1 & 3).func_176735_f()).func_177226_a(field_176519_b, (var1 & 4) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3;
      if (var1.func_177229_b(field_176523_O) == BlockDoor.EnumDoorHalf.UPPER) {
         var3 = var2 | 8;
         if (var1.func_177229_b(field_176521_M) == BlockDoor.EnumHingePosition.RIGHT) {
            var3 |= 1;
         }

         if ((Boolean)var1.func_177229_b(field_176522_N)) {
            var3 |= 2;
         }
      } else {
         var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176520_a)).func_176746_e().func_176736_b();
         if ((Boolean)var1.func_177229_b(field_176519_b)) {
            var3 |= 4;
         }
      }

      return var3;
   }

   protected static int func_176510_b(int var0) {
      return var0 & 7;
   }

   public static boolean func_176514_f(IBlockAccess var0, BlockPos var1) {
      return func_176516_g(func_176515_e(var0, var1));
   }

   public static EnumFacing func_176517_h(IBlockAccess var0, BlockPos var1) {
      return func_176511_f(func_176515_e(var0, var1));
   }

   public static EnumFacing func_176511_f(int var0) {
      return EnumFacing.func_176731_b(var0 & 3).func_176735_f();
   }

   protected static boolean func_176516_g(int var0) {
      return (var0 & 4) != 0;
   }

   protected static boolean func_176518_i(int var0) {
      return (var0 & 8) != 0;
   }

   protected static boolean func_176513_j(int var0) {
      return (var0 & 16) != 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176523_O, field_176520_a, field_176519_b, field_176521_M, field_176522_N});
   }

   static {
      field_176520_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176519_b = PropertyBool.func_177716_a("open");
      field_176521_M = PropertyEnum.func_177709_a("hinge", BlockDoor.EnumHingePosition.class);
      field_176522_N = PropertyBool.func_177716_a("powered");
      field_176523_O = PropertyEnum.func_177709_a("half", BlockDoor.EnumDoorHalf.class);
   }

   public static enum EnumHingePosition implements IStringSerializable {
      LEFT,
      RIGHT;

      private EnumHingePosition() {
      }

      public String toString() {
         return this.func_176610_l();
      }

      public String func_176610_l() {
         return this == LEFT ? "left" : "right";
      }
   }

   public static enum EnumDoorHalf implements IStringSerializable {
      UPPER,
      LOWER;

      private EnumDoorHalf() {
      }

      public String toString() {
         return this.func_176610_l();
      }

      public String func_176610_l() {
         return this == UPPER ? "upper" : "lower";
      }
   }
}
