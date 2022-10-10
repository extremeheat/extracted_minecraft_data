package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemShears extends Item {
   public ItemShears(Item.Properties var1) {
      super(var1);
   }

   public boolean func_179218_a(ItemStack var1, World var2, IBlockState var3, BlockPos var4, EntityLivingBase var5) {
      if (!var2.field_72995_K) {
         var1.func_77972_a(1, var5);
      }

      Block var6 = var3.func_177230_c();
      return !var3.func_203425_a(BlockTags.field_206952_E) && var6 != Blocks.field_196553_aF && var6 != Blocks.field_150349_c && var6 != Blocks.field_196554_aH && var6 != Blocks.field_196555_aI && var6 != Blocks.field_150395_bd && var6 != Blocks.field_150473_bD && !var6.func_203417_a(BlockTags.field_199897_a) ? super.func_179218_a(var1, var2, var3, var4, var5) : true;
   }

   public boolean func_150897_b(IBlockState var1) {
      Block var2 = var1.func_177230_c();
      return var2 == Blocks.field_196553_aF || var2 == Blocks.field_150488_af || var2 == Blocks.field_150473_bD;
   }

   public float func_150893_a(ItemStack var1, IBlockState var2) {
      Block var3 = var2.func_177230_c();
      if (var3 != Blocks.field_196553_aF && !var2.func_203425_a(BlockTags.field_206952_E)) {
         return var3.func_203417_a(BlockTags.field_199897_a) ? 5.0F : super.func_150893_a(var1, var2);
      } else {
         return 15.0F;
      }
   }
}
