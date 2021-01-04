package net.minecraft.client.resources.metadata.language;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.resources.language.Language;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class LanguageMetadataSectionSerializer implements MetadataSectionSerializer<LanguageMetadataSection> {
   public LanguageMetadataSectionSerializer() {
      super();
   }

   public LanguageMetadataSection fromJson(JsonObject var1) {
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

         JsonObject var6 = GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), "language");
         var7 = GsonHelper.getAsString(var6, "region");
         var8 = GsonHelper.getAsString(var6, "name");
         var9 = GsonHelper.getAsBoolean(var6, "bidirectional", false);
         if (var7.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var5 + "'->region: empty value");
         }

         if (var8.isEmpty()) {
            throw new JsonParseException("Invalid language->'" + var5 + "'->name: empty value");
         }
      } while(var2.add(new Language(var5, var7, var8, var9)));

      throw new JsonParseException("Duplicate language->'" + var5 + "' defined");
   }

   public String getMetadataSectionName() {
      return "language";
   }

   // $FF: synthetic method
   public Object fromJson(JsonObject var1) {
      return this.fromJson(var1);
   }
}
