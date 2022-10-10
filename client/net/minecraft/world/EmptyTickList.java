package net.minecraft.world;

import net.minecraft.util.math.BlockPos;

public class EmptyTickList<T> implements ITickList<T> {
   private static final EmptyTickList field_205389_a = new EmptyTickList();

   public EmptyTickList() {
      super();
   }

   public static <T> EmptyTickList<T> func_205388_a() {
      return field_205389_a;
   }

   public boolean func_205359_a(BlockPos var1, T var2) {
      return false;
   }

   public void func_205360_a(BlockPos var1, T var2, int var3) {
   }

   public void func_205362_a(BlockPos var1, T var2, int var3, TickPriority var4) {
   }

   public boolean func_205361_b(BlockPos var1, T var2) {
      return false;
   }
}
