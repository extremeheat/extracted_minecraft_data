package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class BlockPlanks extends Block {
   public static final PropertyEnum<BlockPlanks.EnumType> field_176383_a = PropertyEnum.func_177709_a("variant", BlockPlanks.EnumType.class);

   public BlockPlanks() {
      super(Material.field_151575_d);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176383_a, BlockPlanks.EnumType.OAK));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176383_a)).func_176839_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockPlanks.EnumType[] var4 = BlockPlanks.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockPlanks.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176839_a()));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176383_a, BlockPlanks.EnumType.func_176837_a(var1));
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176383_a)).func_181070_c();
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176383_a)).func_176839_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176383_a});
   }

   public static enum EnumType implements IStringSerializable {
      OAK(0, "oak", MapColor.field_151663_o),
      SPRUCE(1, "spruce", MapColor.field_151654_J),
      BIRCH(2, "birch", MapColor.field_151658_d),
      JUNGLE(3, "jungle", MapColor.field_151664_l),
      ACACIA(4, "acacia", MapColor.field_151676_q),
      DARK_OAK(5, "dark_oak", "big_oak", MapColor.field_151650_B);

      private static final BlockPlanks.EnumType[] field_176842_g = new BlockPlanks.EnumType[values().length];
      private final int field_176850_h;
      private final String field_176851_i;
      private final String field_176848_j;
      private final MapColor field_181071_k;

      private EnumType(int var3, String var4, MapColor var5) {
         this(var3, var4, var4, var5);
      }

      private EnumType(int var3, String var4, String var5, MapColor var6) {
         this.field_176850_h = var3;
         this.field_176851_i = var4;
         this.field_176848_j = var5;
         this.field_181071_k = var6;
      }

      public int func_176839_a() {
         return this.field_176850_h;
      }

      public MapColor func_181070_c() {
         return this.field_181071_k;
      }

      public String toString() {
         return this.field_176851_i;
      }

      public static BlockPlanks.EnumType func_176837_a(int var0) {
         if (var0 < 0 || var0 >= field_176842_g.length) {
            var0 = 0;
         }

         return field_176842_g[var0];
      }

      public String func_176610_l() {
         return this.field_176851_i;
      }

      public String func_176840_c() {
         return this.field_176848_j;
      }

      static {
         BlockPlanks.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockPlanks.EnumType var3 = var0[var2];
            field_176842_g[var3.func_176839_a()] = var3;
         }

      }
   }
}
