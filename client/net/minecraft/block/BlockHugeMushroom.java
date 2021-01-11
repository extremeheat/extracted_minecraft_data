package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
   public static final PropertyEnum<BlockHugeMushroom.EnumType> field_176380_a = PropertyEnum.func_177709_a("variant", BlockHugeMushroom.EnumType.class);
   private final Block field_176379_b;

   public BlockHugeMushroom(Material var1, MapColor var2, Block var3) {
      super(var1, var2);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176380_a, BlockHugeMushroom.EnumType.ALL_OUTSIDE));
      this.field_176379_b = var3;
   }

   public int func_149745_a(Random var1) {
      return Math.max(0, var1.nextInt(10) - 7);
   }

   public MapColor func_180659_g(IBlockState var1) {
      switch((BlockHugeMushroom.EnumType)var1.func_177229_b(field_176380_a)) {
      case ALL_STEM:
         return MapColor.field_151659_e;
      case ALL_INSIDE:
         return MapColor.field_151658_d;
      case STEM:
         return MapColor.field_151658_d;
      default:
         return super.func_180659_g(var1);
      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(this.field_176379_b);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(this.field_176379_b);
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P();
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176380_a, BlockHugeMushroom.EnumType.func_176895_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockHugeMushroom.EnumType)var1.func_177229_b(field_176380_a)).func_176896_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176380_a});
   }

   public static enum EnumType implements IStringSerializable {
      NORTH_WEST(1, "north_west"),
      NORTH(2, "north"),
      NORTH_EAST(3, "north_east"),
      WEST(4, "west"),
      CENTER(5, "center"),
      EAST(6, "east"),
      SOUTH_WEST(7, "south_west"),
      SOUTH(8, "south"),
      SOUTH_EAST(9, "south_east"),
      STEM(10, "stem"),
      ALL_INSIDE(0, "all_inside"),
      ALL_OUTSIDE(14, "all_outside"),
      ALL_STEM(15, "all_stem");

      private static final BlockHugeMushroom.EnumType[] field_176905_n = new BlockHugeMushroom.EnumType[16];
      private final int field_176906_o;
      private final String field_176914_p;

      private EnumType(int var3, String var4) {
         this.field_176906_o = var3;
         this.field_176914_p = var4;
      }

      public int func_176896_a() {
         return this.field_176906_o;
      }

      public String toString() {
         return this.field_176914_p;
      }

      public static BlockHugeMushroom.EnumType func_176895_a(int var0) {
         if (var0 < 0 || var0 >= field_176905_n.length) {
            var0 = 0;
         }

         BlockHugeMushroom.EnumType var1 = field_176905_n[var0];
         return var1 == null ? field_176905_n[0] : var1;
      }

      public String func_176610_l() {
         return this.field_176914_p;
      }

      static {
         BlockHugeMushroom.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockHugeMushroom.EnumType var3 = var0[var2];
            field_176905_n[var3.func_176896_a()] = var3;
         }

      }
   }
}
