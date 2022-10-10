package net.minecraft.util.text.translation;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageMap {
   private static final Logger field_201045_a = LogManager.getLogger();
   private static final Pattern field_111053_a = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final LanguageMap field_197636_c = new LanguageMap();
   private final Map<String, String> field_74816_c = Maps.newHashMap();
   private long field_150511_e;

   public LanguageMap() {
      super();

      try {
         InputStream var1 = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
         JsonElement var2 = (JsonElement)(new Gson()).fromJson(new InputStreamReader(var1, StandardCharsets.UTF_8), JsonElement.class);
         JsonObject var3 = JsonUtils.func_151210_l(var2, "strings");
         Iterator var4 = var3.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            String var6 = field_111053_a.matcher(JsonUtils.func_151206_a((JsonElement)var5.getValue(), (String)var5.getKey())).replaceAll("%$1s");
            this.field_74816_c.put(var5.getKey(), var6);
         }

         this.field_150511_e = Util.func_211177_b();
      } catch (JsonParseException var7) {
         field_201045_a.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", var7);
      }

   }

   public static LanguageMap func_74808_a() {
      return field_197636_c;
   }

   public static synchronized void func_135063_a(Map<String, String> var0) {
      field_197636_c.field_74816_c.clear();
      field_197636_c.field_74816_c.putAll(var0);
      field_197636_c.field_150511_e = Util.func_211177_b();
   }

   public synchronized String func_74805_b(String var1) {
      return this.func_135064_c(var1);
   }

   private String func_135064_c(String var1) {
      String var2 = (String)this.field_74816_c.get(var1);
      return var2 == null ? var1 : var2;
   }

   public synchronized boolean func_210813_b(String var1) {
      return this.field_74816_c.containsKey(var1);
   }

   public long func_150510_c() {
      return this.field_150511_e;
   }
}
