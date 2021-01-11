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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class BlockWoodSlab extends BlockSlab {
   public static final PropertyEnum<BlockPlanks.EnumType> field_176557_b = PropertyEnum.func_177709_a("variant", BlockPlanks.EnumType.class);

   public BlockWoodSlab() {
      super(Material.field_151575_d);
      IBlockState var1 = this.field_176227_L.func_177621_b();
      if (!this.func_176552_j()) {
         var1 = var1.func_177226_a(field_176554_a, BlockSlab.EnumBlockHalf.BOTTOM);
      }

      this.func_180632_j(var1.func_177226_a(field_176557_b, BlockPlanks.EnumType.OAK));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public MapColor func_180659_g(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176557_b)).func_181070_c();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150376_bx);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150376_bx);
   }

   public String func_150002_b(int var1) {
      return super.func_149739_a() + "." + BlockPlanks.EnumType.func_176837_a(var1).func_176840_c();
   }

   public IProperty<?> func_176551_l() {
      return field_176557_b;
   }

   public Object func_176553_a(ItemStack var1) {
      return BlockPlanks.EnumType.func_176837_a(var1.func_77960_j() & 7);
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      if (var1 != Item.func_150898_a(Blocks.field_150373_bw)) {
         BlockPlanks.EnumType[] var4 = BlockPlanks.EnumType.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockPlanks.EnumType var7 = var4[var6];
            var3.add(new ItemStack(var1, 1, var7.func_176839_a()));
         }

      }
   }

   public IBlockState func_176203_a(int var1) {
      IBlockState var2 = this.func_176223_P().func_177226_a(field_176557_b, BlockPlanks.EnumType.func_176837_a(var1 & 7));
      if (!this.func_176552_j()) {
         var2 = var2.func_177226_a(field_176554_a, (var1 & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
      }

      return var2;
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockPlanks.EnumType)var1.func_177229_b(field_176557_b)).func_176839_a();
      if (!this.func_176552_j() && var1.func_177229_b(field_176554_a) == BlockSlab.EnumBlockHalf.TOP) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return this.func_176552_j() ? new BlockState(this, new IProperty[]{field_176557_b}) : new BlockState(this, new IProperty[]{field_176554_a, field_176557_b});
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176557_b)).func_176839_a();
   }
}
