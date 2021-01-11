package net.minecraft.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class StringTranslate {
   private static final Pattern field_111053_a = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final Splitter field_135065_b = Splitter.on('=').limit(2);
   private static StringTranslate field_74817_a = new StringTranslate();
   private final Map<String, String> field_74816_c = Maps.newHashMap();
   private long field_150511_e;

   public StringTranslate() {
      super();

      try {
         InputStream var1 = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
         Iterator var2 = IOUtils.readLines(var1, Charsets.UTF_8).iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            if (!var3.isEmpty() && var3.charAt(0) != '#') {
               String[] var4 = (String[])Iterables.toArray(field_135065_b.split(var3), String.class);
               if (var4 != null && var4.length == 2) {
                  String var5 = var4[0];
                  String var6 = field_111053_a.matcher(var4[1]).replaceAll("%$1s");
                  this.field_74816_c.put(var5, var6);
               }
            }
         }

         this.field_150511_e = System.currentTimeMillis();
      } catch (IOException var7) {
      }

   }

   static StringTranslate func_74808_a() {
      return field_74817_a;
   }

   public static synchronized void func_135063_a(Map<String, String> var0) {
      field_74817_a.field_74816_c.clear();
      field_74817_a.field_74816_c.putAll(var0);
      field_74817_a.field_150511_e = System.currentTimeMillis();
   }

   public synchronized String func_74805_b(String var1) {
      return this.func_135064_c(var1);
   }

   public synchronized String func_74803_a(String var1, Object... var2) {
      String var3 = this.func_135064_c(var1);

      try {
         return String.format(var3, var2);
      } catch (IllegalFormatException var5) {
         return "Format error: " + var3;
      }
   }

   private String func_135064_c(String var1) {
      String var2 = (String)this.field_74816_c.get(var1);
      return var2 == null ? var1 : var2;
   }

   public synchronized boolean func_94520_b(String var1) {
      return this.field_74816_c.containsKey(var1);
   }

   public long func_150510_c() {
      return this.field_150511_e;
   }
}
