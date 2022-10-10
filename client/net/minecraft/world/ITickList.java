package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public interface ITickList<T> {
   boolean func_205359_a(BlockPos var1, T var2);

   default void func_205360_a(BlockPos var1, T var2, int var3) {
      this.func_205362_a(var1, var2, var3, TickPriority.NORMAL);
   }

   void func_205362_a(BlockPos var1, T var2, int var3, TickPriority var4);

   boolean func_205361_b(BlockPos var1, T var2);
}
