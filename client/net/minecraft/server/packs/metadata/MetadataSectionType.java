package net.minecraft.server.packs.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;

public interface MetadataSectionType<T> extends MetadataSectionSerializer<T> {
   JsonObject toJson(T var1);

   static <T> MetadataSectionType<T> fromCodec(final String var0, final Codec<T> var1) {
      return new MetadataSectionType<T>() {
         @Override
         public String getMetadataSectionName() {
            return var0;
         }

         @Override
         public T fromJson(JsonObject var1x) {
            return Util.getOrThrow(var1.parse(JsonOps.INSTANCE, var1x), JsonParseException::new);
         }

         @Override
         public JsonObject toJson(T var1x) {
            return ((JsonElement)Util.getOrThrow(var1.encodeStart(JsonOps.INSTANCE, var1x), IllegalArgumentException::new)).getAsJsonObject();
         }
      };
   }
}
