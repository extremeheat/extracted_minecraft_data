package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      @Override
      public <T> Optional<T> getSection(MetadataSectionSerializer<T> var1) {
         return Optional.empty();
      }
   };

   static ResourceMetadata fromJsonStream(InputStream var0) throws IOException {
      ResourceMetadata var3;
      try (BufferedReader var1 = new BufferedReader(new InputStreamReader(var0, StandardCharsets.UTF_8))) {
         final JsonObject var2 = GsonHelper.parse(var1);
         var3 = new ResourceMetadata() {
            @Override
            public <T> Optional<T> getSection(MetadataSectionSerializer<T> var1) {
               String var2x = var1.getMetadataSectionName();
               return var2.has(var2x) ? Optional.of((T)var1.fromJson(GsonHelper.getAsJsonObject(var2, var2x))) : Optional.empty();
            }
         };
      }

      return var3;
   }

   <T> Optional<T> getSection(MetadataSectionSerializer<T> var1);
}
