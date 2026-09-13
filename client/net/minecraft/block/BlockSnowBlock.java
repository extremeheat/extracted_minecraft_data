package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BlockSnowBlock extends Block {
   protected BlockSnowBlock() {
      super(Material.field_151596_z);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151126_ay;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 4;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_72972_b(EnumSkyBlock.Block, var2, var3, var4) > 11) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
      }
   }
}
