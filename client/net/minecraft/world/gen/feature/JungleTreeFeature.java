package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;

public class JungleTreeFeature extends TreeFeature {
   public JungleTreeFeature(boolean var1, int var2, IBlockState var3, IBlockState var4, boolean var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected int func_208534_a(Random var1) {
      return this.field_76533_a + var1.nextInt(7);
   }
}
