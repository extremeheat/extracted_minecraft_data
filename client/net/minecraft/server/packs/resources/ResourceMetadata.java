package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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
   IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> EMPTY;

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

   default ResourceMetadata copySections(Collection<MetadataSectionSerializer<?>> var1) {
      ResourceMetadata.Builder var2 = new ResourceMetadata.Builder();

      for (MetadataSectionSerializer var4 : var1) {
         this.copySection(var2, var4);
      }

      return var2.build();
   }

   private <T> void copySection(ResourceMetadata.Builder var1, MetadataSectionSerializer<T> var2) {
      this.<Object>getSection(var2).ifPresent(var2x -> var1.put(var2, var2x));
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableMap.Builder<MetadataSectionSerializer<?>, Object> map = ImmutableMap.builder();

      public Builder() {
         super();
      }

      public <T> ResourceMetadata.Builder put(MetadataSectionSerializer<T> var1, T var2) {
         this.map.put(var1, var2);
         return this;
      }

      public ResourceMetadata build() {
         final ImmutableMap var1 = this.map.build();
         return var1.isEmpty() ? ResourceMetadata.EMPTY : new ResourceMetadata() {
            @Override
            public <T> Optional<T> getSection(MetadataSectionSerializer<T> var1x) {
               return Optional.ofNullable((T)var1.get(var1x));
            }
         };
      }
   }
}
