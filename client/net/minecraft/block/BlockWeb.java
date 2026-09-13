package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockWeb extends Block {
   public BlockWeb() {
      super(Material.field_151569_G);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      var5.func_70110_aj();
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public int func_149645_b() {
      return 1;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151007_F;
   }

   @Override
   protected boolean func_149700_E() {
      return true;
   }
}
