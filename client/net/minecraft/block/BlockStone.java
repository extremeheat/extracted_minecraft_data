package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;

public class BlockStone extends Block {
   public static final PropertyEnum<BlockStone.EnumType> field_176247_a = PropertyEnum.func_177709_a("variant", BlockStone.EnumType.class);

   public BlockStone() {
      super(Material.field_151576_e);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176247_a, BlockStone.EnumType.STONE));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + "." + BlockStone.EnumType.STONE.func_176644_c() + ".name");
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockStone.EnumType)var1.func_177229_b(field_176247_a)).func_181072_c();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return var1.func_177229_b(field_176247_a) == BlockStone.EnumType.STONE ? Item.func_150898_a(Blocks.field_150347_e) : Item.func_150898_a(Blocks.field_150348_b);
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockStone.EnumType)var1.func_177229_b(field_176247_a)).func_176642_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockStone.EnumType[] var4 = BlockStone.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockStone.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176642_a()));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176247_a, BlockStone.EnumType.func_176643_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockStone.EnumType)var1.func_177229_b(field_176247_a)).func_176642_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176247_a});
   }

   public static enum EnumType implements IStringSerializable {
      STONE(0, MapColor.field_151665_m, "stone"),
      GRANITE(1, MapColor.field_151664_l, "granite"),
      GRANITE_SMOOTH(2, MapColor.field_151664_l, "smooth_granite", "graniteSmooth"),
      DIORITE(3, MapColor.field_151677_p, "diorite"),
      DIORITE_SMOOTH(4, MapColor.field_151677_p, "smooth_diorite", "dioriteSmooth"),
      ANDESITE(5, MapColor.field_151665_m, "andesite"),
      ANDESITE_SMOOTH(6, MapColor.field_151665_m, "smooth_andesite", "andesiteSmooth");

      private static final BlockStone.EnumType[] field_176655_h = new BlockStone.EnumType[values().length];
      private final int field_176656_i;
      private final String field_176653_j;
      private final String field_176654_k;
      private final MapColor field_181073_l;

      private EnumType(int var3, MapColor var4, String var5) {
         this(var3, var4, var5, var5);
      }

      private EnumType(int var3, MapColor var4, String var5, String var6) {
         this.field_176656_i = var3;
         this.field_176653_j = var5;
         this.field_176654_k = var6;
         this.field_181073_l = var4;
      }

      public int func_176642_a() {
         return this.field_176656_i;
      }

      public MapColor func_181072_c() {
         return this.field_181073_l;
      }

      public String toString() {
         return this.field_176653_j;
      }

      public static BlockStone.EnumType func_176643_a(int var0) {
         if (var0 < 0 || var0 >= field_176655_h.length) {
            var0 = 0;
         }

         return field_176655_h[var0];
      }

      public String func_176610_l() {
         return this.field_176653_j;
      }

      public String func_176644_c() {
         return this.field_176654_k;
      }

      static {
         BlockStone.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockStone.EnumType var3 = var0[var2];
            field_176655_h[var3.func_176642_a()] = var3;
         }

      }
   }
}
