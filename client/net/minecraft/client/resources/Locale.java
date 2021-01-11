package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale {
   private static final Splitter field_135030_b = Splitter.on('=').limit(2);
   private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   Map<String, String> field_135032_a = Maps.newHashMap();
   private boolean field_135029_d;

   public Locale() {
      super();
   }

   public synchronized void func_135022_a(IResourceManager var1, List<String> var2) {
      this.field_135032_a.clear();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String var5 = String.format("lang/%s.lang", var4);
         Iterator var6 = var1.func_135055_a().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();

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
      Iterator var3 = this.field_135032_a.values().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         int var5 = var4.length();
         var2 += var5;

         for(int var6 = 0; var6 < var5; ++var6) {
            if (var4.charAt(var6) >= 256) {
               ++var1;
            }
         }
      }

      float var7 = (float)var1 / (float)var2;
      this.field_135029_d = (double)var7 > 0.1D;
   }

   private void func_135028_a(List<IResource> var1) throws IOException {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         IResource var3 = (IResource)var2.next();
         InputStream var4 = var3.func_110527_b();

         try {
            this.func_135021_a(var4);
         } finally {
            IOUtils.closeQuietly(var4);
         }
      }

   }

   private void func_135021_a(InputStream var1) throws IOException {
      Iterator var2 = IOUtils.readLines(var1, Charsets.UTF_8).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
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
