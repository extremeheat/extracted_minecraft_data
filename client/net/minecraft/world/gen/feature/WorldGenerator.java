package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenerator {
   private final boolean field_76488_a;

   public WorldGenerator() {
      this(false);
   }

   public WorldGenerator(boolean var1) {
      super();
      this.field_76488_a = var1;
   }

   public abstract boolean func_180709_b(World var1, Random var2, BlockPos var3);

   public void func_175904_e() {
   }

   protected void func_175903_a(World var1, BlockPos var2, IBlockState var3) {
      if (this.field_76488_a) {
         var1.func_180501_a(var2, var3, 3);
      } else {
         var1.func_180501_a(var2, var3, 2);
      }

   }
}
