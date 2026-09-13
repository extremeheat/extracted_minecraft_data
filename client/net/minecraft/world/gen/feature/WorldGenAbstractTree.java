package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator {
   public WorldGenAbstractTree(boolean var1) {
      super(var1);
   }

   protected boolean func_150523_a(Block var1) {
      return var1.func_149688_o() == Material.field_151579_a
         || var1.func_149688_o() == Material.field_151584_j
         || var1 == Blocks.field_150349_c
         || var1 == Blocks.field_150346_d
         || var1 == Blocks.field_150364_r
         || var1 == Blocks.field_150363_s
         || var1 == Blocks.field_150345_g
         || var1 == Blocks.field_150395_bd;
   }

   public void func_150524_b(World var1, Random var2, int var3, int var4, int var5) {
   }
}
