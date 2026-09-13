package net.minecraft.profiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private static final Logger field_151234_b = LogManager.getLogger();
   private final List field_76325_b = new ArrayList();
   private final List field_76326_c = new ArrayList();
   public boolean field_76327_a;
   private String field_76323_d = "";
   private final Map field_76324_e = new HashMap();

   public Profiler() {
      super();
   }

   public void func_76317_a() {
      this.field_76324_e.clear();
      this.field_76323_d = "";
      this.field_76325_b.clear();
   }

   public void func_76320_a(String var1) {
      if (this.field_76327_a) {
         if (this.field_76323_d.length() > 0) {
            this.field_76323_d = this.field_76323_d + ".";
         }

         this.field_76323_d = this.field_76323_d + var1;
         this.field_76325_b.add(this.field_76323_d);
         this.field_76326_c.add(System.nanoTime());
      }
   }

   public void func_76319_b() {
      if (this.field_76327_a) {
         long var1 = System.nanoTime();
         long var3 = this.field_76326_c.remove(this.field_76326_c.size() - 1);
         this.field_76325_b.remove(this.field_76325_b.size() - 1);
         long var5 = var1 - var3;
         if (this.field_76324_e.containsKey(this.field_76323_d)) {
            this.field_76324_e.put(this.field_76323_d, this.field_76324_e.get(this.field_76323_d) + var5);
         } else {
            this.field_76324_e.put(this.field_76323_d, var5);
         }

         if (var5 > 100000000L) {
            field_151234_b.warn("Something's taking too long! '" + this.field_76323_d + "' took aprox " + (double)var5 / 1000000.0 + " ms");
         }

         this.field_76323_d = !this.field_76325_b.isEmpty() ? (String)this.field_76325_b.get(this.field_76325_b.size() - 1) : "";
      }
   }

   public List func_76321_b(String var1) {
      if (!this.field_76327_a) {
         return null;
      } else {
         String var2 = var1;
         long var3 = this.field_76324_e.containsKey("root") ? this.field_76324_e.get("root") : 0L;
         long var5 = this.field_76324_e.containsKey(var1) ? this.field_76324_e.get(var1) : -1L;
         ArrayList var7 = new ArrayList();
         if (var1.length() > 0) {
            var1 = var1 + ".";
         }

         long var8 = 0L;

         for(String var11 : this.field_76324_e.keySet()) {
            if (var11.length() > var1.length() && var11.startsWith(var1) && var11.indexOf(".", var1.length() + 1) < 0) {
               var8 += this.field_76324_e.get(var11);
            }
         }

         float var20 = (float)var8;
         if (var8 < var5) {
            var8 = var5;
         }

         if (var3 < var8) {
            var3 = var8;
         }

         for(String var12 : this.field_76324_e.keySet()) {
            if (var12.length() > var1.length() && var12.startsWith(var1) && var12.indexOf(".", var1.length() + 1) < 0) {
               long var13 = this.field_76324_e.get(var12);
               double var15 = (double)var13 * 100.0 / (double)var8;
               double var17 = (double)var13 * 100.0 / (double)var3;
               String var19 = var12.substring(var1.length());
               var7.add(new Profiler$Result(var19, var15, var17));
            }
         }

         for(String var23 : this.field_76324_e.keySet()) {
            this.field_76324_e.put(var23, this.field_76324_e.get(var23) * 999L / 1000L);
         }

         if ((float)var8 > var20) {
            var7.add(
               new Profiler$Result("unspecified", (double)((float)var8 - var20) * 100.0 / (double)var8, (double)((float)var8 - var20) * 100.0 / (double)var3)
            );
         }

         Collections.sort(var7);
         var7.add(0, new Profiler$Result(var2, 100.0, (double)var8 * 100.0 / (double)var3));
         return var7;
      }
   }

   public void func_76318_c(String var1) {
      this.func_76319_b();
      this.func_76320_a(var1);
   }

   public String func_76322_c() {
      return this.field_76325_b.size() == 0 ? "[UNKNOWN]" : (String)this.field_76325_b.get(this.field_76325_b.size() - 1);
   }
}
