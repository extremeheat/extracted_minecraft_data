package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class BlockDeadBush extends BlockBush {
   protected BlockDeadBush() {
      super(Material.field_151582_l);
      float var1 = 0.4F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.8F, 0.5F + var1);
   }

   @Override
   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150354_m || var1 == Blocks.field_150405_ch || var1 == Blocks.field_150406_ce || var1 == Blocks.field_150346_d;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
         this.func_149642_a(var1, var3, var4, var5, new ItemStack(Blocks.field_150330_I, 1, var6));
      } else {
         super.func_149636_a(var1, var2, var3, var4, var5, var6);
      }
   }
}
