package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private static final Logger field_151234_b = LogManager.getLogger();
   private final List<String> field_76325_b = Lists.newArrayList();
   private final List<Long> field_76326_c = Lists.newArrayList();
   private boolean field_76327_a;
   private String field_76323_d = "";
   private final Map<String, Long> field_76324_e = Maps.newHashMap();
   private long field_199099_g;
   private int field_199100_h;

   public Profiler() {
      super();
   }

   public boolean func_199094_a() {
      return this.field_76327_a;
   }

   public void func_199098_b() {
      this.field_76327_a = false;
   }

   public long func_199097_c() {
      return this.field_199099_g;
   }

   public int func_199096_d() {
      return this.field_199100_h;
   }

   public void func_199095_a(int var1) {
      if (!this.field_76327_a) {
         this.field_76327_a = true;
         this.field_76324_e.clear();
         this.field_76323_d = "";
         this.field_76325_b.clear();
         this.field_199100_h = var1;
         this.field_199099_g = Util.func_211178_c();
      }
   }

   public void func_76320_a(String var1) {
      if (this.field_76327_a) {
         if (!this.field_76323_d.isEmpty()) {
            this.field_76323_d = this.field_76323_d + ".";
         }

         this.field_76323_d = this.field_76323_d + var1;
         this.field_76325_b.add(this.field_76323_d);
         this.field_76326_c.add(Util.func_211178_c());
      }
   }

   public void func_194340_a(Supplier<String> var1) {
      if (this.field_76327_a) {
         this.func_76320_a((String)var1.get());
      }
   }

   public void func_76319_b() {
      if (this.field_76327_a && !this.field_76326_c.isEmpty()) {
         long var1 = Util.func_211178_c();
         long var3 = (Long)this.field_76326_c.remove(this.field_76326_c.size() - 1);
         this.field_76325_b.remove(this.field_76325_b.size() - 1);
         long var5 = var1 - var3;
         if (this.field_76324_e.containsKey(this.field_76323_d)) {
            this.field_76324_e.put(this.field_76323_d, (Long)this.field_76324_e.get(this.field_76323_d) + var5);
         } else {
            this.field_76324_e.put(this.field_76323_d, var5);
         }

         if (var5 > 100000000L) {
            field_151234_b.warn("Something's taking too long! '{}' took aprox {} ms", this.field_76323_d, (double)var5 / 1000000.0D);
         }

         this.field_76323_d = this.field_76325_b.isEmpty() ? "" : (String)this.field_76325_b.get(this.field_76325_b.size() - 1);
      }
   }

   public List<Profiler.Result> func_76321_b(String var1) {
      long var3 = this.field_76324_e.containsKey("root") ? (Long)this.field_76324_e.get("root") : 0L;
      long var5 = this.field_76324_e.containsKey(var1) ? (Long)this.field_76324_e.get(var1) : -1L;
      ArrayList var7 = Lists.newArrayList();
      if (!var1.isEmpty()) {
         var1 = var1 + ".";
      }

      long var8 = 0L;
      Iterator var10 = this.field_76324_e.keySet().iterator();

      while(var10.hasNext()) {
         String var11 = (String)var10.next();
         if (var11.length() > var1.length() && var11.startsWith(var1) && var11.indexOf(".", var1.length() + 1) < 0) {
            var8 += (Long)this.field_76324_e.get(var11);
         }
      }

      float var20 = (float)var8;
      if (var8 < var5) {
         var8 = var5;
      }

      if (var3 < var8) {
         var3 = var8;
      }

      Iterator var21 = this.field_76324_e.keySet().iterator();

      String var12;
      while(var21.hasNext()) {
         var12 = (String)var21.next();
         if (var12.length() > var1.length() && var12.startsWith(var1) && var12.indexOf(".", var1.length() + 1) < 0) {
            long var13 = (Long)this.field_76324_e.get(var12);
            double var15 = (double)var13 * 100.0D / (double)var8;
            double var17 = (double)var13 * 100.0D / (double)var3;
            String var19 = var12.substring(var1.length());
            var7.add(new Profiler.Result(var19, var15, var17));
         }
      }

      var21 = this.field_76324_e.keySet().iterator();

      while(var21.hasNext()) {
         var12 = (String)var21.next();
         this.field_76324_e.put(var12, (Long)this.field_76324_e.get(var12) * 999L / 1000L);
      }

      if ((float)var8 > var20) {
         var7.add(new Profiler.Result("unspecified", (double)((float)var8 - var20) * 100.0D / (double)var8, (double)((float)var8 - var20) * 100.0D / (double)var3));
      }

      Collections.sort(var7);
      var7.add(0, new Profiler.Result(var1, 100.0D, (double)var8 * 100.0D / (double)var3));
      return var7;
   }

   public void func_76318_c(String var1) {
      this.func_76319_b();
      this.func_76320_a(var1);
   }

   public void func_194339_b(Supplier<String> var1) {
      this.func_76319_b();
      this.func_194340_a(var1);
   }

   public String func_76322_c() {
      return this.field_76325_b.isEmpty() ? "[UNKNOWN]" : (String)this.field_76325_b.get(this.field_76325_b.size() - 1);
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public double field_76332_a;
      public double field_76330_b;
      public String field_76331_c;

      public Result(String var1, double var2, double var4) {
         super();
         this.field_76331_c = var1;
         this.field_76332_a = var2;
         this.field_76330_b = var4;
      }

      public int compareTo(Profiler.Result var1) {
         if (var1.field_76332_a < this.field_76332_a) {
            return -1;
         } else {
            return var1.field_76332_a > this.field_76332_a ? 1 : var1.field_76331_c.compareTo(this.field_76331_c);
         }
      }

      public int func_76329_a() {
         return (this.field_76331_c.hashCode() & 11184810) + 4473924;
      }

      // $FF: synthetic method
      public int compareTo(Object var1) {
         return this.compareTo((Profiler.Result)var1);
      }
   }
}
