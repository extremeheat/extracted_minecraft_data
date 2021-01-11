package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
   public TextureMetadataSectionSerializer() {
      super();
   }

   public TextureMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      boolean var5 = JsonUtils.func_151209_a(var4, "blur", false);
      boolean var6 = JsonUtils.func_151209_a(var4, "clamp", false);
      ArrayList var7 = Lists.newArrayList();
      if (var4.has("mipmaps")) {
         try {
            JsonArray var8 = var4.getAsJsonArray("mipmaps");

            for(int var9 = 0; var9 < var8.size(); ++var9) {
               JsonElement var10 = var8.get(var9);
               if (var10.isJsonPrimitive()) {
                  try {
                     var7.add(var10.getAsInt());
                  } catch (NumberFormatException var12) {
                     throw new JsonParseException("Invalid texture->mipmap->" + var9 + ": expected number, was " + var10, var12);
                  }
               } else if (var10.isJsonObject()) {
                  throw new JsonParseException("Invalid texture->mipmap->" + var9 + ": expected number, was " + var10);
               }
            }
         } catch (ClassCastException var13) {
            throw new JsonParseException("Invalid texture->mipmaps: expected array, was " + var4.get("mipmaps"), var13);
         }
      }

      return new TextureMetadataSection(var5, var6, var7);
   }

   public String func_110483_a() {
      return "texture";
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return this.deserialize(var1, var2, var3);
   }
}
