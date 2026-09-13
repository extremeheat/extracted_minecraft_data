package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class BlockObsidian extends BlockStone {
   public BlockObsidian() {
      super();
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150343_Z);
   }

   @Override
   public MapColor func_149728_f(int var1) {
      return MapColor.field_151654_J;
   }
}
