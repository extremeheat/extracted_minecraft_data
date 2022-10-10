package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;

public class LanguageMetadataSectionSerializer implements IMetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSectionSerializer() {
      super();
   }

   public LanguageMetadataSection func_195812_a(JsonObject var1) {
      HashSet var2 = Sets.newHashSet();
      Iterator var3 = var1.entrySet().iterator();

      String var5;
      String var7;
      String var8;
      boolean var9;
      do {
         if (!var3.hasNext()) {
            return new LanguageMetadataSection(var2);
         }

         Entry var4 = (Entry)var3.next();
         var5 = (String)var4.getKey();
         if (var5.length() > 16) {
            throw new JsonParseException("Invalid language->'" + var5 + "': language code must not be more than " + 16 + " characters long");
         }

         JsonObject var6 = JsonUtils.func_151210_l((JsonElement)var4.getValue(), "language");
         var7 = JsonUtils.func_151200_h(var6, "region");
         var8 = JsonUtils.func_151200_h(var6, "name");
         var9 = JsonUtils.func_151209_a(var6, "bidirectional", false);
         if (var7.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var5 + "'->region: empty value");
         }

         if (var8.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var5 + "'->name: empty value");
         }
      } while(var2.add(new Language(var5, var7, var8, var9)));

      throw new JsonParseException("Duplicate language->'" + var5 + "' defined");
   }

   public String func_110483_a() {
      return "language";
   }

   // $FF: synthetic method
   public Object func_195812_a(JsonObject var1) {
      return this.func_195812_a(var1);
   }
}
