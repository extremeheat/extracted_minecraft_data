package net.minecraft.profiler;

public final class Profiler$Result implements Comparable {
   public double field_76332_a;
   public double field_76330_b;
   public String field_76331_c;

   public Profiler$Result(String var1, double var2, double var4) {
      super();
      this.field_76331_c = var1;
      this.field_76332_a = var2;
      this.field_76330_b = var4;
   }

   public int compareTo(Profiler$Result var1) {
      if (var1.field_76332_a < this.field_76332_a) {
         return -1;
      } else {
         return var1.field_76332_a > this.field_76332_a ? 1 : var1.field_76331_c.compareTo(this.field_76331_c);
      }
   }

   public int func_76329_a() {
      return (this.field_76331_c.hashCode() & 11184810) + 4473924;
   }
}
