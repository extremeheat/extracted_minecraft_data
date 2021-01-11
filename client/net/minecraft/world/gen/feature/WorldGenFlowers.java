package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenFlowers extends WorldGenerator {
   private BlockFlower field_150552_a;
   private IBlockState field_175915_b;

   public WorldGenFlowers(BlockFlower var1, BlockFlower.EnumFlowerType var2) {
      super();
      this.func_175914_a(var1, var2);
   }

   public void func_175914_a(BlockFlower var1, BlockFlower.EnumFlowerType var2) {
      this.field_150552_a = var1;
      this.field_175915_b = var1.func_176223_P().func_177226_a(var1.func_176494_l(), var2);
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      for(int var4 = 0; var4 < 64; ++var4) {
         BlockPos var5 = var3.func_177982_a(var2.nextInt(8) - var2.nextInt(8), var2.nextInt(4) - var2.nextInt(4), var2.nextInt(8) - var2.nextInt(8));
         if (var1.func_175623_d(var5) && (!var1.field_73011_w.func_177495_o() || var5.func_177956_o() < 255) && this.field_150552_a.func_180671_f(var1, var5, this.field_175915_b)) {
            var1.func_180501_a(var5, this.field_175915_b, 2);
         }
      }

      return true;
   }
}
