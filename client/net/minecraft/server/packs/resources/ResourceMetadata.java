package net.minecraft.server.packs.resources;

import com.google.gson.JsonElement;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.GsonHelper;

public interface ResourceMetadata {
   ResourceMetadata EMPTY = new ResourceMetadata() {
      public <T> Optional<T> getSection(final MetadataSectionType<T> serializer) {
         return Optional.empty();
      }
   };
   IoSupplier<ResourceMetadata> EMPTY_SUPPLIER = () -> EMPTY;

   static ResourceMetadata fromJsonStream(final InputStream inputStream) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

      ResourceMetadata var3;
      try {
         final JsonObject metadata = GsonHelper.parse((Reader)reader);
         var3 = new ResourceMetadata() {
            public <T> Optional<T> getSection(final MetadataSectionType<T> serializer) {
               String name = serializer.name();
               JsonElement rawSection = metadata.get(name);
               if (rawSection != null) {
                  T section = (T)serializer.codec().parse(JsonOps.INSTANCE, rawSection).getOrThrow(JsonParseException::new);
                  return Optional.of(section);
               } else {
                  return Optional.empty();
               }
            }
         };
      } catch (Throwable var5) {
         try {
            reader.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      reader.close();
      return var3;
   }

   <T> Optional<T> getSection(MetadataSectionType<T> serializer);

   default <T> Optional<MetadataSectionType.WithValue<T>> getTypedSection(final MetadataSectionType<T> type) {
      Optional var10000 = this.getSection(type);
      Objects.requireNonNull(type);
      return var10000.map(type::withValue);
   }

   static <T> ResourceMetadata of(final MetadataSectionType<T> k, final T v) {
      return new MapBased(Map.of(k, v));
   }

   static <T1, T2> ResourceMetadata of(final MetadataSectionType<T1> k1, final T1 v1, final MetadataSectionType<T2> k2, final T2 v2) {
      return new MapBased(Map.of(k1, v1, k2, v2));
   }

   default List<MetadataSectionType.WithValue<?>> getTypedSections(final Collection<MetadataSectionType<?>> types) {
      return (List)types.stream().map(this::getTypedSection).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }

   public static class MapBased implements ResourceMetadata {
      private final Map<MetadataSectionType<?>, ?> values;

      private MapBased(final Map<MetadataSectionType<?>, ?> values) {
         super();
         this.values = values;
      }

      public <T> Optional<T> getSection(final MetadataSectionType<T> serializer) {
         return Optional.ofNullable(this.values.get(serializer));
      }
   }
}
