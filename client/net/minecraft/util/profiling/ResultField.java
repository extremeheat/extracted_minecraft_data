package net.minecraft.util.profiling;

public final class ResultField implements Comparable<ResultField> {
   public final double percentage;
   public final double globalPercentage;
   public final long count;
   public final String name;

   public ResultField(String var1, double var2, double var4, long var6) {
      super();
      this.name = var1;
      this.percentage = var2;
      this.globalPercentage = var4;
      this.count = var6;
   }

   public int compareTo(ResultField var1) {
      if (var1.percentage < this.percentage) {
         return -1;
      } else {
         return var1.percentage > this.percentage ? 1 : var1.name.compareTo(this.name);
      }
   }

   public int getColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }

   // $FF: synthetic method
   public int compareTo(final Object var1) {
      return this.compareTo((ResultField)var1);
   }
}
