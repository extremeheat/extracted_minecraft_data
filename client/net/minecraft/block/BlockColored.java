package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockColored extends Block {
   public static final PropertyEnum<EnumDyeColor> field_176581_a = PropertyEnum.func_177709_a("color", EnumDyeColor.class);

   public BlockColored(Material var1) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176581_a, EnumDyeColor.WHITE));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_180651_a(IBlockState var1) {
      return ((EnumDyeColor)var1.func_177229_b(field_176581_a)).func_176765_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      EnumDyeColor[] var4 = EnumDyeColor.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumDyeColor var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176765_a()));
      }

   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((EnumDyeColor)var1.func_177229_b(field_176581_a)).func_176768_e();
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176581_a, EnumDyeColor.func_176764_b(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((EnumDyeColor)var1.func_177229_b(field_176581_a)).func_176765_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176581_a});
   }
}
