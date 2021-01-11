package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops {
   public BlockPotato() {
      super();
   }

   protected Item func_149866_i() {
      return Items.field_151174_bG;
   }

   protected Item func_149865_P() {
      return Items.field_151174_bG;
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      super.func_180653_a(var1, var2, var3, var4, var5);
      if (!var1.field_72995_K) {
         if ((Integer)var3.func_177229_b(field_176488_a) >= 7 && var1.field_73012_v.nextInt(50) == 0) {
            func_180635_a(var1, var2, new ItemStack(Items.field_151170_bI));
         }

      }
   }
}
