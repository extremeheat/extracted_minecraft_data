package net.minecraft.block;

import java.util.Iterator;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLever extends Block {
   public static final PropertyEnum<BlockLever.EnumOrientation> field_176360_a = PropertyEnum.func_177709_a("facing", BlockLever.EnumOrientation.class);
   public static final PropertyBool field_176359_b = PropertyBool.func_177716_a("powered");

   protected BlockLever() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176360_a, BlockLever.EnumOrientation.NORTH).func_177226_a(field_176359_b, false));
      this.func_149647_a(CreativeTabs.field_78028_d);
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

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return func_181090_a(var1, var2, var3.func_176734_d());
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         if (func_181090_a(var1, var2, var6)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean func_181090_a(World var0, BlockPos var1, EnumFacing var2) {
      return BlockButton.func_181088_a(var0, var1, var2);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      IBlockState var9 = this.func_176223_P().func_177226_a(field_176359_b, false);
      if (func_181090_a(var1, var2, var3.func_176734_d())) {
         return var9.func_177226_a(field_176360_a, BlockLever.EnumOrientation.func_176856_a(var3, var8.func_174811_aO()));
      } else {
         Iterator var10 = EnumFacing.Plane.HORIZONTAL.iterator();

         EnumFacing var11;
         do {
            if (!var10.hasNext()) {
               if (World.func_175683_a(var1, var2.func_177977_b())) {
                  return var9.func_177226_a(field_176360_a, BlockLever.EnumOrientation.func_176856_a(EnumFacing.UP, var8.func_174811_aO()));
               }

               return var9;
            }

            var11 = (EnumFacing)var10.next();
         } while(var11 == var3 || !func_181090_a(var1, var2, var11.func_176734_d()));

         return var9.func_177226_a(field_176360_a, BlockLever.EnumOrientation.func_176856_a(var11, var8.func_174811_aO()));
      }
   }

   public static int func_176357_a(EnumFacing var0) {
      switch(var0) {
      case DOWN:
         return 0;
      case UP:
         return 5;
      case NORTH:
         return 4;
      case SOUTH:
         return 3;
      case WEST:
         return 2;
      case EAST:
         return 1;
      default:
         return -1;
      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (this.func_181091_e(var1, var2, var3) && !func_181090_a(var1, var2, ((BlockLever.EnumOrientation)var3.func_177229_b(field_176360_a)).func_176852_c().func_176734_d())) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

   }

   private boolean func_181091_e(World var1, BlockPos var2, IBlockState var3) {
      if (this.func_176196_c(var1, var2)) {
         return true;
      } else {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
         return false;
      }
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      float var3 = 0.1875F;
      switch((BlockLever.EnumOrientation)var1.func_180495_p(var2).func_177229_b(field_176360_a)) {
      case EAST:
         this.func_149676_a(0.0F, 0.2F, 0.5F - var3, var3 * 2.0F, 0.8F, 0.5F + var3);
         break;
      case WEST:
         this.func_149676_a(1.0F - var3 * 2.0F, 0.2F, 0.5F - var3, 1.0F, 0.8F, 0.5F + var3);
         break;
      case SOUTH:
         this.func_149676_a(0.5F - var3, 0.2F, 0.0F, 0.5F + var3, 0.8F, var3 * 2.0F);
         break;
      case NORTH:
         this.func_149676_a(0.5F - var3, 0.2F, 1.0F - var3 * 2.0F, 0.5F + var3, 0.8F, 1.0F);
         break;
      case UP_Z:
      case UP_X:
         var3 = 0.25F;
         this.func_149676_a(0.5F - var3, 0.0F, 0.5F - var3, 0.5F + var3, 0.6F, 0.5F + var3);
         break;
      case DOWN_X:
      case DOWN_Z:
         var3 = 0.25F;
         this.func_149676_a(0.5F - var3, 0.4F, 0.5F - var3, 0.5F + var3, 1.0F, 0.5F + var3);
      }

   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         var3 = var3.func_177231_a(field_176359_b);
         var1.func_180501_a(var2, var3, 3);
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, (Boolean)var3.func_177229_b(field_176359_b) ? 0.6F : 0.5F);
         var1.func_175685_c(var2, this);
         EnumFacing var9 = ((BlockLever.EnumOrientation)var3.func_177229_b(field_176360_a)).func_176852_c();
         var1.func_175685_c(var2.func_177972_a(var9.func_176734_d()), this);
         return true;
      }
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      if ((Boolean)var3.func_177229_b(field_176359_b)) {
         var1.func_175685_c(var2, this);
         EnumFacing var4 = ((BlockLever.EnumOrientation)var3.func_177229_b(field_176360_a)).func_176852_c();
         var1.func_175685_c(var2.func_177972_a(var4.func_176734_d()), this);
      }

      super.func_180663_b(var1, var2, var3);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return (Boolean)var3.func_177229_b(field_176359_b) ? 15 : 0;
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      if (!(Boolean)var3.func_177229_b(field_176359_b)) {
         return 0;
      } else {
         return ((BlockLever.EnumOrientation)var3.func_177229_b(field_176360_a)).func_176852_c() == var4 ? 15 : 0;
      }
   }

   public boolean func_149744_f() {
      return true;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176360_a, BlockLever.EnumOrientation.func_176853_a(var1 & 7)).func_177226_a(field_176359_b, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockLever.EnumOrientation)var1.func_177229_b(field_176360_a)).func_176855_a();
      if ((Boolean)var1.func_177229_b(field_176359_b)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176360_a, field_176359_b});
   }

   public static enum EnumOrientation implements IStringSerializable {
      DOWN_X(0, "down_x", EnumFacing.DOWN),
      EAST(1, "east", EnumFacing.EAST),
      WEST(2, "west", EnumFacing.WEST),
      SOUTH(3, "south", EnumFacing.SOUTH),
      NORTH(4, "north", EnumFacing.NORTH),
      UP_Z(5, "up_z", EnumFacing.UP),
      UP_X(6, "up_x", EnumFacing.UP),
      DOWN_Z(7, "down_z", EnumFacing.DOWN);

      private static final BlockLever.EnumOrientation[] field_176869_i = new BlockLever.EnumOrientation[values().length];
      private final int field_176866_j;
      private final String field_176867_k;
      private final EnumFacing field_176864_l;

      private EnumOrientation(int var3, String var4, EnumFacing var5) {
         this.field_176866_j = var3;
         this.field_176867_k = var4;
         this.field_176864_l = var5;
      }

      public int func_176855_a() {
         return this.field_176866_j;
      }

      public EnumFacing func_176852_c() {
         return this.field_176864_l;
      }

      public String toString() {
         return this.field_176867_k;
      }

      public static BlockLever.EnumOrientation func_176853_a(int var0) {
         if (var0 < 0 || var0 >= field_176869_i.length) {
            var0 = 0;
         }

         return field_176869_i[var0];
      }

      public static BlockLever.EnumOrientation func_176856_a(EnumFacing var0, EnumFacing var1) {
         switch(var0) {
         case DOWN:
            switch(var1.func_176740_k()) {
            case X:
               return DOWN_X;
            case Z:
               return DOWN_Z;
            default:
               throw new IllegalArgumentException("Invalid entityFacing " + var1 + " for facing " + var0);
            }
         case UP:
            switch(var1.func_176740_k()) {
            case X:
               return UP_X;
            case Z:
               return UP_Z;
            default:
               throw new IllegalArgumentException("Invalid entityFacing " + var1 + " for facing " + var0);
            }
         case NORTH:
            return NORTH;
         case SOUTH:
            return SOUTH;
         case WEST:
            return WEST;
         case EAST:
            return EAST;
         default:
            throw new IllegalArgumentException("Invalid facing: " + var0);
         }
      }

      public String func_176610_l() {
         return this.field_176867_k;
      }

      static {
         BlockLever.EnumOrientation[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockLever.EnumOrientation var3 = var0[var2];
            field_176869_i[var3.func_176855_a()] = var3;
         }

      }
   }
}
