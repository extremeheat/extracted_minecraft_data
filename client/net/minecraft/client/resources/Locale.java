package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Locale {
   private static final Gson field_200700_b = new Gson();
   private static final Logger field_199755_b = LogManager.getLogger();
   private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   Map<String, String> field_135032_a = Maps.newHashMap();

   public Locale() {
      super();
   }

   public synchronized void func_195811_a(IResourceManager var1, List<String> var2) {
      this.field_135032_a.clear();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         String var5 = String.format("lang/%s.json", var4);
         Iterator var6 = var1.func_199001_a().iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();

            try {
               ResourceLocation var8 = new ResourceLocation(var7, var5);
               this.func_135028_a(var1.func_199004_b(var8));
            } catch (FileNotFoundException var9) {
            } catch (Exception var10) {
               field_199755_b.warn("Skipped language file: {}:{} ({})", var7, var5, var10.toString());
            }
         }
      }

   }

   private void func_135028_a(List<IResource> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         IResource var3 = (IResource)var2.next();
         InputStream var4 = var3.func_199027_b();

         try {
            this.func_135021_a(var4);
         } finally {
            IOUtils.closeQuietly(var4);
         }
      }

   }

   private void func_135021_a(InputStream var1) {
      JsonElement var2 = (JsonElement)field_200700_b.fromJson(new InputStreamReader(var1, StandardCharsets.UTF_8), JsonElement.class);
      JsonObject var3 = JsonUtils.func_151210_l(var2, "strings");
      Iterator var4 = var3.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         String var6 = field_135031_c.matcher(JsonUtils.func_151206_a((JsonElement)var5.getValue(), (String)var5.getKey())).replaceAll("%$1s");
         this.field_135032_a.put(var5.getKey(), var6);
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

   public boolean func_188568_a(String var1) {
      return this.field_135032_a.containsKey(var1);
   }
}
