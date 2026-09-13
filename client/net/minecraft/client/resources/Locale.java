package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
   private static final Splitter field_135030_b = Splitter.on('=').limit(2);
   private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   Map field_135032_a = Maps.newHashMap();
   private boolean field_135029_d;

   public Locale() {
      super();
   }

   public synchronized void func_135022_a(IResourceManager var1, List var2) {
      this.field_135032_a.clear();

      for(String var4 : var2) {
         String var5 = String.format("lang/%s.lang", var4);

         for(String var7 : var1.func_135055_a()) {
            try {
               this.func_135028_a(var1.func_135056_b(new ResourceLocation(var7, var5)));
            } catch (IOException var9) {
            }
         }
      }

      this.func_135024_b();
   }

   public boolean func_135025_a() {
      return this.field_135029_d;
   }

   private void func_135024_b() {
      this.field_135029_d = false;
      int var1 = 0;
      int var2 = 0;

      for(String var4 : this.field_135032_a.values()) {
         int var5 = var4.length();
         var2 += var5;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var4.charAt(var6) >= 256) {
               ++var1;
            }
         }
      }

      float var7 = (float)var1 / (float)var2;
      this.field_135029_d = (double)var7 > 0.1;
   }

   private void func_135028_a(List var1) {
      for(IResource var3 : var1) {
         this.func_135021_a(var3.func_110527_b());
      }
   }

   private void func_135021_a(InputStream var1) {
      for(String var3 : IOUtils.readLines(var1, Charsets.UTF_8)) {
         if (!var3.isEmpty() && var3.charAt(0) != '#') {
            String[] var4 = (String[])Iterables.toArray(field_135030_b.split(var3), String.class);
            if (var4 != null && var4.length == 2) {
               String var5 = var4[0];
               String var6 = field_135031_c.matcher(var4[1]).replaceAll("%$1s");
               this.field_135032_a.put(var5, var6);
            }
         }
      }
   }

   private String func_135026_c(String var1) {
      String var2 = (String)this.field_135032_a.get(var1);
      return var2 == null ? var1 : var2;
   }

   public String func_135023_a(String var1, Object[] var2) {
      String var3 = this.func_135026_c(var1);

      try {
         return String.format(var3, var2);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }
}
