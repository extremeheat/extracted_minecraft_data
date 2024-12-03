package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      public <T> Optional<T> getSection(MetadataSectionType<T> var1) {
         return Optional.empty();
      }
   };
   IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> EMPTY;

   static ResourceMetadata fromJsonStream(InputStream var0) throws IOException {
      BufferedReader var1 = new BufferedReader(new InputStreamReader(var0, StandardCharsets.UTF_8));

      ResourceMetadata var3;
      try {
         final JsonObject var2 = GsonHelper.parse((Reader)var1);
         var3 = new ResourceMetadata() {
            public <T> Optional<T> getSection(MetadataSectionType<T> var1) {
               String var2x = var1.name();
               if (var2.has(var2x)) {
                  Object var3 = var1.codec().parse(JsonOps.INSTANCE, var2.get(var2x)).getOrThrow(JsonParseException::new);
                  return Optional.of(var3);
               } else {
                  return Optional.empty();
               }
            }
         };
      } catch (Throwable var5) {
         try {
            var1.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      var1.close();
      return var3;
   }

   <T> Optional<T> getSection(MetadataSectionType<T> var1);

   default ResourceMetadata copySections(Collection<MetadataSectionType<?>> var1) {
      Builder var2 = new Builder();

      for(MetadataSectionType var4 : var1) {
         this.copySection(var2, var4);
      }

      return var2.build();
   }

   private <T> void copySection(Builder var1, MetadataSectionType<T> var2) {
      this.getSection(var2).ifPresent((var2x) -> var1.put(var2, var2x));
   }

   public static class Builder {
      private final ImmutableMap.Builder<MetadataSectionType<?>, Object> map = ImmutableMap.builder();

      public Builder() {
         super();
      }

      public <T> Builder put(MetadataSectionType<T> var1, T var2) {
         this.map.put(var1, var2);
         return this;
      }

      public ResourceMetadata build() {
         final ImmutableMap var1 = this.map.build();
         return var1.isEmpty() ? ResourceMetadata.EMPTY : new ResourceMetadata() {
            public <T> Optional<T> getSection(MetadataSectionType<T> var1x) {
               return Optional.ofNullable(var1.get(var1x));
            }
         };
      }
   }
}
