package net.minecraft.server.packs.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      public <T> Optional<T> getSection(MetadataSectionSerializer<T> var1) {
         return Optional.empty();
      }
   };
   IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> {
      return EMPTY;
   };

   static ResourceMetadata fromJsonStream(InputStream var0) throws IOException {
      BufferedReader var1 = new BufferedReader(new InputStreamReader(var0, StandardCharsets.UTF_8));

      ResourceMetadata var3;
      try {
         final JsonObject var2 = GsonHelper.parse((Reader)var1);
         var3 = new ResourceMetadata() {
            public <T> Optional<T> getSection(MetadataSectionSerializer<T> var1) {
               String var2x = var1.getMetadataSectionName();
               return var2.has(var2x) ? Optional.of(var1.fromJson(GsonHelper.getAsJsonObject(var2, var2x))) : Optional.empty();
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

   <T> Optional<T> getSection(MetadataSectionSerializer<T> var1);

   default ResourceMetadata copySections(Collection<MetadataSectionSerializer<?>> var1) {
      Builder var2 = new Builder();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         MetadataSectionSerializer var4 = (MetadataSectionSerializer)var3.next();
         this.copySection(var2, var4);
      }

      return var2.build();
   }

   private <T> void copySection(Builder var1, MetadataSectionSerializer<T> var2) {
      this.getSection(var2).ifPresent((var2x) -> {
         var1.put(var2, var2x);
      });
   }

   public static class Builder {
      private final ImmutableMap.Builder<MetadataSectionSerializer<?>, Object> map = ImmutableMap.builder();

      public Builder() {
         super();
      }

      public <T> Builder put(MetadataSectionSerializer<T> var1, T var2) {
         this.map.put(var1, var2);
         return this;
      }

      public ResourceMetadata build() {
         final ImmutableMap var1 = this.map.build();
         return var1.isEmpty() ? ResourceMetadata.EMPTY : new ResourceMetadata(this) {
            public <T> Optional<T> getSection(MetadataSectionSerializer<T> var1x) {
               return Optional.ofNullable(var1.get(var1x));
            }
         };
      }
   }
}
