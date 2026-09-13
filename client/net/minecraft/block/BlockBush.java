package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockBush extends Block {
   protected BlockBush(Material var1) {
      super(var1);
      this.func_149675_a(true);
      float var2 = 0.2F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var2 * 3.0F, 0.5F + var2);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   protected BlockBush() {
      this(Material.field_151585_k);
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return super.func_149742_c(var1, var2, var3, var4) && this.func_149854_a(var1.func_147439_a(var2, var3 - 1, var4));
   }

   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150349_c || var1 == Blocks.field_150346_d || var1 == Blocks.field_150458_ak;
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      super.func_149695_a(var1, var2, var3, var4, var5);
      this.func_149855_e(var1, var2, var3, var4);
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      this.func_149855_e(var1, var2, var3, var4);
   }

   protected void func_149855_e(World var1, int var2, int var3, int var4) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147465_d(var2, var3, var4, func_149729_e(0), 0, 2);
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      return this.func_149854_a(var1.func_147439_a(var2, var3 - 1, var4));
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 1;
   }
}
