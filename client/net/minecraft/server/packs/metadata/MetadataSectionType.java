package net.minecraft.server.packs.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

public interface MetadataSectionType<T> extends MetadataSectionSerializer<T> {
   JsonObject toJson(T var1);

   static <T> MetadataSectionType<T> fromCodec(final String var0, final Codec<T> var1) {
      return new MetadataSectionType<T>() {
         public String getMetadataSectionName() {
            return var0;
         }

         public T fromJson(JsonObject var1x) {
            return var1.parse(JsonOps.INSTANCE, var1x).getOrThrow(JsonParseException::new);
         }

         public JsonObject toJson(T var1x) {
            return ((JsonElement)var1.encodeStart(JsonOps.INSTANCE, var1x).getOrThrow(IllegalArgumentException::new)).getAsJsonObject();
         }
      };
   }
}
