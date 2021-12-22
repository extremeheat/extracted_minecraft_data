package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DiscreteCubeMerger implements IndexMerger {
   private final CubePointRange result;
   private final int firstDiv;
   private final int secondDiv;

   DiscreteCubeMerger(int var1, int var2) {
      super();
      this.result = new CubePointRange((int)Shapes.lcm(var1, var2));
      int var3 = IntMath.gcd(var1, var2);
      this.firstDiv = var1 / var3;
      this.secondDiv = var2 / var3;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.result.size() - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (!var1.merge(var3 / this.secondDiv, var3 / this.firstDiv, var3)) {
            return false;
         }
      }

      return true;
   }

   public int size() {
      return this.result.size();
   }

   public DoubleList getList() {
      return this.result;
   }
}
