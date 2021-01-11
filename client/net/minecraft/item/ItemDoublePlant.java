package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.world.ColorizerGrass;

public class ItemDoublePlant extends ItemMultiTexture {
   public ItemDoublePlant(Block var1, Block var2, Function<ItemStack, String> var3) {
      super(var1, var2, var3);
   }

   public int func_82790_a(ItemStack var1, int var2) {
      BlockDoublePlant.EnumPlantType var3 = BlockDoublePlant.EnumPlantType.func_176938_a(var1.func_77960_j());
      return var3 != BlockDoublePlant.EnumPlantType.GRASS && var3 != BlockDoublePlant.EnumPlantType.FERN ? super.func_82790_a(var1, var2) : ColorizerGrass.func_77480_a(0.5D, 1.0D);
   }
}
