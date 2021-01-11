package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {
   private final boolean field_150171_a;

   public BlockRedstoneLight(boolean var1) {
      super(Material.field_151591_t);
      this.field_150171_a = var1;
      if (var1) {
         this.func_149715_a(1.0F);
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         if (this.field_150171_a && !var1.func_175640_z(var2)) {
            var1.func_180501_a(var2, Blocks.field_150379_bu.func_176223_P(), 2);
         } else if (!this.field_150171_a && var1.func_175640_z(var2)) {
            var1.func_180501_a(var2, Blocks.field_150374_bv.func_176223_P(), 2);
         }

      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         if (this.field_150171_a && !var1.func_175640_z(var2)) {
            var1.func_175684_a(var2, this, 4);
         } else if (!this.field_150171_a && var1.func_175640_z(var2)) {
            var1.func_180501_a(var2, Blocks.field_150374_bv.func_176223_P(), 2);
         }

      }
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         if (this.field_150171_a && !var1.func_175640_z(var2)) {
            var1.func_180501_a(var2, Blocks.field_150379_bu.func_176223_P(), 2);
         }

      }
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150379_bu);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(Blocks.field_150379_bu);
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(Blocks.field_150379_bu);
   }
}
