package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockSand extends BlockFalling {
   public static final String[] field_149838_a = new String[]{"default", "red"};
   private static IIcon field_149837_b;
   private static IIcon field_149839_N;

   public BlockSand() {
      super();
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return var2 == 1 ? field_149839_N : field_149837_b;
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      field_149837_b = var1.func_94245_a("sand");
      field_149839_N = var1.func_94245_a("red_sand");
   }

   @Override
   public int func_149692_a(int var1) {
      return var1;
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return var1 == 1 ? MapColor.field_151664_l : MapColor.field_151658_d;
   }
}
