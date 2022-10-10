package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IDoubleListMerger {
   DoubleList func_212435_a();

   boolean func_197855_a(IDoubleListMerger.Consumer var1);

   public interface Consumer {
      boolean merge(int var1, int var2, int var3);
   }
}
