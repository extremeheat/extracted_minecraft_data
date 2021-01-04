package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DiscreteCubeMerger implements IndexMerger {
   private final CubePointRange result;
   private final int firstSize;
   private final int secondSize;
   private final int gcd;

   DiscreteCubeMerger(int var1, int var2) {
      super();
      this.result = new CubePointRange((int)Shapes.lcm(var1, var2));
      this.firstSize = var1;
      this.secondSize = var2;
      this.gcd = IntMath.gcd(var1, var2);
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer var1) {
      int var2 = this.firstSize / this.gcd;
      int var3 = this.secondSize / this.gcd;

      for(int var4 = 0; var4 <= this.result.size(); ++var4) {
         if (!var1.merge(var4 / var3, var4 / var2, var4)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.result;
   }
}
