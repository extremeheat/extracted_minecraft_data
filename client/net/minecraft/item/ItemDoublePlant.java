package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerGrass;

public class ItemDoublePlant extends ItemMultiTexture {
   public ItemDoublePlant(Block var1, BlockDoublePlant var2, String[] var3) {
      super(var1, var2, var3);
   }

   @Override
   public IIcon func_77617_a(int var1) {
      return BlockDoublePlant.func_149890_d(var1) == 0
         ? ((BlockDoublePlant)this.field_150941_b).field_149891_b[0]
         : ((BlockDoublePlant)this.field_150941_b).func_149888_a(true, var1);
   }

   @Override
   public int func_82790_a(ItemStack var1, int var2) {
      int var3 = BlockDoublePlant.func_149890_d(var1.func_77960_j());
      return var3 != 2 && var3 != 3 ? super.func_82790_a(var1, var2) : ColorizerGrass.func_77480_a(0.5, 1.0);
   }
}
