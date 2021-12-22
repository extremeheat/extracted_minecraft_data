package net.minecraft.util.profiling.jfr;

import com.google.common.math.Quantiles;
import com.google.common.math.Quantiles.ScaleAndIndexes;
import it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;

public class Percentiles {
   public static final ScaleAndIndexes DEFAULT_INDEXES = Quantiles.scale(100).indexes(new int[]{50, 75, 90, 99});

   private Percentiles() {
      super();
   }

   public static Map<Integer, Double> evaluate(long[] var0) {
      return var0.length == 0 ? Map.of() : sorted(DEFAULT_INDEXES.compute(var0));
   }

   public static Map<Integer, Double> evaluate(double[] var0) {
      return var0.length == 0 ? Map.of() : sorted(DEFAULT_INDEXES.compute(var0));
   }

   private static Map<Integer, Double> sorted(Map<Integer, Double> var0) {
      Int2DoubleSortedMap var1 = (Int2DoubleSortedMap)Util.make(new Int2DoubleRBTreeMap(Comparator.reverseOrder()), (var1x) -> {
         var1x.putAll(var0);
      });
      return Int2DoubleSortedMaps.unmodifiable(var1);
   }
}
