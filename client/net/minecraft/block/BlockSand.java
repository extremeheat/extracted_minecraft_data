package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockSand extends BlockFalling {
   public static final PropertyEnum<BlockSand.EnumType> field_176504_a = PropertyEnum.func_177709_a("variant", BlockSand.EnumType.class);

   public BlockSand() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176504_a, BlockSand.EnumType.SAND));
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockSand.EnumType)var1.func_177229_b(field_176504_a)).func_176688_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockSand.EnumType[] var4 = BlockSand.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockSand.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176688_a()));
      }

   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockSand.EnumType)var1.func_177229_b(field_176504_a)).func_176687_c();
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176504_a, BlockSand.EnumType.func_176686_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockSand.EnumType)var1.func_177229_b(field_176504_a)).func_176688_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176504_a});
   }

   public static enum EnumType implements IStringSerializable {
      SAND(0, "sand", "default", MapColor.field_151658_d),
      RED_SAND(1, "red_sand", "red", MapColor.field_151676_q);

      private static final BlockSand.EnumType[] field_176695_c = new BlockSand.EnumType[values().length];
      private final int field_176692_d;
      private final String field_176693_e;
      private final MapColor field_176690_f;
      private final String field_176691_g;

      private EnumType(int var3, String var4, String var5, MapColor var6) {
         this.field_176692_d = var3;
         this.field_176693_e = var4;
         this.field_176690_f = var6;
         this.field_176691_g = var5;
      }

      public int func_176688_a() {
         return this.field_176692_d;
      }

      public String toString() {
         return this.field_176693_e;
      }

      public MapColor func_176687_c() {
         return this.field_176690_f;
      }

      public static BlockSand.EnumType func_176686_a(int var0) {
         if (var0 < 0 || var0 >= field_176695_c.length) {
            var0 = 0;
         }

         return field_176695_c[var0];
      }

      public String func_176610_l() {
         return this.field_176693_e;
      }

      public String func_176685_d() {
         return this.field_176691_g;
      }

      static {
         BlockSand.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockSand.EnumType var3 = var0[var2];
            field_176695_c[var3.func_176688_a()] = var3;
         }

      }
   }
}
