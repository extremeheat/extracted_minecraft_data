package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map.Entry;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer {
   public LanguageMetadataSectionSerializer() {
      super();
   }

   public LanguageMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) {
      JsonObject var4 = var1.getAsJsonObject();
      HashSet var5 = Sets.newHashSet();

      for(Entry var7 : var4.entrySet()) {
         String var8 = (String)var7.getKey();
         JsonObject var9 = JsonUtils.func_151210_l((JsonElement)var7.getValue(), "language");
         String var10 = JsonUtils.func_151200_h(var9, "region");
         String var11 = JsonUtils.func_151200_h(var9, "name");
         boolean var12 = JsonUtils.func_151209_a(var9, "bidirectional", false);
         if (var10.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var8 + "'->region: empty value");
         }

         if (var11.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var8 + "'->name: empty value");
         }

         if (!var5.add(new Language(var8, var10, var11, var12))) {
            throw new JsonParseException("Duplicate language->'" + var8 + "' defined");
         }
      }

      return new LanguageMetadataSection(var5);
   }

   @Override
   public String func_110483_a() {
      return "language";
   }
}
