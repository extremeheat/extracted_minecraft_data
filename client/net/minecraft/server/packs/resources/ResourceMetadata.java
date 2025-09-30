package net.minecraft.server.packs.resources;

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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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

   default <T> Optional<MetadataSectionType.WithValue<T>> getTypedSection(MetadataSectionType<T> var1) {
      Optional var10000 = this.getSection(var1);
      Objects.requireNonNull(var1);
      return var10000.map(var1::withValue);
   }

   default List<MetadataSectionType.WithValue<?>> getTypedSections(Collection<MetadataSectionType<?>> var1) {
      return (List)var1.stream().map(this::getTypedSection).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }
}
